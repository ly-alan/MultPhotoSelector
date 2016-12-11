package com.yyg.photoselect.photoselector.ui;
/**
 * @author Aizaz AZ
 */

import android.os.Bundle;

import com.yyg.photoselect.photoselector.domain.PhotoSelectorDomain;
import com.yyg.photoselect.photoselector.model.PhotoModel;
import com.yyg.photoselect.photoselector.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 大图预览界面
 */
public class PhotoPreviewActivity extends BasePhotoPreviewActivity implements PhotoSelectorActivity.OnLocalReccentListener {

    private PhotoSelectorDomain photoSelectorDomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photoSelectorDomain = new PhotoSelectorDomain(getApplicationContext());

        tempSelectList = new ArrayList<PhotoModel>();
        init(getIntent().getExtras());
    }

    @SuppressWarnings("unchecked")
    protected void init(Bundle extras) {
        if (extras == null)
            return;

        if (extras.containsKey("photos")) {
            // 预览选中的图片
            photos = (List<PhotoModel>) extras.getSerializable("photos");
            tempSelectList.addAll(photos);
            current = extras.getInt("position", 0);
            updatePercent();
            bindData();
        } else if (extras.containsKey("album")) {
            // 查看文件目录下的全部照片// 相册
            String albumName = extras.getString("album");
            tempSelectList = (ArrayList<PhotoModel>) extras.getSerializable("selected");
            this.current = extras.getInt("position");
            if (!CommonUtils.isNull(albumName) && albumName.equals(PhotoSelectorActivity.RECCENT_PHOTO)) {
                photoSelectorDomain.getReccent(this);
            } else {
                photoSelectorDomain.getAlbum(albumName, this);
            }
        }
        if (tempSelectList == null) {

        }
    }

    @Override
    public void onPhotoLoaded(List<PhotoModel> photos) {
        this.photos = photos;
        updatePercent();
        bindData(); // 更新界面
    }

}
