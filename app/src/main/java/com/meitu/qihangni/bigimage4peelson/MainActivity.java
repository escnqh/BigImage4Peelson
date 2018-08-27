package com.meitu.qihangni.bigimage4peelson;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.meitu.qihangni.bigimage4peelson.BigImageViewer.BigImageViewUtil;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.ImagePreview;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.bean.ImageInfo;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.glide.ImageLoader;
import com.meitu.qihangni.bigimage4peelson.BigImageViewer.imagepreviewer.tool.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String[] images = {
            Environment.getExternalStorageDirectory().getPath() + "/Pictures/Screenshots/Screenshot_2018-03-27-00-27-15.jpg"
            , "http://img3.16fan.com/live/origin/201805/21/E421b24c08446.jpg"
            , "http://img.nga.178.com/attachments/mon_201801/18/-bqqbQ5-d3yyZ1gT3cSg4-3gl.jpg.medium.jpg"
            , "https://img.zcool.cn/community/01247f5991c8d40000002129fce48c.jpg@1280w_1l_2o_100sh.webp"
            , "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1534129404&di=5fb201b02d8b18efc82a3f3f5b199099&src=http://a3.att.hudong.com/44/06/01200000194742136353062572417.jpg"
            , "https://gss1.bdstatic.com/9vo3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike150%2C5%2C5%2C150%2C50/sign=c05506e79482d158af8f51e3e16372bd/c2fdfc039245d688c56332adacc27d1ed21b2451.jpg"
// "http://img3.16fan.com/live/origin/201805/21/4D7B35fdf082e.jpg",
//            "http://img6.16fan.com/attachments/wenzhang/201805/18/152660818127263ge.jpeg", //  5760 * 3840
//            "http://img3.16fan.com/live/origin/201805/21/2D02ebc5838e6.jpg",
//            "http://img6.16fan.com/attachments/wenzhang/201805/18/152660818716180ge.jpeg", //  2280 * 22116
//            "http://img3.16fan.com/live/origin/201805/21/14C5e483e7583.jpg",
//            "http://img3.16fan.com/live/origin/201805/21/A1B17c5f59b78.jpg",
//            "http://img3.16fan.com/live/origin/201805/21/94699b2be3cfa.jpg",
            , "http://img6.16fan.com/attachments/wenzhang/201805/18/152660818716180ge.jpeg" //  2280 * 22116
//            "http://img3.16fan.com/live/origin/201805/21/14C5e483e7583.jpg",
//            "http://img3.16fan.com/live/origin/201805/21/EB298ce595dd2.jpg",
//            "http://img6.16fan.com/attachments/wenzhang/201805/18/152660818127263ge.jpeg", //  5760 * 3840
//            "http://img3.16fan.com/live/origin/201805/21/264Ba4860d469.jpg",
//            "http://img6.16fan.com/attachments/wenzhang/201805/18/152660818716180ge.jpeg", //  2280 * 22116
//            "http://img6.16fan.com/attachments/wenzhang/201805/18/152660818127263ge.jpeg" //  5760 * 3840
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageInfo imageInfo;
        final List<ImageInfo> imageInfoList = new ArrayList<>();
        for (String image : images) {
            imageInfo = new ImageInfo();
            imageInfo.setOriginUrl(image);// 原图
            imageInfo.setThumbnailUrl(
                    image.concat("-1200"));// 缩略图，实际使用中，根据需求传入缩略图路径。如果没有缩略图url，可以将两项设置为一样，并隐藏查看原图按钮即可。
            imageInfoList.add(imageInfo);
            imageInfo = null;
        }

        // 仅加载普清
        findViewById(R.id.buttonThumb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePreview
                        .getInstance()
                        .setContext(MainActivity.this)
                        .setImageInfoList(imageInfoList)
                        .setShowDownButton(true)
                        .setLoadStrategy(ImagePreview.LoadStrategy.AlwaysThumb)
                        .setFolderName("BigImageViewDownload")
                        .setScaleLevel(0.5f, 3, 8)
                        .setZoomTransitionDuration(300)
                        .start();
            }
        });

        // 仅加载原图
        findViewById(R.id.buttonOrigin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigImageViewUtil.showAlbum(MainActivity.this, imageInfoList);
            }
        });

        // 手动模式：默认普清，手动高清
        findViewById(R.id.buttonDefault).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePreview
                        .getInstance()
                        .setContext(MainActivity.this)
                        .setImageInfoList(imageInfoList)
                        .setShowDownButton(true)
                        .setLoadStrategy(ImagePreview.LoadStrategy.Default)
                        .setFolderName("BigImageViewDownload")
                        .setScaleLevel(1, 3, 8)
                        .setZoomTransitionDuration(500)
                        .start();
            }
        });

        // 网络自适应（WiFi原图，流量普清）
        findViewById(R.id.buttonAuto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePreview
                        .getInstance()
                        .setContext(MainActivity.this)
                        .setImageInfoList(imageInfoList)
                        .setShowDownButton(true)
                        .setLoadStrategy(ImagePreview.LoadStrategy.NetworkAuto)
                        .setFolderName("BigImageViewDownload")
                        .setScaleLevel(1, 3, 5)
                        .setZoomTransitionDuration(300)
                        .start();
            }
        });

        findViewById(R.id.buttonClean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageLoader.cleanDiskCache(MainActivity.this);
                ToastUtil.getInstance()._short(MainActivity.this, "磁盘缓存已成功清除");
            }
        });
    }
}
