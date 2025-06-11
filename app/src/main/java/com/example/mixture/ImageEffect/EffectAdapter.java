package com.example.mixture.ImageEffect;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mixture.R;

import java.util.List;

public class EffectAdapter extends RecyclerView.Adapter<EffectAdapter.EffectViewHolder> {
    private List<EffectType> effectList;
    private OnEffectClickListener listener;
    private ImageView previewImageView;

    public interface OnEffectClickListener {
        void onEffectClick(EffectType effectType);
    }

    public EffectAdapter(List<EffectType> effectList, ImageView previewImageView, OnEffectClickListener listener) {
        this.effectList = effectList;
        this.previewImageView = previewImageView;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EffectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_effect, parent, false);
        return new EffectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EffectViewHolder holder, int position) {
        EffectType effect = effectList.get(position);
        holder.bind(effect);
    }

    @Override
    public int getItemCount() {
        return effectList.size();
    }

    class EffectViewHolder extends RecyclerView.ViewHolder {
        private ImageView effectPreview;
        private TextView effectName;

        public EffectViewHolder(@NonNull View itemView) {
            super(itemView);
            effectPreview = itemView.findViewById(R.id.effect_preview);
            effectName = itemView.findViewById(R.id.effect_name);
        }

        public void bind(EffectType effect) {
            effectName.setText(effect.getDisplayName());

            // 创建预览效果
            createEffectPreview(effect);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEffectClick(effect);
                }
            });
        }

        private void createEffectPreview(EffectType effect) {
            if (previewImageView.getDrawable() != null) {
                Drawable drawable = previewImageView.getDrawable();
                Bitmap originalBitmap = drawableToBitmap(drawable);
                Bitmap previewBitmap = applyEffectToBitmap(originalBitmap, effect);
                effectPreview.setImageBitmap(previewBitmap);
            }
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private Bitmap applyEffectToBitmap(Bitmap original, EffectType effect) {
        Bitmap result = original.copy(original.getConfig(), true);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();

        switch (effect) {
            case GRAYSCALE:
                ColorMatrix grayMatrix = new ColorMatrix();
                grayMatrix.setSaturation(0);
                paint.setColorFilter(new ColorMatrixColorFilter(grayMatrix));
                break;
            case SEPIA:
                ColorMatrix sepiaMatrix = new ColorMatrix();
                sepiaMatrix.setScale(1f, 0.95f, 0.8f, 1f);
                paint.setColorFilter(new ColorMatrixColorFilter(sepiaMatrix));
                break;
            case NEGATIVE:
                ColorMatrix negativeMatrix = new ColorMatrix(new float[]{
                        -1, 0, 0, 0, 255,
                        0, -1, 0, 0, 255,
                        0, 0, -1, 0, 255,
                        0, 0, 0, 1, 0
                });
                paint.setColorFilter(new ColorMatrixColorFilter(negativeMatrix));
                break;
            default:
                return result;
        }

        canvas.drawBitmap(original, 0, 0, paint);
        return result;
    }
}
