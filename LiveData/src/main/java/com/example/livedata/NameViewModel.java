package com.example.livedata;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import java.util.List;

public class NameViewModel extends AndroidViewModel {
  // Create a LiveData with a String
  private MutableLiveData<String> mCurrentName;
  // Create a LiveData with a String list
  private MutableLiveData<List<String>> mNameListData;

  public NameViewModel(@NonNull Application application) {
    super(application);
  }

  public LiveData<String> getNameWithExt() {
    return Transformations.map(getCurrentName(), user -> user + "abc");
  }

  public MutableLiveData<String> getCurrentName() {
    if (mCurrentName == null) {
      mCurrentName = new MutableLiveData<>();
    }
    return mCurrentName;
  }

  public MutableLiveData<List<String>> getNameList() {
    if (mNameListData == null) {
      mNameListData = new MutableLiveData<>();
    }
    return mNameListData;
  }
}