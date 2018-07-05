package com.example.livedata;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.List;

public class LiveDataFragment extends Fragment {
  private static final String TAG = "LiveDataFragment";
  private NameViewModel mNameViewModel;
  @BindView(R.id.tv_name) TextView mTvName;

  public static LiveDataFragment getInstance() {
    return new LiveDataFragment();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mNameViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance((Application) getActivity().getApplicationContext())
      .create(NameViewModel.class);

    // 订阅LiveData中当前Name数据变化，以lambda形式定义Observer
    mNameViewModel.getCurrentName().observe(this, (String name) -> {
      mTvName.setText(name);
      Log.d(TAG, "currentName: " + name);
    });

    // 订阅LiveData中Name列表数据变化，以lambda形式定义Observer
    mNameViewModel.getNameList().observe(this, (List<String> nameList) -> {
      if (nameList == null) {
        return;
      }
      for (String item : nameList) {
        Log.d(TAG, "name: " + item);
      }
    });
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.layout_livedata, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @OnClick({ R.id.btn_change_name, R.id.btn_update_list })
  void onClicked(View view) {
    switch (view.getId()) {
      case R.id.btn_change_name:
        mNameViewModel.getCurrentName().setValue("Jane");
        break;
      case R.id.btn_update_list:
        List<String> nameList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
          nameList.add("Jane<" + i + ">");
        }
        mNameViewModel.getNameList().setValue(nameList);
        break;
    }
  }
}