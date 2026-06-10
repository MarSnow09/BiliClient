package com.RobinNotBad.BiliClient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.RobinNotBad.BiliClient.R;
import com.RobinNotBad.BiliClient.model.CoinLog;

import java.util.List;

public class CoinLogAdapter extends RecyclerView.Adapter<CoinLogAdapter.ViewHolder> {
    private final Context context;
    private final List<CoinLog> logList;

    public CoinLogAdapter(Context context, List<CoinLog> logList) {
        this.context = context;
        this.logList = logList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_coin_log, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < 0 || position >= logList.size()) return;
        CoinLog log = logList.get(position);
        if (log == null) return;

        holder.delta.setText(log.delta > 0 ? "+" + log.delta : String.valueOf(log.delta));
        holder.reason.setText(log.reason);
        holder.time.setText(log.time);
    }

    @Override
    public int getItemCount() {
        return logList != null ? logList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView delta;
        public final TextView reason;
        public final TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            delta = itemView.findViewById(R.id.delta);
            reason = itemView.findViewById(R.id.reason);
            time = itemView.findViewById(R.id.time);
        }
    }
}
