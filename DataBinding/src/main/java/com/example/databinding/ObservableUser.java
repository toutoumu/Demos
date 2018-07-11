package com.example.databinding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * 双向数据绑定使用这个
 */
public class ObservableUser extends BaseObservable {
  private String firstName;
  private String lastName;
  private boolean isAdult;

  private int padding;

  @Bindable
  public int getPadding() {
    return padding;
  }

  @Bindable
  public boolean isAdult() {
    return isAdult;
  }

  @Bindable
  public String getFirstName() {
    return firstName;
  }

  @Bindable
  public String getLastName() {
    return lastName;
  }

  public void setPadding(int padding) {
    this.padding = padding;
    notifyPropertyChanged(BR.padding);
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
    notifyPropertyChanged(BR.firstName);
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
    notifyPropertyChanged(BR.lastName);
  }

  public void setAdult(boolean adult) {
    isAdult = adult;
    notifyPropertyChanged(BR.adult);
  }
}