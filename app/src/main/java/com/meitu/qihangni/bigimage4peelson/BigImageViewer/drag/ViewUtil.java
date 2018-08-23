package com.meitu.qihangni.bigimage4peelson.BigImageViewer.drag;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewUtil {

    public static void setVisible(View view) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void setGone(View view) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }

    public static void setInvisible(View view) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() != View.INVISIBLE) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static void setMarginLeft(View view, int leftMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.leftMargin = leftMargin;
        view.setLayoutParams(params);
    }

    public static void setMarginRight(View view, int rightMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.rightMargin = rightMargin;
        view.setLayoutParams(params);
    }

    public static void setMarginTop(View view, int topMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.topMargin = topMargin;
        view.setLayoutParams(params);
    }

    public static void setMarginBottom(View view, int bottomMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.bottomMargin = bottomMargin;
        view.setLayoutParams(params);
    }

    public static void setBold(TextView tv) {
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
    }

    public static void setDrawableTop(TextView textView, int drawableResId) {
        Drawable drawable = textView.getContext().getResources().getDrawable(drawableResId);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(null, drawable, null, null);
        }
    }

    public static void setMaxHeight(TextView view, int maxHeight) {
        if (view == null) {
            return;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (view.getMaxHeight() != maxHeight) {
                view.setMaxHeight(maxHeight);
            }
        } else {
            view.setMaxHeight(maxHeight);
        }
    }

    public static void changeViewMarginTop(View view, int topMargin, boolean containOriMargin) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (containOriMargin) {
            params.topMargin = params.topMargin + topMargin;
        } else {
            params.topMargin = topMargin;
        }
        view.setLayoutParams(params);
    }

    public static void changeViewHeight(View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        view.setLayoutParams(params);
    }

    public static void setVisibility(View view, int visibility) {
        if (view == null) {
            return;
        }
        if (visibility == View.VISIBLE) {
            setVisible(view);
        } else if (visibility == View.GONE) {
            setGone(view);
        } else if (visibility == View.INVISIBLE) {
            setInvisible(view);
        }
    }

    public static boolean isShow(View view) {
        return view != null && view.isShown();
    }

    public static void setSelected(View view, boolean isSelected) {
        if (view == null) return;
        view.setSelected(isSelected);
    }
}
