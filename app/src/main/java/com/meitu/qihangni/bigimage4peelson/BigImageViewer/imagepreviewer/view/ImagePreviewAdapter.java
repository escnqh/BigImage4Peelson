package com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.view;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.DragContract;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.DragDirection;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.DragHelper;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.effect.DragImageToExit;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.ImagePreview;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.bean.ImageInfo;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.glide.ImageLoader;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.tool.ImageUtil;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.subscaleview.ImageSource;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.subscaleview.SubsamplingScaleImageView;
import com.meitu.qihangni.bigimage4peelson.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ViewPager的Adapter用来显示图片等操作
 */
public class ImagePreviewAdapter extends PagerAdapter {
    private static final String TAG = "ImagePreview";
    private ImagePreviewFragment mFragment;
    private List<ImageInfo> mImageInfo;
    private HashMap<String, SubsamplingScaleImageView> mImageHashMap = new HashMap<>();
    private String mFinalLoadUrl = "";// 最终加载的图片url
    private FrameLayout mDragLayout;
    private boolean isDrag2Exit = false;
    private boolean isClick2Exit = false;
    private ImagePreview mImagePreview;
    private float mDefaultScale = 0;

    public ImagePreviewAdapter(ImagePreviewFragment fragment, @NonNull ImagePreview imagePreview) {
        super();
        this.mImagePreview = imagePreview;
        this.mImageInfo = mImagePreview.getImageInfoList();
        this.mFragment = fragment;
        this.isDrag2Exit = mImagePreview.getDragable();
        this.isClick2Exit = mImagePreview.getClickToExit();
    }

