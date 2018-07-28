package com.example.databinding;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.databinding.databinding.UserItemBinding;
import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView使用数据绑定
 */
public class AdapterBindingAdapter extends RecyclerView.Adapter<AdapterBindingAdapter.UserHolder> {

  @NonNull private final List<User> mUsers;

  public AdapterBindingAdapter(@NonNull List<User> data) {
    mUsers = data;
  }

  @NonNull
  @Override
  public UserHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    // 也可以这样
    /*UserItemBinding binding =
      DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.user_item, viewGroup, false);
    return new UserHolder(binding.getRoot(), binding);*/

    View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item, viewGroup, false);
    return new UserHolder(itemView);
  }

  @Override
  public void onBindViewHolder(UserHolder holder, int position) {
    holder.bind(mUsers.get(position));
  }

  @Override
  public int getItemCount() {
    return mUsers.size();
  }

  public static class UserHolder extends RecyclerView.ViewHolder {
    private UserItemBinding mBinding;

    public UserHolder(View itemView) {
      super(itemView);
      mBinding = DataBindingUtil.bind(itemView);
    }

    public UserHolder(View itemView, UserItemBinding binding) {
      super(itemView);
      mBinding = binding;
    }

    public void bind(@NonNull User user) {
      mBinding.setUser(user);
    }
  }
}
