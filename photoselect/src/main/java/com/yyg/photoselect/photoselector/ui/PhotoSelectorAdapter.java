package com.yyg.photoselect.photoselector.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yyg.photoselect.R;
import com.yyg.photoselect.photoselector.model.PhotoModel;
import com.yyg.photoselect.photoselector.util.LoadUtils;

import java.util.ArrayList;


/**
 * @author Aizaz AZ
 */
public class PhotoSelectorAdapter extends MBaseAdapter<PhotoModel> {

    private int itemWidth;
    private int horizentalNum = 3;
    /**
     * 选中状态变化监听
     */
    private onPhotoItemCheckedListener listener;
    private RelativeLayout.LayoutParams itemLayoutParams;
    /**
     * 点击图片的监听
     */
    private onItemClickListener mCallback;
    /**
     * 点击拍照监听
     */
    private OnClickListener cameraListener;

    private PhotoSelectorActivity photoSelectorActivity;

    Toast mToast;

    public PhotoSelectorAdapter(PhotoSelectorActivity context, ArrayList<PhotoModel> models, int screenWidth, onPhotoItemCheckedListener listener, onItemClickListener mCallback,
                                OnClickListener cameraListener) {
        super(context, models);
        this.photoSelectorActivity = context;
        setItemWidth(screenWidth);
        this.listener = listener;
        this.mCallback = mCallback;
        this.cameraListener = cameraListener;
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    public void setItemWidth(int screenWidth) {
        int horizentalSpace = context.getResources().getDimensionPixelSize(R.dimen.sticky_item_horizontalSpacing);
        //若布局中未设置padding需将该值去掉，或修改
        int padding = context.getResources().getDimensionPixelSize(R.dimen.gridview_padding);
        this.itemWidth = (screenWidth - 2 * padding - (horizentalSpace * (horizentalNum - 1))) / horizentalNum;
        this.itemLayoutParams = new RelativeLayout.LayoutParams(itemWidth, itemWidth);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_photoitem, parent, false);
            viewHolder = new MyHolder(convertView);
            viewHolder.ivPhoto.setLayoutParams(itemLayoutParams);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyHolder) convertView.getTag();
        }

        viewHolder.loadBitmap(parent.getContext(), models.get(position));
        viewHolder.setCheckState(models.get(position).isChecked());
        setListener(viewHolder, mCallback, position);
        return convertView;
    }

    /**
     * 设置监听事件
     */
    public void setListener(final MyHolder myHolder, final onItemClickListener itemClickListener, final int position) {
        myHolder.ivPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.showAllPicture(position);
                }
            }
        });
        myHolder.cbLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCheckBox(myHolder, position);
            }
        });
        myHolder.cbPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCheckBox(myHolder, position);
            }
        });
    }

    /**
     * checkBox的点击事件
     *
     * @param myHolder
     * @param position
     */
    private void clickCheckBox(MyHolder myHolder, int position) {
        if (models.get(position).isChecked()) {
            //当前是选中状态，取消选中
            models.get(position).setChecked(false);
            myHolder.setCheckState(false);
            listener.onCheckedChanged(models.get(position), myHolder.cbPhoto, false);
        } else {
            //当前是未选中状态,变更为选中
            if (!photoSelectorActivity.hasChoiceMaxSize()) {
                //未达到最大的选择值
                models.get(position).setChecked(true);
                myHolder.setCheckState(true);
                listener.onCheckedChanged(models.get(position), myHolder.cbPhoto, true);
            } else {
                models.get(position).setChecked(false);
                myHolder.setCheckState(false);

                mToast.setText(photoSelectorActivity.getString(R.string.max_img_limit_reached, photoSelectorActivity.MAX_IMAGE));
                mToast.show();

            }
        }
    }


    private static class MyHolder {
        /**
         * 图片
         */
        private ImageView ivPhoto;
        /**
         * 复选框
         */
        private CheckBox cbPhoto;
        /**
         * 复选框的点击区域
         */
        private View cbLayout;

        public MyHolder(View view) {
            ivPhoto = (ImageView) view.findViewById(R.id.iv_photo_lpsi);
            cbPhoto = (CheckBox) view.findViewById(R.id.cb_photo_choice);
            cbLayout = (View) view.findViewById(R.id.cb_photo_choice_layout);
        }

        /**
         * 加载图片
         *
         * @param context
         * @param photo
         */
        public void loadBitmap(Context context, PhotoModel photo) {
            LoadUtils.loadImage(context, "file://" + photo.getOriginalPath(), ivPhoto);
        }

        /**
         * 设置选中状态
         *
         * @param isCheck
         */
        public void setCheckState(boolean isCheck) {
            if (isCheck) {
                //图片添加阴影
                ivPhoto.setDrawingCacheEnabled(true);
                ivPhoto.buildDrawingCache();
                ivPhoto.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                cbPhoto.setChecked(true);
            } else {
                //清除阴影
                ivPhoto.clearColorFilter();
                cbPhoto.setChecked(false);
            }
        }
    }

    /**
     * item 点击状态的改变
     */
    public interface onPhotoItemCheckedListener {
        public void onCheckedChanged(PhotoModel photoModel, CompoundButton buttonView, boolean isChecked);
    }

    /**
     * 长按item，跳转查看所有的相片
     */
    public interface onItemClickListener {
        public void showAllPicture(int position);
    }
}
