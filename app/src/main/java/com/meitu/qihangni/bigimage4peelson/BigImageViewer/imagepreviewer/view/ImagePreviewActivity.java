package com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.view;

import android.animation.Animator;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.AnimationHelper;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.ImagePreview;
import com.meitu.qihangni.bigimage4peelson.R;

/**
 * 预览图片的Activity
 *
 * @author nqh 2018/8/22
 */
public class ImagePreviewActivity extends FragmentActivity {
    public static final String TAG = "ImagePreview";
    private int mCurrentItem;// 当前显示的图片索引
    private ImagePreviewAdapter mImagePreviewAdapter;
    private HackyViewPager mViewPager;
    private ImagePreview mImagePreview;
    private boolean isDragable = true;
    private int mLocationX;
    private int mLocationY;
    private int mResourceHeight;
    private int mResourceWidth;
    private View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        mImagePreview = (ImagePreview) getIntent().getSerializableExtra("ImagePreview");
        //获得一些初始化信息
        if (mImagePreview != null) {
            mCurrentItem = mImagePreview.getIndex();
            mLocationX = mImagePreview.getLocationX();
            mLocationY = mImagePreview.getLocationY();
            mResourceHeight = mImagePreview.getResourceHeight();
            mResourceWidth = mImagePreview.getResourceWidth();
        } else {
            mCurrentItem = 0;
            mLocationX = 0;
            mLocationY = 0;
            mResourceHeight = 0;
            mResourceWidth = 0;
        }
        //ViewPager及其相关配置
        mViewPager = findViewById(R.id.viewPager);
        mRootView = findViewById(R.id.rootView);
        mImagePreviewAdapter = new ImagePreviewAdapter(this, mImagePreview);
        mViewPager.setAdapter(mImagePreviewAdapter);
        mViewPager.setCurrentItem(mCurrentItem);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentItem = position;
            }
        });

    }

    @Override
    public void onBackPressed() {
        checkfinish();
    }

    public void checkfinish() {
        if (isDragable) {
            mRootView.setAlpha(0);
            AnimationHelper.animationTarget(mViewPager, new RectF(mLocationX, mLocationY, mLocationX + mResourceWidth, mLocationY + mResourceHeight), 200, new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    finish();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        } else {
            finish();

        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    /**
     * 当内部的Imageview截获到单击时调用
     */
    public void onImageViewerClick(int position) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImagePreviewAdapter != null) {
            mImagePreviewAdapter.closePage();
        }
    }
}