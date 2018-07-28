package com.example.databinding;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import com.example.databinding.databinding.ActivityDynamicBinding;
import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView使用数据绑定
 */
public class AdapterBindingActivity extends AppCompatActivity {

  private final List<User> mData = new ArrayList<>();
  ActivityDynamicBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = DataBindingUtil.setContentView(this, R.layout.activity_dynamic);

    binding.recyclerView.setHasFixedSize(true);
    binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    binding.recyclerView.setAdapter(new AdapterBindingAdapter(mData));
  }

  public void addData(View view) {
    for (int i = 0; i < 10; i++) {
      User user = new User(Randoms.nextFirstName(), Randoms.nextLastName(), false);
      mData.add(user);
    }
    binding.recyclerView.getAdapter().notifyDataSetChanged();
  }
}