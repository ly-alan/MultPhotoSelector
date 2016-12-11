package com.yyg.photoselect.photoselector.ui;
/**
 * @author Aizaz AZ
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yyg.photoselect.R;
import com.yyg.photoselect.photoselector.domain.PhotoSelectorDomain;
import com.yyg.photoselect.photoselector.model.AlbumModel;
import com.yyg.photoselect.photoselector.model.PhotoModel;
import com.yyg.photoselect.photoselector.ui.PhotoSelectorAdapter.onItemClickListener;
import com.yyg.photoselect.photoselector.ui.PhotoSelectorAdapter.onPhotoItemCheckedListener;
import com.yyg.photoselect.photoselector.util.AnimationUtil;
import com.yyg.photoselect.photoselector.util.CommonUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Aizaz AZ
 */
public class PhotoSelectorActivity extends Activity implements
        onItemClickListener, onPhotoItemCheckedListener, OnItemClickListener,
        OnClickListener {

    public static final int SINGLE_IMAGE = 1;
    public static final String KEY_MAX = "key_max";
    public static int MAX_IMAGE;

    public static final int REQUEST_PHOTO = 0;
    private static final int REQUEST_CAMERA = 1;

    public static String RECCENT_PHOTO = null;
    /**
     * 相册的图片文件
     */
    private GridView gvPhotos;
    private PhotoSelectorAdapter photoAdapter;
    /**
     * 相册列表
     */
    private RelativeLayout layoutAlbum;
    private ListView lvAblum;
    private AlbumAdapter albumAdapter;
    /**
     * 确定按钮
     */
    private Button btnOk;
    /**
     * 文件夹名称，
     */
    private TextView tvAlbum;
    /**
     * 预览按钮
     */
    private TextView tvPreview;
    /**
     * 标题
     */
    private TextView tvTitle;
    /***
     * 获取本地图库的控制器
     */
    private PhotoSelectorDomain photoSelectorDomain;
    /**
     * 已选中的文件列表
     */
    private ArrayList<PhotoModel> selected;
//    /**
//     * 确定按钮
//     */
//    private TextView tvOk;

    /**
     * 确定按钮
     */
    private TextView tvNumber;

    /**
     * 最近照片目录的更新
     */
    private OnLocalReccentListener reccentListener = new OnLocalReccentListener() {
        @Override
        public void onPhotoLoaded(List<PhotoModel> photos) {
            for (PhotoModel model : photos) {
                if (selected.contains(model)) {
                    model.setChecked(true);
                }
            }
            photoAdapter.update(photos);
            // 滚动到顶端
            gvPhotos.smoothScrollToPosition(0);
            // reset(); //--keep selected photos
        }
    };

    /**
     * 更换查看相册
     */
    private OnLocalAlbumListener albumListener = new OnLocalAlbumListener() {
        @Override
        public void onAlbumLoaded(List<AlbumModel> albums) {
            albumAdapter.update(albums);
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_right_lh || v.getId() == R.id.tv_number) {
            //确认按钮
            ok();
        } else if (v.getId() == R.id.tv_album_ar) {
            //打开相册列表
            album();
        } else if (v.getId() == R.id.tv_album_ar_layout) {
            album();
        } else if (v.getId() == R.id.tv_preview_ar) {
            //预览按钮
            priview();
        } else if (v.getId() == R.id.tv_camera_vc) {
            //开启照相机，暂时未使用
//            catchPicture();
        } else if (v.getId() == R.id.bv_back_lh) {
            //返回按钮
            if (layoutAlbum.getVisibility() == View.GONE) {
                finish();
            } else {
                hideAlbum();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RECCENT_PHOTO = getResources().getString(R.string.recent_photos);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        setContentView(R.layout.activity_photoselector);
        if (getIntent().getExtras() != null) {
            MAX_IMAGE = getIntent().getIntExtra(KEY_MAX, 10);
        }
        //默认最大选择数目为10
        MAX_IMAGE = MAX_IMAGE <= 0 ? 10 : MAX_IMAGE;

        initView();
        initData();
    }


    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title_lh);
        gvPhotos = (GridView) findViewById(R.id.gv_photos_ar);
        lvAblum = (ListView) findViewById(R.id.lv_ablum_ar);
//        tvOk = (TextView) findViewById(R.id.tv_ok);
        btnOk = (Button) findViewById(R.id.btn_right_lh);
        findViewById(R.id.tv_album_ar_layout).setOnClickListener(this);
        tvAlbum = (TextView) findViewById(R.id.tv_album_ar);
        tvPreview = (TextView) findViewById(R.id.tv_preview_ar);
        layoutAlbum = (RelativeLayout) findViewById(R.id.layout_album_ar);
        tvNumber = (TextView) findViewById(R.id.tv_number);

        // 返回
        findViewById(R.id.bv_back_lh).setOnClickListener(this);
        btnOk.setOnClickListener(this);
        tvNumber.setOnClickListener(this);
        tvAlbum.setOnClickListener(this);
        tvPreview.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        tvAlbum.setText(getString(R.string.recent_photos));
        tvTitle.setText(getString(R.string.recent_photos));
//        tvOk.setText(getString(R.string.select_complete, 0));

        photoSelectorDomain = new PhotoSelectorDomain(getApplicationContext());
        selected = new ArrayList<PhotoModel>();

        photoAdapter = new PhotoSelectorAdapter(this,
                new ArrayList<PhotoModel>(), CommonUtils.getWidthPixels(this),
                this, this, this);
        gvPhotos.setAdapter(photoAdapter);

        albumAdapter = new AlbumAdapter(getApplicationContext(),
                new ArrayList<AlbumModel>());
        lvAblum.setAdapter(albumAdapter);
        lvAblum.setOnItemClickListener(this);

        photoSelectorDomain.getReccent(reccentListener); // 更新最近照片
        photoSelectorDomain.updateAlbum(albumListener); // 跟新相册信息
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            PhotoModel photoModel = new PhotoModel(CommonUtils.query(
                    getApplicationContext(), data.getData()));
            //照相后的操做，暂时没有
            if (selected.size() >= MAX_IMAGE) {
                Toast.makeText(this, String.format(getString(R.string.max_img_limit_reached),
                        MAX_IMAGE), Toast.LENGTH_SHORT).show();
                photoModel.setChecked(false);
                photoAdapter.notifyDataSetChanged();
            } else {
                if (!selected.contains(photoModel)) {
                    selected.add(photoModel);
                }
            }
            ok();
        }else if (requestCode == PhotoPreviewActivity.REQUEST_CODE && resultCode == RESULT_OK){
            if (data!=null){
                selected.clear();
                selected = (ArrayList<PhotoModel>)data.getSerializableExtra(PhotoPreviewActivity.KEY_SELECT);
                List<PhotoModel> photos = photoAdapter.getItems();
                for (PhotoModel model : photos) {
                    if (selected.contains(model)) {
                        model.setChecked(true);
                    }else {
                        model.setChecked(false);
                    }
                }
                photoAdapter.notifyDataSetChanged();
                tvNumber.setText("(" + selected.size() + ")");
                if (selected.isEmpty()) {
                    tvPreview.setEnabled(false);
                    tvPreview.setTextColor(getResources().getColor(R.color.preview_gray));
                    tvPreview.setText(getString(R.string.preview));
                }else {
                    tvPreview.setEnabled(true);
                    tvPreview.setTextColor(getResources().getColor(R.color.bg));
                }
            }
        }
    }

    /**
     * 完成
     */
    private void ok() {
        if (selected.isEmpty()) {
            setResult(RESULT_CANCELED);
        } else {
            Intent data = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("photos", selected);
            data.putExtras(bundle);
            setResult(RESULT_OK, data);
        }
        finish();
    }

    /**
     * 弹出目录列表
     */
    private void album() {
        if (layoutAlbum.getVisibility() == View.GONE) {
            popAlbum();
        } else {
            hideAlbum();
        }
    }

    /**
     * 拍照，暂时未使用
     */
    private void catchPicture() {
        CommonUtils.launchActivityForResult(this, new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CAMERA);
    }

    /**
     * 弹出相册列表
     */
    private void popAlbum() {
        layoutAlbum.setVisibility(View.VISIBLE);
        new AnimationUtil(getApplicationContext(), R.anim.translate_up_current)
                .setLinearInterpolator().startAnimation(layoutAlbum);
    }

    /**
     * 隐藏相册列表
     */
    private void hideAlbum() {
        new AnimationUtil(getApplicationContext(), R.anim.translate_down)
                .setLinearInterpolator().startAnimation(layoutAlbum);
        layoutAlbum.setVisibility(View.GONE);
    }

    /**
     * 清空选中的图片
     */
    private void reset() {
        selected.clear();
        tvNumber.setText("(0)");
        tvPreview.setEnabled(false);
        tvPreview.setTextColor(getResources().getColor(R.color.preview_gray));
    }

    /**
     * 预览照片，查看当前选中的几张图片
     */
    private void priview() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("photos", selected);
