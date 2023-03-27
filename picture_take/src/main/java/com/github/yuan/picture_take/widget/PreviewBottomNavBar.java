package com.github.yuan.picture_take.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.github.yuan.picture_take.R;
import com.github.yuan.picture_take.config.PictureSelectionConfig;
import com.github.yuan.picture_take.style.BottomNavBarStyle;
import com.github.yuan.picture_take.utils.StyleUtils;


/**
 * @author：luck
 * @date：2021/11/17 10:46 上午
 * @describe：PreviewBottomNavBar
 */
public class PreviewBottomNavBar extends BottomNavBar {

    public PreviewBottomNavBar(Context context) {
        super(context);
    }

    public PreviewBottomNavBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewBottomNavBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void handleLayoutUI() {
        tvPreview.setVisibility(GONE);
        tvImageEditor.setOnClickListener(this);
        tvImageEditor.setVisibility(PictureSelectionConfig.onEditMediaEventListener != null ? View.VISIBLE : GONE);
    }

    public void isDisplayEditor(boolean isHasVideo) {
        tvImageEditor.setVisibility(PictureSelectionConfig.onEditMediaEventListener != null && !isHasVideo ? View.VISIBLE : GONE);
    }

    public TextView getEditor() {
        return tvImageEditor;
    }

    @Override
    public void setBottomNavBarStyle() {
        super.setBottomNavBarStyle();
        BottomNavBarStyle bottomBarStyle = PictureSelectionConfig.selectorStyle.getBottomBarStyle();
        if (StyleUtils.checkStyleValidity(bottomBarStyle.getBottomPreviewNarBarBackgroundColor())) {
            setBackgroundColor(bottomBarStyle.getBottomPreviewNarBarBackgroundColor());
        } else if (StyleUtils.checkSizeValidity(bottomBarStyle.getBottomNarBarBackgroundColor())) {
            setBackgroundColor(bottomBarStyle.getBottomNarBarBackgroundColor());
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.ps_tv_editor) {
            if (bottomNavBarListener != null) {
                bottomNavBarListener.onEditImage();
            }
        }
    }
}
