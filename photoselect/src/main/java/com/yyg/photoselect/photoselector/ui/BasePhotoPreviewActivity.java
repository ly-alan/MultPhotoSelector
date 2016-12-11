package com.yyg.photoselect.photoselector.ui;
/**
 * @author Aizaz AZ
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yyg.photoselect.R;
import com.yyg.photoselect.photoselector.model.PhotoModel;
import com.yyg.photoselect.photoselector.util.AnimationUtil;
import com.yyg.photoselect.photoselector.view.PhotoViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 点击查看大图预览
 */
public class BasePhotoPreviewActivity extends Activity implements OnPageChangeListener, OnClickListener {

    public static final int REQUEST_CODE = 23;

    public static final String KEY_SELECT = "edit_select";
    /**
     * 图片展示
     */
    private PhotoViewPager mViewPager;
    /**
     * 顶部布局
     */
    private RelativeLayout layoutTop;
    /**
     * 返回按钮
     */
    private ImageButton btnBack;
    /**
     * 中间图片张数标识
     */
    private TextView tvPercent;
    /**
     * 右上角的点击区域
     */
    private View checkLayout;
    /**
     * 选中标识
     */
    private CheckBox checkBox;
    /**
     * 当前显示的所有图片列表
     */
    protected List<PhotoModel> photos;
    /**
     * 当前选中的列表
     */
    protected ArrayList<PhotoModel> tempSelectList;
    /**
     * 当前显示位置
     */
    protected int current;
    /**
     * viewpager是否正在滑动
     */
    boolean isScrolling;
    /**
     * 顶部标题栏是否隐藏了
     */
    protected boolean isUp;

    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_photopreview);
        layoutTop = (RelativeLayout) findViewById(R.id.layout_top_app);
        btnBack = (ImageButton) findViewById(R.id.btn_back_app);
        tvPercent = (TextView) findViewById(R.id.tv_percent_app);
        mViewPager = (PhotoViewPager) findViewById(R.id.vp_base_app);
        checkBox = (CheckBox) findViewById(R.id.cb_preview_choice);
        checkLayout = (View) findViewById(R.id.cb_preview_choice_layout);


        checkLayout.setOnClickListener(this);

        btnBack.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(this);
        // 渐入效果
        overridePendingTransition(R.anim.activity_alpha_action_in, 0);

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    /**
     * 绑定数据，更新界面
     */
    protected void bindData() {
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(current);
    }

    private PagerAdapter mPagerAdapter = new PagerAdapter() {

        @Override
        public int getCount() {
            if (photos == null) {
                return 0;
            } else {
                return photos.size();
            }
        }

        @Override
        public View instantiateItem(final ViewGroup container, final int position) {
            PhotoPreview photoPreview = new PhotoPreview(getApplicationContext());
            ((ViewPager) container).addView(photoPreview);
            photoPreview.loadImage(photos.get(position));
            photoPreview.setOnClickListener(photoItemClickListener);
            return photoPreview;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    };

    @Override
    public void onClick(View v) {
        if (isUp) {
            return;
        }
        if (isScrolling) {
            //正在滑动时不允许点击
            return;
        }
        if (v.getId() == R.id.btn_back_app) {
            finish();
        } else if (v.getId() == R.id.cb_preview_choice_layout) {
            clickCheckBox();
        }
//            if (checkBox.isChecked()) {
//                checkBox.setChecked(false);
//            } else {
//                checkBox.setChecked(true);
//            }
//        }
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_SELECT, tempSelectList);
        data.putExtras(bundle);
        setResult(RESULT_OK, data);
        super.finish();
    }

    /**
     * checkBox的点击事件
     */
    private void clickCheckBox() {
        if (checkBox.isChecked()) {
            //取消当前的选中
            checkBox.setChecked(false);
            tempSelectList.remove(photos.get(current));
        } else {
            if (tempSelectList.size() < PhotoSelectorActivity.MAX_IMAGE) {
                //新增选中
                checkBox.setChecked(true);
                tempSelectList.add(photos.get(current));
            } else {
                checkBox.setChecked(false);
                mToast.setText(getString(R.string.max_img_limit_reached, PhotoSelectorActivity.MAX_IMAGE));
                mToast.show();
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (arg0 == 1) {
            isScrolling = true;
        } else {
            isScrolling = false;
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        current = arg0;
        updatePercent();
    }

    protected void updatePercent() {
        tvPercent.setText((current + 1) + "/" + photos.size());
        //该张图是否被选中(gridview有缓存左右，因此要根据当前显示的图片判断)
        if (tempSelectList.contains(photos.get(current))) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
    }

    /**
     * 图片点击事件回调
     */
    private OnClickListener photoItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
//            if (!isUp) {
//                new AnimationUtil(getApplicationContext(), R.anim.translate_up)
//                        .setInterpolator(new LinearInterpolator()).setFillAfter(true).startAnimation(layoutTop);
//                isUp = true;
//                checkBox.setEnabled(false);
//                checkLayout.setEnabled(false);
//            } else {
//                new AnimationUtil(getApplicationContext(), R.anim.translate_down_current)
//                        .setInterpolator(new LinearInterpolator()).setFillAfter(true).startAnimation(layoutTop);
//                isUp = false;
//                checkBox.setEnabled(true);
//                checkLayout.setEnabled(true);
//            }
        }
    };
}
