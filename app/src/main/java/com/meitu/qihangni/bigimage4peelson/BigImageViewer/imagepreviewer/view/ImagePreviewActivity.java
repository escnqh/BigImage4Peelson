package com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.ImagePreview;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.bean.ImageInfo;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.glide.ImageLoader;

import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.tool.DownloadPictureUtil;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.tool.HandlerUtils;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.tool.Print;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.tool.ToastUtil;
import com.meitu.qihangni.bigimage4peelson.R;

import java.io.File;
import java.util.List;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

/**
 * 预览图片的Activity
 *
 * @author nqh 2018/8/22
 */
public class ImagePreviewActivity extends FragmentActivity implements Handler.Callback, View.OnClickListener {

    public static final String TAG = "ImagePreview";

    private Context mContext;

    private List<ImageInfo> mImageInfoList;
    private int mCurrentItem;// 当前显示的图片索引
    private String mDownloadFolderName = "";// 保存的文件夹名
    private boolean isShowDownButton;
    private boolean isShowOriginButton;

    private ImagePreviewAdapter mImagePreviewAdapter;
    private HackyViewPager mViewPager;
    private TextView tv_indicator;

    private String mCurrentItemOriginPathUrl = "";// 当前显示的原图链接
    private HandlerUtils.HandlerHolder mHandlerHolder;

    public static void activityStart(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, ImagePreviewActivity.class);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        mContext = this;
        mHandlerHolder = new HandlerUtils.HandlerHolder(this);

        mImageInfoList = ImagePreview.getInstance().getImageInfoList();
        mCurrentItem = ImagePreview.getInstance().getIndex();
        mDownloadFolderName = ImagePreview.getInstance().getFolderName();
        isShowDownButton = ImagePreview.getInstance().isShowDownButton();

        mCurrentItemOriginPathUrl = mImageInfoList.get(mCurrentItem).getOriginUrl();

        isShowOriginButton = ImagePreview.getInstance().isShowOriginButton(mCurrentItem);
        if (isShowOriginButton) {
            // 检查缓存是否存在
            checkCache(mCurrentItemOriginPathUrl);
        }

        mViewPager = findViewById(R.id.viewPager);
        tv_indicator = findViewById(R.id.tv_indicator);

        if (mImageInfoList.size() > 1) {
            tv_indicator.setVisibility(View.VISIBLE);
        } else {
            tv_indicator.setVisibility(View.GONE);
        }

        // 更新进度指示器
        tv_indicator.setText(
                String.format(getString(R.string.indicator), mCurrentItem + 1 + " ",
                        " " + mImageInfoList.size()));

        mImagePreviewAdapter = new ImagePreviewAdapter(this, mImageInfoList);
        mViewPager.setAdapter(mImagePreviewAdapter);
        mViewPager.setCurrentItem(mCurrentItem);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentItem = position;
                mCurrentItemOriginPathUrl = mImageInfoList.get(position).getOriginUrl();

                isShowOriginButton = ImagePreview.getInstance().isShowOriginButton(mCurrentItem);
                if (isShowOriginButton) {
                    // 检查缓存是否存在
                    checkCache(mCurrentItemOriginPathUrl);
                }
                // 更新进度指示器
                tv_indicator.setText(
                        String.format(getString(R.string.indicator), mCurrentItem + 1 + " ",
                                " " + mImageInfoList.size()));
            }
        });
    }

    /**
     * 下载当前图片到SD卡
     */
    private void downloadCurrentImg() {
        String path = Environment.getExternalStorageDirectory() + "/" + mDownloadFolderName + "/";
        DownloadPictureUtil.downloadPicture(mContext, mCurrentItemOriginPathUrl, path,
                System.currentTimeMillis() + ".jpeg");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 0) {// 点击查看原图按钮，开始加载原图
            final String path = mImageInfoList.get(mCurrentItem).getOriginUrl();
            Print.d(TAG, "handler == 0 path = " + path);
            visible();
//            Glide.with(mContext).load(path).into(new SimpleTarget<Drawable>() {
//                @Override
//                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//
//                }
//            });
        } else if (msg.what == 1) {// 加载完成
            Print.d(TAG, "handler == 1");
            Bundle bundle = (Bundle) msg.obj;
            String url = bundle.getString("url");
            gone();
            if (mCurrentItem == getRealIndexWithPath(url)) {
                mImagePreviewAdapter.loadOrigin(mImageInfoList.get(mCurrentItem));
            }
        } else if (msg.what == 2) {// 加载中
            Bundle bundle = (Bundle) msg.obj;
            String url = bundle.getString("url");
            int progress = bundle.getInt("progress");
            if (mCurrentItem == getRealIndexWithPath(url)) {
                visible();
            }
        }
        return true;
    }

    private int getRealIndexWithPath(String path) {
        for (int i = 0; i < mImageInfoList.size(); i++) {
            if (path.equalsIgnoreCase(mImageInfoList.get(i).getOriginUrl())) {
                return i;
            }
        }
        return 0;
    }

    private void checkCache(String url_) {
        gone();
        File cacheFile = ImageLoader.getGlideCacheFile(mContext, url_);
        if (cacheFile != null && cacheFile.exists()) {
            gone();
        } else {
            visible();
        }
    }

    public void onImageViewerClick(){

    }

    @Override
    public void onClick(View v) {
        Log.i("nqh", v.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    downloadCurrentImg();
                } else {
                    ToastUtil.getInstance()._short(mContext, "您拒绝了存储权限，下载失败！");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImagePreviewAdapter != null) {
            mImagePreviewAdapter.closePage();
        }
        ImagePreview.getInstance().reset();
    }

    private void gone() {
        mHandlerHolder.sendEmptyMessage(3);
    }

    private void visible() {
        mHandlerHolder.sendEmptyMessage(4);
    }
}