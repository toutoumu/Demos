package com.example.imageselector.custom;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.imageselector.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

public class FileSelectorActivity extends AppCompatActivity {

  public final static String DATA = "data";//返回给前一个页面的数据
  public final static String COUNG = "count";//可以选多少张

  private List<Picture> mPictures = new ArrayList<>();// 数据
  private ArrayList<Picture> mSelectedPictures = new ArrayList<>();//选择的数据

  private int count = 1;//可以选多少张
  private GridView mGridView;
  private PictureListAdapter mPictureListAdapter;
  private Toolbar mToolBar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_file_selector);
    if (getIntent() != null) {
      count = getIntent().getIntExtra(COUNG, 1);
    }
    mGridView = (GridView) findViewById(R.id.grid_view);

    mPictureListAdapter = new PictureListAdapter(this, mPictures);
    mGridView.setAdapter(mPictureListAdapter);
    mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Picture picture = mPictures.get(position);
        if (mSelectedPictures.size() >= count && !picture.isSelected()) {
          return;
        }
        if (mSelectedPictures.contains(picture)) {
          mSelectedPictures.remove(picture);
        } else {
          mSelectedPictures.add(picture);
        }

        picture.setSelected(!picture.isSelected());
        View mask = view.findViewById(R.id.mask);
        ImageView indicator = (ImageView) view.findViewById(R.id.checkmark);

        if (picture.isSelected()) {//选中
          mask.setVisibility(View.VISIBLE);
          indicator.setVisibility(View.VISIBLE);
          indicator.setImageResource(R.drawable.ic_checkbox_red_checked);
        } else {//未选中
          mask.setVisibility(View.GONE);
          indicator.setVisibility(View.VISIBLE);
          indicator.setImageResource(R.drawable.ic_checkbox_white_unchecked);
        }
        if (mSelectedPictures.size() > 0) {
          mToolBar.setTitle("选择图片(" + mSelectedPictures.size() + ")");
        } else {
          mToolBar.setTitle("选择图片");
        }
      }
    });

    initToolBar();

    loadData();
  }

  private void initToolBar() {
    mToolBar = (Toolbar) findViewById(R.id.tool_bar);
    mToolBar.setTitle("选择图片");
    TextView mSettingView =
        (TextView) getLayoutInflater().inflate(R.layout.header_right, mToolBar, false);
    mSettingView.setText("完成");
    ((Toolbar.LayoutParams) mSettingView.getLayoutParams()).gravity = Gravity.END;
    mToolBar.addView(mSettingView);

    mSettingView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA, mSelectedPictures);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
      }
    });
  }

  /**
   * 加载图片
   */
  public void loadData() {
    Observable.create(new ObservableOnSubscribe<List<Picture>>() {
      @Override public void subscribe(ObservableEmitter<List<Picture>> subscriber)
          throws Exception {
        List<Picture> pictures = new ArrayList<>();
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = getContentResolver();
        //只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(mImageUri, null,
            MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
            new String[] { "image/jpeg", "image/png" }, "date_modified desc ");
        if (mCursor != null) {
          while (mCursor.moveToNext()) {
            //获取图片的路径
            String name =
                mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Picture picture = new Picture();
            picture.setFileName(name);
            picture.setFilePath(path);
            pictures.add(picture);
          }
          mCursor.close();
        }
        subscriber.onNext(pictures);
        subscriber.onComplete();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<Picture>>() {
          @Override public void accept(List<Picture> pictures) throws Exception {
            mPictures.addAll(pictures);
            mPictureListAdapter.notifyDataSetChanged();
          }
        }, new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) throws Exception {

          }
        });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }
}
