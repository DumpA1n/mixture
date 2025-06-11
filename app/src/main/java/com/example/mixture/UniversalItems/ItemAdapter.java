package com.example.mixture.UniversalItems;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mixture.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<ItemViewModel> itemList;
    private OnItemClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnItemClickListener {
        void onItemClick(ItemViewModel item);
    }

    public ItemAdapter(List<ItemViewModel> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemViewModel item = itemList.get(position);

        holder.imageView.setImageResource(item.imageResId);
        holder.textView.setText(item.text);

        // 判断是否为选中项
        if (position == selectedPosition) {
            holder.imageView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_selected_bg));
            // holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_selected_text));
        } else {
            holder.imageView.setBackgroundColor(Color.TRANSPARENT);
            // holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.item_normal_text));
        }

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (item.actionType.equals("brush")) {
                int previousPosition = selectedPosition;
                // 如果点击的是已选中的 item，取消选中
                if (selectedPosition == holder.getAdapterPosition()) {
                    selectedPosition = RecyclerView.NO_POSITION;
                    notifyItemChanged(previousPosition);
                } else {
                    selectedPosition = holder.getAdapterPosition();
                    notifyItemChanged(previousPosition);
                    notifyItemChanged(selectedPosition);
                }
            }

            if (listener != null) {
                listener.onItemClick(item);
            }
        });

    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            textView = itemView.findViewById(R.id.brush_details_lable);
        }
        public void bind(final ItemViewModel item) {
            imageView.setImageResource(item.imageResId);
            textView.setText(item.text);
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
