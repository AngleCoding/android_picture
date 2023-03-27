package com.github.yuan.picture_take.adapter.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.yuan.picture_take.R;
import com.github.yuan.picture_take.config.PictureMimeType;
import com.github.yuan.picture_take.config.PictureSelectionConfig;
import com.github.yuan.picture_take.entity.LocalMedia;
import com.github.yuan.picture_take.style.SelectMainStyle;
import com.github.yuan.picture_take.utils.MediaUtils;
import com.github.yuan.picture_take.utils.StyleUtils;

/**
 * @author：luck
 * @date：2021/11/20 3:59 下午
 * @describe：ImageViewHolder
 */
public class ImageViewHolder extends BaseRecyclerMediaHolder {
    private final ImageView ivEditor;
    private final TextView tvMediaTag;

    public ImageViewHolder(View itemView, PictureSelectionConfig config) {
        super(itemView, config);
        tvMediaTag = itemView.findViewById(R.id.tv_media_tag);
        ivEditor = itemView.findViewById(R.id.ivEditor);
        SelectMainStyle adapterStyle = PictureSelectionConfig.selectorStyle.getSelectMainStyle();
        int imageEditorRes = adapterStyle.getAdapterImageEditorResources();
        if (StyleUtils.checkStyleValidity(imageEditorRes)) {
            ivEditor.setImageResource(imageEditorRes);
        }
        int[] editorGravity = adapterStyle.getAdapterImageEditorGravity();
        if (StyleUtils.checkArrayValidity(editorGravity)) {
            if (ivEditor.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) ivEditor.getLayoutParams()).removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                for (int i : editorGravity) {
                    ((RelativeLayout.LayoutParams) ivEditor.getLayoutParams()).addRule(i);
                }
            }
        }

        int[] tagGravity = adapterStyle.getAdapterTagGravity();
        if (StyleUtils.checkArrayValidity(tagGravity)) {
            if (tvMediaTag.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) tvMediaTag.getLayoutParams()).removeRule(RelativeLayout.ALIGN_PARENT_END);
                ((RelativeLayout.LayoutParams) tvMediaTag.getLayoutParams()).removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                for (int i : tagGravity) {
                    ((RelativeLayout.LayoutParams) tvMediaTag.getLayoutParams()).addRule(i);
                }
            }
        }
        int background = adapterStyle.getAdapterTagBackgroundResources();
        if (StyleUtils.checkStyleValidity(background)) {
            tvMediaTag.setBackgroundResource(background);
        }

        int textSize = adapterStyle.getAdapterTagTextSize();
        if (StyleUtils.checkSizeValidity(textSize)) {
            tvMediaTag.setTextSize(textSize);
        }

        int textColor = adapterStyle.getAdapterTagTextColor();
        if (StyleUtils.checkStyleValidity(textColor)) {
            tvMediaTag.setTextColor(textColor);
        }
    }


    @Override
    public void bindData(LocalMedia media, int position) {
        super.bindData(media, position);
        if (media.isEditorImage() && media.isCut()) {
            ivEditor.setVisibility(View.VISIBLE);
        } else {
            ivEditor.setVisibility(View.GONE);
        }
        tvMediaTag.setVisibility(View.VISIBLE);
        if (PictureMimeType.isHasGif(media.getMimeType())) {
            tvMediaTag.setText(mContext.getString(R.string.ps_gif_tag));
        } else if (PictureMimeType.isHasWebp(media.getMimeType())) {
            tvMediaTag.setText(mContext.getString(R.string.ps_webp_tag));
        } else if (MediaUtils.isLongImage(media.getWidth(), media.getHeight())) {
            tvMediaTag.setText(mContext.getString(R.string.ps_long_chart));
        } else {
            tvMediaTag.setVisibility(View.GONE);
        }
    }
}
