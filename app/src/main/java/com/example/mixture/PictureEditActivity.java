package com.example.mixture;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mixture.EditorItemUI.ItemAdapter;
import com.example.mixture.EditorItemUI.ItemModel;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoEditor;

public class PictureEditActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_picture_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // PhotoEditorView photoEditorView = findViewById(R.id.photoEditorView);
        //
        // // 初始化 PhotoEditor
        // PhotoEditor photoEditor = new PhotoEditor.Builder(this, photoEditorView)
        //         .setPinchTextScalable(true) // 支持文字缩放
        //         .build();


        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        if (intent != null){
            String imageUriExtra = intent.getStringExtra("imageUri");
            if (imageUriExtra != null) {
                Uri imageUri = Uri.parse(imageUriExtra);
                imageView.setImageURI(imageUri);
            }
        }

        AtomicReference<Float> currentRotation = new AtomicReference<>(0f);
        AtomicReference<Float> currentScale = new AtomicReference<>(1f);

        imageView.setScaleType(ImageView.ScaleType.MATRIX); // 可选：便于自由缩放

        RecyclerView recyclerView = findViewById(R.id.recyclerView_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<ItemModel> itemList = new ArrayList<>();
        itemList.add(new ItemModel(R.drawable.radio_button_unchecked_24px, "旋转", "rotate"));
        itemList.add(new ItemModel(R.drawable.radio_button_unchecked_24px, "放大", "scale_up"));
        itemList.add(new ItemModel(R.drawable.radio_button_unchecked_24px, "缩小", "scale_down"));
        itemList.add(new ItemModel(R.drawable.radio_button_unchecked_24px, "裁剪", "crop"));
        itemList.add(new ItemModel(R.drawable.radio_button_unchecked_24px, "选项", "none"));
        itemList.add(new ItemModel(R.drawable.radio_button_unchecked_24px, "选项", "none"));
        itemList.add(new ItemModel(R.drawable.radio_button_unchecked_24px, "选项", "none"));
        itemList.add(new ItemModel(R.drawable.radio_button_unchecked_24px, "选项", "none"));
        itemList.add(new ItemModel(R.drawable.radio_button_unchecked_24px, "选项", "none"));
        itemList.add(new ItemModel(R.drawable.radio_button_unchecked_24px, "选项", "none"));
        itemList.add(new ItemModel(R.drawable.radio_button_unchecked_24px, "选项", "none"));
        itemList.add(new ItemModel(R.drawable.radio_button_unchecked_24px, "选项", "none"));
        itemList.add(new ItemModel(R.drawable.radio_button_unchecked_24px, "选项", "none"));
        itemList.add(new ItemModel(R.drawable.radio_button_unchecked_24px, "选项", "none"));

        ItemAdapter itemAdapter = new ItemAdapter(itemList, item -> {
            switch (item.actionType) {
                case "rotate":
                    currentRotation.updateAndGet(v -> new Float((float) (v + 90)));
                    imageView.setRotation(currentRotation.get());
                    break;
                case "scale_up":
                    currentScale.updateAndGet(v -> new Float((float) (v + 0.1f)));
                    imageView.setScaleX(currentScale.get());
                    imageView.setScaleY(currentScale.get());
                    break;
                case "scale_down":
                    currentScale.set(Math.max(0.1f, currentScale.get() - 0.1f));
                    imageView.setScaleX(currentScale.get());
                    imageView.setScaleY(currentScale.get());
                    break;
                case "crop":
                    // 使用 uCrop 裁剪，确保你添加了 uCrop 依赖
                    Uri sourceUri = Uri.parse(getIntent().getStringExtra("imageUri"));
                    Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped.jpg"));

                    UCrop.of(sourceUri, destinationUri)
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(1000, 1000)
                            .start(this);
                    break;
                default:
                    Toast.makeText(this, "未知操作", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(itemAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            final Uri resultUri = UCrop.getOutput(data);
            imageView.setImageURI(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(this, "裁剪失败", Toast.LENGTH_SHORT).show();
        }
    }
}