    public void closePage() {
        try {
            if (mImageHashMap != null && mImageHashMap.size() > 0) {
                for (Object o : mImageHashMap.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    if (entry != null && entry.getValue() != null) {
                        ((SubsamplingScaleImageView) entry.getValue()).recycle();
                    }
                }
                mImageHashMap.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return mImageInfo.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        if (mFragment == null) {
            return container;
        }
        View convertView = View.inflate(mFragment.getContext(), R.layout.item_photoview, null);
        View rootView = mFragment.getView().findViewById(R.id.rootView);
        rootView.setBackground(mFragment.getResources().getDrawable(mImagePreview.getBackgroundcolor()));
        final ProgressBar progressBar = convertView.findViewById(R.id.progress_view);
        final SubsamplingScaleImageView imageView = new SubsamplingScaleImageView(mFragment.getContext());
        //设置拖拽
        DragHelper helper = new DragHelper.Builder(mFragment.getActivity())
                .onlyCreateView()
                .setCanDrag(isDrag2Exit)
                .setDragIntercept(new DragContract.DragIntercept() {
                    @Override
                    public boolean canDrag(@NonNull MotionEvent ev, int direction) {
                        //因为存在缩放精确值的问题所以这里只保留两位小数来判断图片的缩放状态
                        DecimalFormat df = new DecimalFormat("0.00");
                        if (direction == DragDirection.DOWN || direction <= DragDirection.UP) {
                            if (imageView.getCenter() != null && df.format(imageView.getCenter().y).equals(df.format(imageView.getHeight() / imageView.getScale() / 2))) {
                                //放大状况下拖拽到上边缘
                                return true;
                            }
                            if (imageView.getCenter() != null && df.format(imageView.getSHeight() - imageView.getCenter().y).equals(df.format(imageView.getHeight() / imageView.getScale() / 2))) {
                                //放大状况下拖拽到下边缘
                                return true;
                            }
                            if (df.format(imageView.getScale()).equals(df.format(imageView.getMinScale()))) {
                                //判断是否在初始状态
                                return true;
                            }
                        }
                        return false;
                    }
                })
                .addDragDirection(DragDirection.DOWN, new DragImageToExit(imageView, null, DragDirection.DOWN, rootView, mImagePreview.getLocationX(), mImagePreview.getLocationY(), mImagePreview.getResourceWidth(), mImagePreview.getResourceHeight()))
                .addDragDirection(DragDirection.UP, new DragImageToExit(imageView, null, DragDirection.DOWN, rootView, mImagePreview.getLocationX(), mImagePreview.getLocationY(), mImagePreview.getResourceWidth(), mImagePreview.getResourceHeight()))
                .build();
        mDragLayout = helper.getDragLayout();
        mDragLayout.addView(imageView);
        ((ViewGroup) convertView).addView(mDragLayout);
        //设置图片属性
        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE);
        imageView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        imageView.setDoubleTapZoomDuration(mImagePreview.getZoomTransitionDuration());
        imageView.setMinScale(mImagePreview.getMinScale());
        imageView.setMaxScale(mImagePreview.getMaxScale());
        imageView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
            @Override
            public void onReady() {
                Log.i("nqh", "onReady");
                if (mDefaultScale == 0) {
                    mDefaultScale = imageView.getScale();
                    imageView.setMinScale(mDefaultScale);
                    imageView.setMaxScale(mDefaultScale * 3);
                    imageView.setDoubleTapZoomScale(mDefaultScale * 2);
                }
            }

            @Override
            public void onImageLoaded() {
                Log.i("nqh", "onImageLoaded");
            }

            @Override
            public void onPreviewLoadError(Exception e) {

            }

            @Override
            public void onImageLoadError(Exception e) {

            }

            @Override
            public void onTileLoadError(Exception e) {

            }

            @Override
            public void onPreviewReleased() {

            }
        });
        //图片范围的点击事件
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick2Exit) {
                    //如果设置了点击退出那么将会直接退出
                    mFragment.checkfinish();
                } else {
                    mFragment.onImageViewerClick(position);
                }
            }
        });

        final ImageInfo info = this.mImageInfo.get(position);
        final String originPathUrl = info.getOriginUrl();
        final String thumbPathUrl = info.getThumbnailUrl();

        mFinalLoadUrl = thumbPathUrl;

        if (mImageHashMap.containsKey(originPathUrl)) {
            mImageHashMap.remove(originPathUrl);
        }
        mImageHashMap.put(originPathUrl, imageView);
        // 判断原图缓存是否存在，存在的话，直接显示原图缓存，优先保证清晰。
        File cacheFile = ImageLoader.getGlideCacheFile(mFragment.getContext(), originPathUrl);
        if (cacheFile != null && cacheFile.exists()) {
            String imagePath = cacheFile.getAbsolutePath();
            boolean isLongImage = ImageUtil.isLongImage(imagePath);
            if (isLongImage) {
                imageView.setOrientation(ImageUtil.getOrientation(imagePath));
                imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_START);
            }
            imageView.setImage(ImageSource.uri(Uri.fromFile(new File(cacheFile.getAbsolutePath()))));
            progressBar.setVisibility(View.GONE);
        } else {
            mFinalLoadUrl = originPathUrl;
            mFinalLoadUrl = mFinalLoadUrl.trim();
            final String url = mFinalLoadUrl;
            //真实加载
            Glide.with(mFragment).downloadOnly().load(url).into(new SimpleTarget<File>() {
                @Override
                public void onLoadStarted(@Nullable Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    // glide会有时加载失败，具体看：https://github.com/bumptech/glide/issues/2894
                    Glide.with(mFragment).asFile().load(url).into(new SimpleTarget<File>() {
                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            //todo 检查加载失败图片
                            progressBar.setVisibility(View.GONE);
                            imageView.setImage(ImageSource.resource(R.drawable.icon_error_face_old));
                            mDefaultScale = -1;
                            imageView.setMaxScale(0.5f);
                        }

                        @Override
                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                            String imagePath = resource.getAbsolutePath();
                            boolean isLongImage = ImageUtil.isLongImage(imagePath);
                            if (isLongImage) {
                                imageView.setOrientation(ImageUtil.getOrientation(imagePath));
                                imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_START);
                            }
                            imageView.setImage(ImageSource.uri(Uri.fromFile(new File(resource.getAbsolutePath()))));
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onResourceReady(@NonNull File resource,
                                            @Nullable Transition<? super File> transition) {
                    String imagePath = resource.getAbsolutePath();
                    boolean isLongImage = ImageUtil.isLongImage(imagePath);
                    if (isLongImage) {
                        imageView.setOrientation(ImageUtil.getOrientation(imagePath));
                        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_START);
                    }
                    imageView.setImage(ImageSource.uri(Uri.fromFile(new File(resource.getAbsolutePath()))));
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        container.addView(convertView);
        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        try {
            container.removeView((View) object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, final Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}