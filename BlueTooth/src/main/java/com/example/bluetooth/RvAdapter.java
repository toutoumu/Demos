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

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.RvHolder> {
  private Context mContext;
  private List<BluetoothDevice> mDevices;
  private OnItemClickListener onItemClickListener;

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  public RvAdapter(Context mContext) {
    this.mContext = mContext;
    mDevices = new ArrayList<>();
  }

  @Override public RvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new RvHolder(LayoutInflater.from(mContext).inflate(R.layout.item, parent, false));
  }

  @Override public void onBindViewHolder(RvHolder holder, final int position) {
    holder.nameTv.setText(
        mDevices.get(position).getName() + ":" + mDevices.get(position).getAddress());
    //点击事件 点击配对
    if (onItemClickListener != null) {
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          onItemClickListener.onClick(mDevices.get(position));
        }
      });
    }
  }

  @Override public int getItemCount() {
    return mDevices.size();
  }

  public void addDevice(BluetoothDevice device) {
    mDevices.add(device);
    notifyItemInserted(mDevices.size() - 1);
  }

  public void clearDevices() {
    mDevices.clear();
    notifyDataSetChanged();
  }

  public interface OnItemClickListener {
    void onClick(BluetoothDevice device);
  }

  class RvHolder extends RecyclerView.ViewHolder {
    private TextView nameTv;

    public RvHolder(View itemView) {
      super(itemView);
      nameTv = itemView.findViewById(R.id.name);
    }
  }
}
