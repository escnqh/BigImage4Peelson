package com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.view;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag.effect.DragToExit;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.ImagePreview;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.bean.ImageInfo;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.glide.ImageLoader;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.tool.ImageUtil;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.tool.NetworkUtil;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.tool.Print;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.subscaleview.ImageSource;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.subscaleview.SubsamplingScaleImageView;
import com.meitu.qihangni.bigimage4peelson.R;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ImagePreviewAdapter extends PagerAdapter {

    private static final String TAG = "ImagePreview";
    private FragmentActivity mActivity;
    private List<ImageInfo> mImageInfo;
    private HashMap<String, SubsamplingScaleImageView> mImageHashMap = new HashMap<>();
    private String mFinalLoadUrl = "";// 最终加载的图片url
    private double mDefaultScale;
    private FrameLayout mDragLayout;
    private boolean isDrag2Exit = false;
    private boolean isClick2Exit = false;

    public ImagePreviewAdapter(FragmentActivity mActivity, @NonNull List<ImageInfo> mImageInfo) {
        super();
        this.mImageInfo = mImageInfo;
        this.mActivity = mActivity;
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

    /**
     * 加载原图
     */
    public void loadOrigin(final ImageInfo imageInfo) {
        if (mImageHashMap.get(imageInfo.getOriginUrl()) != null) {
            final SubsamplingScaleImageView imageView = mImageHashMap.get(imageInfo.getOriginUrl());
            File cacheFile = ImageLoader.getGlideCacheFile(mActivity, imageInfo.getOriginUrl());
            if (cacheFile != null && cacheFile.exists()) {
                String thumbnailUrl = imageInfo.getThumbnailUrl();
                File smallCacheFile = ImageLoader.getGlideCacheFile(mActivity, thumbnailUrl);
                ImageSource small = null;
                if (smallCacheFile != null && smallCacheFile.exists()) {
                    String smallImagePath = smallCacheFile.getAbsolutePath();
                    small = ImageSource.bitmap(ImageUtil.getImageBitmap(smallImagePath, ImageUtil.getBitmapDegree(smallImagePath)));
                    int widSmall = ImageUtil.getWidthHeight(smallImagePath)[0];
                    int heiSmall = ImageUtil.getWidthHeight(smallImagePath)[1];
                    small.dimensions(widSmall, heiSmall);
                }

                String imagePath = cacheFile.getAbsolutePath();
                ImageSource origin = ImageSource.uri(imagePath);
                int widOrigin = ImageUtil.getWidthHeight(imagePath)[0];
                int heiOrigin = ImageUtil.getWidthHeight(imagePath)[1];
                origin.dimensions(widOrigin, heiOrigin);

                boolean isLongImage = ImageUtil.isLongImage(imagePath);
                Print.d(TAG, "isLongImage = " + isLongImage);
                if (isLongImage) {
                    imageView.setOrientation(ImageUtil.getOrientation(imagePath));
                    imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_START);
                }
                imageView.setImage(origin, small);
            }
        } else {
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        if (mActivity == null) {
            return container;
        }
        View convertView = View.inflate(mActivity, R.layout.item_photoview, null);
        View rootView = mActivity.findViewById(R.id.rootView);
        final ProgressBar progressBar = convertView.findViewById(R.id.progress_view);
        final SubsamplingScaleImageView imageView = new SubsamplingScaleImageView(mActivity);
        //设置拖拽
        DragHelper helper = new DragHelper.Builder(mActivity)
                .onlyCreateView()
                .setCanDrag(true)
                .setDragIntercept(new DragContract.DragIntercept() {
                    @Override
                    public boolean canDrag(@NonNull MotionEvent ev, int direction) {
                        if (direction == DragDirection.DOWN || direction <= DragDirection.UP) {
                            if (imageView.getCenter() != null && imageView.getCenter().y == imageView.getHeight() / imageView.getScale() / 2) {
                                return true;
                            }
                            if (imageView.getScale() <= mDefaultScale * 1.01) {
                                return true;
                            }
                        }
                        return false;
                    }
                })
                .addDragDirection(DragDirection.DOWN, new DragToExit(imageView, null, DragDirection.DOWN, rootView))
                .addDragDirection(DragDirection.UP, new DragToExit(imageView, null, DragDirection.DOWN, rootView))
                .build();
        mDragLayout = helper.getDragLayout();
        mDragLayout.addView(imageView);
        ((ViewGroup) convertView).addView(mDragLayout);
        //监听一下图片的状态
        imageView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
            @Override
            public void onReady() {
                Log.i("nqh", "onReady scale is: " + imageView.getScale());
                if (mDefaultScale == 0) {
                    markDefaultScale(imageView.getScale());
                }
            }

            @Override
            public void onImageLoaded() {
                Log.i("nqh", "onImageLoaded scale is: " + imageView.getScale());
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
        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE);
        imageView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        imageView.setDoubleTapZoomDuration(ImagePreview.getInstance().getZoomTransitionDuration());
        imageView.setMinScale(ImagePreview.getInstance().getMinScale());
        imageView.setMaxScale(ImagePreview.getInstance().getMaxScale());
        imageView.setDoubleTapZoomScale(ImagePreview.getInstance().getMediumScale());
        //图片范围的点击事件
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick2Exit) {
                    //如果设置了点击退出那么将会直接退出
                    mActivity.finish();
                }
                //todo 相册页选中效果
            }
        });

        final ImageInfo info = this.mImageInfo.get(position);
        final String originPathUrl = info.getOriginUrl();
        final String thumbPathUrl = info.getThumbnailUrl();

        mFinalLoadUrl = thumbPathUrl;
        ImagePreview.LoadStrategy loadStrategy = ImagePreview.getInstance().getLoadStrategy();

        if (mImageHashMap.containsKey(originPathUrl)) {
            mImageHashMap.remove(originPathUrl);
        }
        mImageHashMap.put(originPathUrl, imageView);

