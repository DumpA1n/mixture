package com.example.mixture.ColorPicker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mixture.R;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ViewHolder> {

    private Context context;
    private List<Integer> colorList;
    private OnColorPickerClickListener onColorPickerClickListener;
    private int selectedPosition = 0; // 默认选中第一个颜色

    public ColorPickerAdapter(Context context) {
        this.context = context;
        this.colorList = getDefaultColors();
    }

    public ColorPickerAdapter(Context context, List<Integer> colorList) {
        this.context = context;
        this.colorList = colorList;
    }

    // 获取默认颜色列表
    private List<Integer> getDefaultColors() {
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);
        colors.add(Color.CYAN);
        colors.add(Color.BLACK);
        colors.add(Color.WHITE);
        colors.add(Color.GRAY);
        colors.add(Color.parseColor("#FF9800")); // Orange
        colors.add(Color.parseColor("#9C27B0")); // Purple
        colors.add(Color.parseColor("#795548")); // Brown
        colors.add(Color.parseColor("#03A9F4")); // Light Blue
        colors.add(Color.parseColor("#4CAF50")); // Green (Material)
        colors.add(Color.parseColor("#8BC34A")); // Light Green
        colors.add(Color.parseColor("#CDDC39")); // Lime
        colors.add(Color.parseColor("#FFEB3B")); // Yellow (Material)
        colors.add(Color.parseColor("#FFC107")); // Amber
        colors.add(Color.parseColor("#FF5722")); // Deep Orange
        colors.add(Color.parseColor("#E91E63")); // Pink
        colors.add(Color.parseColor("#F44336")); // Red (Material)
        colors.add(Color.parseColor("#607D8B")); // Blue Grey
        colors.add(Color.parseColor("#00BCD4")); // Cyan (Material)
        colors.add(Color.parseColor("#009688")); // Teal
        colors.add(Color.parseColor("#D32F2F")); // Dark Red
        colors.add(Color.parseColor("#1976D2")); // Dark Blue
        colors.add(Color.parseColor("#388E3C")); // Dark Green
        return colors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.color_picker_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int color = colorList.get(position);
        holder.colorView.setBackgroundColor(color);

        if (position == selectedPosition) {
            holder.colorView.setScaleX(1.2f);
            holder.colorView.setScaleY(1.2f);
            holder.colorView.setElevation(8f);
        } else {
            holder.colorView.setScaleX(1.0f);
            holder.colorView.setScaleY(1.0f);
            holder.colorView.setElevation(0f);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getPosition();

            // 更新UI
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            // 回调颜色选择
            if (onColorPickerClickListener != null) {
                onColorPickerClickListener.onColorPickerClickListener(color);
            }
        });
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public void setOnColorPickerClickListener(OnColorPickerClickListener listener) {
        this.onColorPickerClickListener = listener;
    }

    public int getSelectedColor() {
        return colorList.get(selectedPosition);
    }

    public void setSelectedColor(int color) {
        for (int i = 0; i < colorList.size(); i++) {
            if (colorList.get(i) == color) {
                int previousPosition = selectedPosition;
                selectedPosition = i;
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                break;
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View colorView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.color_picker_view);
        }
    }

    public interface OnColorPickerClickListener {
        void onColorPickerClickListener(int colorCode);
    }
}
