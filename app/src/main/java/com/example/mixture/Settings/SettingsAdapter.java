package com.example.mixture.Settings;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mixture.R;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private List<SettingsViewModel> items;
    private OnSettingItemClickListener listener;

    public SettingsAdapter(List<SettingsViewModel> items, OnSettingItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public interface OnSettingItemClickListener {
        void onItemClick(SettingsViewModel item, int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_switch, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingsViewModel item = items.get(position);
        holder.icon.setImageResource(item.iconResId);
        holder.title.setText(item.title);
        holder.aSwitch.setChecked(item.switchState);

        holder.aSwitch.setVisibility(item.hasSwitch ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> {
            // Switch切换逻辑
            if (item.hasSwitch) {
                holder.aSwitch.toggle();
                item.switchState = holder.aSwitch.isChecked();
            }
            // item点击逻辑
            if (listener != null) {
                listener.onItemClick(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        Switch aSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon_settings);
            title = itemView.findViewById(R.id.title_settings);
            aSwitch = itemView.findViewById(R.id.switch_settings);
        }
    }
}

