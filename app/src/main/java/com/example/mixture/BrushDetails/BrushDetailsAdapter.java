package com.example.mixture.BrushDetails;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mixture.R;

import java.util.List;

public class BrushDetailsAdapter extends RecyclerView.Adapter<BrushDetailsAdapter.ViewHolder> {

    private List<BrushDetailsViewModel> items;
    private OnSeekBarChangeListener listener;

    public BrushDetailsAdapter(List<BrushDetailsViewModel> items, OnSeekBarChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public interface OnSeekBarChangeListener {
        void onValueChanged(int position, int value);
    }

    @NonNull
    @Override
    public BrushDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.brush_details, parent, false);
        return new BrushDetailsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BrushDetailsViewModel item = items.get(position);

        holder.textView.setText(item.getLabel());
        holder.seekBar.setProgress(item.getValue());

        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && listener != null) {
                    item.setValue(progress);
                    listener.onValueChanged(holder.getAdapterPosition(), progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        SeekBar seekBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.brush_details_lable);
            seekBar = itemView.findViewById(R.id.brush_details_seekBar);
        }
    }
}
