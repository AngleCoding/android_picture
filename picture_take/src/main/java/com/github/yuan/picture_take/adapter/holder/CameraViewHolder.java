package com.github.yuan.picture_take.adapter.holder;

import android.view.View;
import android.widget.TextView;

import com.github.yuan.picture_take.R;
import com.github.yuan.picture_take.config.PictureSelectionConfig;
import com.github.yuan.picture_take.config.SelectMimeType;
import com.github.yuan.picture_take.style.SelectMainStyle;
import com.github.yuan.picture_take.utils.StyleUtils;


/**
 * @author：luck
 * @date：2021/11/20 3:54 下午
 * @describe：CameraViewHolder
 */
public class CameraViewHolder extends BaseRecyclerMediaHolder {

    public CameraViewHolder(View itemView) {
        super(itemView);
        TextView tvCamera = itemView.findViewById(R.id.tvCamera);
        SelectMainStyle adapterStyle = PictureSelectionConfig.selectorStyle.getSelectMainStyle();
        int background = adapterStyle.getAdapterCameraBackgroundColor();
        if (StyleUtils.checkStyleValidity(background)) {
            tvCamera.setBackgroundColor(background);
        }
        int drawableTop = adapterStyle.getAdapterCameraDrawableTop();
        if (StyleUtils.checkStyleValidity(drawableTop)) {
            tvCamera.setCompoundDrawablesRelativeWithIntrinsicBounds(0, drawableTop, 0, 0);
        }
        String text = adapterStyle.getAdapterCameraText();
        if (StyleUtils.checkTextValidity(text)) {
            tvCamera.setText(text);
        } else {
            if (PictureSelectionConfig.getInstance().chooseMode == SelectMimeType.ofAudio()) {
                tvCamera.setText(itemView.getContext().getString(R.string.ps_tape));
            }
        }
        int textSize = adapterStyle.getAdapterCameraTextSize();
        if (StyleUtils.checkSizeValidity(textSize)) {
            tvCamera.setTextSize(textSize);
        }
        int textColor = adapterStyle.getAdapterCameraTextColor();
        if (StyleUtils.checkStyleValidity(textColor)) {
            tvCamera.setTextColor(textColor);
        }
    }

}
