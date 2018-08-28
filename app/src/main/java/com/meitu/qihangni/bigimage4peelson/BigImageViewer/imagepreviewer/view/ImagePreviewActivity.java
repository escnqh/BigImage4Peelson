package com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.view;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        mImagePreview = (ImagePreview) getIntent().getSerializableExtra("ImagePreview");
        //获得一些初始化信息
        if (mImagePreview != null) {
            mCurrentItem = mImagePreview.getIndex();
        } else {
            mCurrentItem = 0;
        }
        //ViewPager及其相关配置
        mViewPager = findViewById(R.id.viewPager);
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
        finish();
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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