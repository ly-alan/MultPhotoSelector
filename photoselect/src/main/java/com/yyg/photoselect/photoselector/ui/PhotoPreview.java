package com.yyg.photoselect.photoselector.ui;
/**
 * 
 * @author Aizaz AZ
 *
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.yyg.photoselect.R;
import com.yyg.photoselect.photoselector.model.PhotoModel;
import com.yyg.photoselect.photoselector.util.LoadUtils;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 预览大图
 */
public class PhotoPreview extends LinearLayout{

//	private ProgressBar pbLoading;
	private PhotoView ivContent;
	private OnClickListener mClickListener;

	public PhotoPreview(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.view_photopreview, this, true);

		ivContent = (PhotoView) findViewById(R.id.iv_content_vpp);

//		ivContent.setOnClickListener(this);
		//photoView的已经拦截了点击事件，单击需自己做判断
		ivContent.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View view, float x, float y) {
				if (mClickListener != null)
					mClickListener.onClick(ivContent);
			}

			@Override
			public void onOutsidePhotoTap() {

			}
		});
	}

	public PhotoPreview(Context context, AttributeSet attrs, int defStyle) {
		this(context);
	}

	public PhotoPreview(Context context, AttributeSet attrs) {
		this(context);
	}

	public void loadImage(PhotoModel photoModel) {
		loadImage("file://" + photoModel.getOriginalPath());
	}

	private void loadImage(String path) {
		LoadUtils.loadImage(getContext(),path,ivContent);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		this.mClickListener = l;
	}
}
