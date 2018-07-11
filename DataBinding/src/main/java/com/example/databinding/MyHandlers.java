package com.example.databinding;

import android.view.View;
import android.widget.Toast;

public class MyHandlers {
  public void onClickFriend(View view) {
    Toast.makeText(view.getContext(), view.toString(), Toast.LENGTH_SHORT).show();
  }

  public boolean onLongClickFriend(View view) {
    Toast.makeText(view.getContext(), view.toString(), Toast.LENGTH_SHORT).show();
    return true;
  }
}