//        CommonUtils.launchActivity(this, PhotoPreviewActivity.class, bundle);

        Intent intent = new Intent(this, PhotoPreviewActivity.class);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        CommonUtils.launchActivityForResult(this, intent, PhotoPreviewActivity.REQUEST_CODE);
    }

    /**
     * 点击item，查看该目录下的全部图片
     *
     * @param position
     */
    @Override
    public void showAllPicture(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putSerializable("selected", selected);
        bundle.putString("album", tvAlbum.getText().toString());
//        CommonUtils.launchActivity(this, PhotoPreviewActivity.class, bundle);

        Intent intent = new Intent(this, PhotoPreviewActivity.class);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        CommonUtils.launchActivityForResult(this, intent, PhotoPreviewActivity.REQUEST_CODE);
    }


    /**
     * 是否已经选择达到了最大的可选数目
     *
     * @return
     */
    public boolean hasChoiceMaxSize() {
        if (selected.size() < MAX_IMAGE) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 点击显示图片的checkbox，选中状态发生变化时
     *
     * @param photoModel
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(PhotoModel photoModel, CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (!selected.contains(photoModel)) {
                selected.add(photoModel);
            }
            tvPreview.setEnabled(true);
            tvPreview.setTextColor(getResources().getColor(R.color.bg));
        } else {
            selected.remove(photoModel);
        }
        tvNumber.setText("(" + selected.size() + ")");

        if (selected.isEmpty()) {
            tvPreview.setEnabled(false);
            tvPreview.setTextColor(getResources().getColor(R.color.preview_gray));
            tvPreview.setText(getString(R.string.preview));
        }
    }


    /**
     * 相册列表的点击事件，
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        AlbumModel current = (AlbumModel) parent.getItemAtPosition(position);
        for (int i = 0; i < parent.getCount(); i++) {
            AlbumModel album = (AlbumModel) parent.getItemAtPosition(i);
            if (i == position)
                album.setCheck(true);
            else
                album.setCheck(false);
        }
        albumAdapter.notifyDataSetChanged();
        hideAlbum();
        tvAlbum.setText(current.getName());
        tvTitle.setText(current.getName());

        // 更新照片列表
        if (current.getName().equals(RECCENT_PHOTO)) {
            photoSelectorDomain.getReccent(reccentListener);
        } else {
            // 获取选中相册的照片
            photoSelectorDomain.getAlbum(current.getName(), reccentListener);
        }
    }

    /**
     * 获取本地图库照片回调
     */
    public interface OnLocalReccentListener {
        public void onPhotoLoaded(List<PhotoModel> photos);
    }

    /**
     * 获取本地相册信息回调
     */
    public interface OnLocalAlbumListener {
        public void onAlbumLoaded(List<AlbumModel> albums);
    }

    @Override
    public void onBackPressed() {
        if (layoutAlbum.getVisibility() == View.VISIBLE) {
            hideAlbum();
        } else
            super.onBackPressed();
    }
}