//        // 判断原图缓存是否存在，存在的话，直接显示原图缓存，优先保证清晰。
//        File cacheFile = ImageLoader.getGlideCacheFile(mActivity, originPathUrl);
//        if (cacheFile != null && cacheFile.exists()) {
//            String imagePath = cacheFile.getAbsolutePath();
//            boolean isLongImage = ImageUtil.isLongImage(imagePath);
//            Print.d(TAG, "isLongImage = " + isLongImage);
//            if (isLongImage) {
//                imageView.setOrientation(ImageUtil.getOrientation(imagePath));
//                imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_START);
//            }
//            imageView.setImage(ImageSource.uri(Uri.fromFile(new File(cacheFile.getAbsolutePath()))));
//            progressBar.setVisibility(View.GONE);
//        } else {

            // 根据当前加载策略判断，需要加载的url是哪一个
            if (loadStrategy == ImagePreview.LoadStrategy.Default) {
                mFinalLoadUrl = thumbPathUrl;
            } else if (loadStrategy == ImagePreview.LoadStrategy.AlwaysOrigin) {
                mFinalLoadUrl = originPathUrl;
            } else if (loadStrategy == ImagePreview.LoadStrategy.AlwaysThumb) {
                mFinalLoadUrl = thumbPathUrl;
            } else if (loadStrategy == ImagePreview.LoadStrategy.NetworkAuto) {
                if (NetworkUtil.isWiFi(mActivity)) {
                    mFinalLoadUrl = originPathUrl;
                } else {
                    mFinalLoadUrl = thumbPathUrl;
                }
            }
            mFinalLoadUrl = mFinalLoadUrl.trim();
            Print.d(TAG, "mFinalLoadUrl == " + mFinalLoadUrl);
            final String url = mFinalLoadUrl;

            Glide.with(mActivity).asFile().load(url).into(new SimpleTarget<File>() {
                @Override
                public void onLoadStarted(@Nullable Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    // glide会有时加载失败，具体看：https://github.com/bumptech/glide/issues/2894
                    Glide.with(mActivity).asFile().load(url).into(new SimpleTarget<File>() {
                        @Override
                        public void onLoadStarted(@Nullable Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                        }

                        @Override
                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                            String imagePath = resource.getAbsolutePath();
                            boolean isLongImage = ImageUtil.isLongImage(imagePath);
                            Print.d(TAG, "isLongImage = " + isLongImage);
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
                    Print.d(TAG, "isLongImage = " + isLongImage);
                    if (isLongImage) {
                        imageView.setOrientation(ImageUtil.getOrientation(imagePath));
                        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_START);
                    }
                    imageView.setImage(ImageSource.uri(Uri.fromFile(new File(resource.getAbsolutePath()))));
                    progressBar.setVisibility(View.GONE);
                }
            });

        container.addView(convertView);
        return convertView;
    }

    /**
     * 记录原始缩放比例
     *
     * @param scale 缩放比例
     */
    private void markDefaultScale(double scale) {
        mDefaultScale = scale;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        try {
            container.removeView((View) object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ImageLoader.clearMemory(mActivity);
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