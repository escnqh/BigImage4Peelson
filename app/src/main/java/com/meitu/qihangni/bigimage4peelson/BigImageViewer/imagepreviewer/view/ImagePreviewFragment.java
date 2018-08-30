package com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.view;

import android.animation.Animator;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.AnimationHelper;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.ImagePreview;
import com.meitu.qihangni.bigimage4peelson.R;

/**
 * @author nqh 2018/8/30
 */
public class ImagePreviewFragment extends Fragment {
    public static final String TAG = "ImagePreview";
    private int mCurrentItem;// 当前显示的图片索引
    private ImagePreviewAdapter mImagePreviewAdapter;
    private HackyViewPager mViewPager;
    private ImagePreview mImagePreview;
    private boolean isDragable = false;
    private int mLocationX;
    private int mLocationY;
    private int mResourceHeight;
    private int mResourceWidth;
    private View mRootView;

    public ImagePreviewFragment() {
    }

    public static ImagePreviewFragment newInstance(ImagePreview imagePreview) {
        ImagePreviewFragment imagePreviewFragment = new ImagePreviewFragment();
        Bundle args = new Bundle();
        args.putSerializable("ImagePreview", imagePreview);
        imagePreviewFragment.setArguments(args);
        return imagePreviewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mImagePreview = (ImagePreview) getArguments().getSerializable("ImagePreview");
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_preview, container, false);
        if (mImagePreview != null) {
            mCurrentItem = mImagePreview.getIndex();
            mLocationX = mImagePreview.getLocationX();
            isDragable = mImagePreview.getDragable();
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
        mViewPager = root.findViewById(R.id.viewPager);
        mRootView = root.findViewById(R.id.rootView);
        mImagePreviewAdapter = new ImagePreviewAdapter(this, mImagePreview);
        mViewPager.setAdapter(mImagePreviewAdapter);
        mViewPager.setCurrentItem(mCurrentItem);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentItem = position;
            }
        });
        return root;
    }

    /**
     * 当内部的Imageview截获到单击时调用
     */
    public void onImageViewerClick(int position) {

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
//                    finish();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        } else {
//            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImagePreviewAdapter != null) {
            mImagePreviewAdapter.closePage();
        }
    }
}
