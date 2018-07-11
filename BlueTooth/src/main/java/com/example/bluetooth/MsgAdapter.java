package com.example.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by small_qi on 2017/9/13.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MsgHolder> {
  private Context mContext;
  private List<String> msgList;

  public MsgAdapter(Context mContext) {
    this.mContext = mContext;
    msgList = new ArrayList<>();
  }

  @Override public MsgAdapter.MsgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new MsgAdapter.MsgHolder(
        LayoutInflater.from(mContext).inflate(R.layout.item, parent, false));
  }

  @Override public void onBindViewHolder(MsgAdapter.MsgHolder holder, final int position) {
    holder.nameTv.setText(msgList.get(position));
  }

  @Override public int getItemCount() {
    return msgList.size();
  }

  public void addMessage(String msg) {
    msgList.add(msg);
    notifyItemInserted(msgList.size() - 1);
  }

  public void clearMsgList() {
    msgList.clear();
    notifyDataSetChanged();
  }

  public interface OnItemClickListener {
    void onClick(BluetoothDevice device);
  }

  class MsgHolder extends RecyclerView.ViewHolder {
    private TextView nameTv;

    public MsgHolder(View itemView) {
      super(itemView);
      nameTv = itemView.findViewById(R.id.name);
    }
  }
}
