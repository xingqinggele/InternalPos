package com.example.internalpos.mefragment.setup;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.internalpos.R;
import com.example.internalpos.base.BaseActivity;
import com.example.internalpos.cos.CosServiceFactory;
import com.example.internalpos.fragment.MeFragment;
import com.example.internalpos.homefragment.homeInvitepartners.HomeInvitePartnersActivity;
import com.example.internalpos.net.HttpRequest;
import com.example.internalpos.net.OkHttpException;
import com.example.internalpos.net.RequestParams;
import com.example.internalpos.net.ResponseCallback;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import com.tencent.cos.xml.transfer.TransferState;
import com.tencent.cos.xml.transfer.TransferStateListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 作者: qgl
 * 创建日期：2020/12/23
 * 描述:个人信息
 */
public class PersonalActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout iv_back;
    private RelativeLayout personal_mcode_line;  // 推荐界面
    private TextView user_name; // 用户姓名
    private TextView user_phone; // 用户手机号
    private TextView user_code; //  推荐号码
    private SimpleDraweeView me_head; //  用户头像
    //已经选择图片
    private List<LocalMedia> selectList = new ArrayList<>();
    //照片选择最大值
    private int maxSelectNum = 1;
    private final int PER_NUM = 10; // 选取图片时候inCode
    private COSXMLUploadTask cosxmlTask;
    private String folderName = "portrait";
    private CosXmlService cosXmlService;
    private TransferManager transferManager;

    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color,false).init();
        return R.layout.personal_activity;
    }

    @Override
    protected void initView() {
        //初始化存储桶控件
        cosXmlService = CosServiceFactory.getCosXmlService(this, "ap-beijing", getSecretId(), getSecretKey(), true);
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        transferManager = new TransferManager(cosXmlService, transferConfig);
        iv_back = findViewById(R.id.iv_back);
        personal_mcode_line = findViewById(R.id.personal_mcode_line);
        user_name = findViewById(R.id.user_name);
        user_phone = findViewById(R.id.user_phone);
        user_code = findViewById(R.id.user_code);
        me_head = findViewById(R.id.me_head);

    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
        personal_mcode_line.setOnClickListener(this);
        me_head.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        posDate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.personal_mcode_line:
                startActivity(new Intent(PersonalActivity.this, HomeInvitePartnersActivity.class));
                break;
            case R.id.me_head:
                initSelectImage(PER_NUM);
                break;
        }
    }

    // 获取用户个人信息
    public void posDate() {
        RequestParams params = new RequestParams();
        HttpRequest.getCurrent(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    JSONObject data = result.getJSONObject("data");
                    JSONObject merchantDetail = data.getJSONObject("merchantDetail");
                    JSONObject merchantBrief = data.getJSONObject("merchantBrief");
                    user_name.setText(merchantDetail.getString("merchIdcardName").substring(0,1) + "**");
                    user_phone.setText(merchantDetail.getString("merchBankMobile").substring(0, 3) + "****" + merchantDetail.getString("merchBankMobile").substring(merchantDetail.getString("merchBankMobile").length() - 4));
                    user_code.setText(merchantBrief.getString("merchCode"));
                    Uri imgurl=Uri.parse(data.getString("portrait"));
                    // 清除Fresco对这条验证码的缓存
                    ImagePipeline imagePipeline = Fresco.getImagePipeline();
                    imagePipeline.evictFromMemoryCache(imgurl);
                    imagePipeline.evictFromDiskCache(imgurl);
                    // combines above two lines
                    imagePipeline.evictFromCache(imgurl);
                    me_head.setImageURI(imgurl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(OkHttpException failuer) {
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });
    }


    /**
     * 选取照片初始化
     */
    private void initSelectImage(int resultCode) {
        selectList = new ArrayList<>();
        PictureSelector.create(PersonalActivity.this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .maxSelectNum(maxSelectNum)// 最大图片选择数量
                .minSelectNum(0)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
//                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                .compress(true)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .selectionMedia(selectList)// 是否传入已选图片
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .forResult(resultCode);//结果回调onActivityResult code
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            selectList = new ArrayList<>();
            switch (requestCode) {
                case PER_NUM:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    Uri uri = new Uri.Builder()
                            .scheme(UriUtil.LOCAL_FILE_SCHEME)
                            .path(selectList.get(0).getCompressPath())
                            .build();
                    me_head.setImageURI(uri);
                    upload(selectList.get(0).getCompressPath());
                    break;
            }
        }
    }


    //上传图片
    private void upload(String file_url) {
        if (TextUtils.isEmpty(file_url)) {
            return;
        }
        if (cosxmlTask == null) {
            File file = new File(file_url);
            String cosPath;
            if (TextUtils.isEmpty(folderName)) {
                cosPath = file.getName();
            } else {
                cosPath = folderName + File.separator + file.getName();
            }
            cosxmlTask = transferManager.upload(getBucketName(), cosPath, file_url, null);
            Log.e("参数-------》", getBucketName() + "----" + cosPath + "---" + file_url);
            cosxmlTask.setTransferStateListener(new TransferStateListener() {
                @Override
                public void onStateChanged(final TransferState state) {
                    // refreshUploadState(state);
                }
            });
            cosxmlTask.setCosXmlProgressListener(new CosXmlProgressListener() {
                @Override
                public void onProgress(final long complete, final long target) {
                    // refreshUploadProgress(complete, target);
                }
            });

            cosxmlTask.setCosXmlResultListener(new CosXmlResultListener() {
                @Override
                public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                    COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                    cosxmlTask = null;
                    setResult(RESULT_OK);
                    // 删除原来的头像，在存储桶里
                    // 上传服务器url
                    posHead(cOSXMLUploadTaskResult.accessUrl);
                }

                @Override
                public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                    if (cosxmlTask.getTaskState() != TransferState.PAUSED) {
                        cosxmlTask = null;
                        Log.e("1111", "上传失败");
                    }
                    exception.printStackTrace();
                    serviceException.printStackTrace();
                }
            });

        }
    }
    //更换头像
    public void posHead(String url){
        RequestParams params = new RequestParams();
        params.put("portraitUrl",url);
        HttpRequest.getUpdatePortrait(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                // 成功,通知我的，界面更新头像
                EventBus.getDefault().post(new MeFragment());
            }

            @Override
            public void onFailure(OkHttpException failuer) {
                Failuer(failuer.getEcode(),failuer.getEmsg());
            }
        });

    }
}
