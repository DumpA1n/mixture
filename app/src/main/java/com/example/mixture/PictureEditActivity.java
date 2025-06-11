package com.example.mixture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mixture.BrushDetails.BrushDetailsAdapter;
import com.example.mixture.BrushDetails.BrushDetailsViewModel;
import com.example.mixture.UniversalItems.ItemAdapter;
import com.example.mixture.UniversalItems.ItemViewModel;
import com.example.mixture.Utils.ImageViewUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.ViewType;

public class PictureEditActivity extends AppCompatActivity {
    private PhotoEditorView photoEditorView;
    private PhotoEditor photoEditor;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private Uri selectedSaveUri = null;
    public static final int GET_PATH_REQUEST_CODE = 1001;
    private boolean isBrushEnabel = false;
    private List<BrushDetailsViewModel> mBrushDetailsItems;
    private BrushDetailsAdapter mBrushDetailsAdapter;
    RecyclerView recyclerViewSecondary;

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

        photoEditorView = findViewById(R.id.photoEditorView);
        photoEditor = new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true) // 允许文字缩放
                .setDefaultTextTypeface(Typeface.DEFAULT_BOLD) // 设置默认字体
                .setDefaultEmojiTypeface(Typeface.DEFAULT) // 设置表情字体
                .build();

        loadImageFromIntent();

        imageView = photoEditorView.getSource();



        recyclerView = findViewById(R.id.recyclerView_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<ItemViewModel> itemList = new ArrayList<>();
        itemList.add(new ItemViewModel(R.drawable.crop_24px, "裁剪", "crop"));
        itemList.add(new ItemViewModel(R.drawable.rotate_90_degrees_cw_24px, "旋转", "rotate"));
        itemList.add(new ItemViewModel(R.drawable.text_fields_24px, "文字", "text"));
        itemList.add(new ItemViewModel(R.drawable.draw_24px, "画笔", "brush"));
        itemList.add(new ItemViewModel(R.drawable.add_reaction_24px, "表情", "emoji"));
        // itemList.add(new ItemViewModel(R.drawable.ar_stickers_24px, "贴纸", "sticker"));
        itemList.add(new ItemViewModel(R.drawable.filter_vintage_24px, "滤镜", "filter"));

        ItemAdapter itemAdapter = new ItemAdapter(itemList, item -> {
            switch (item.actionType) {
                case "rotate":
                    rotateImage();
                    break;
                case "text":
                    addText();
                    break;
                case "brush":
                    enableBrushDrawing();
                    break;
                case "emoji":
                    addEmoji();
                    break;
                case "sticker":
                    addSticker();
                    break;
                case "filter":
                    applyFilter();
                    break;
                case "crop":
                    Uri sourceUri = ImageViewUtils.getImageViewUri(this, imageView);
                    Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg"));
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


        // 创建画笔参数RecyclerView并添加监听
        recyclerViewSecondary = findViewById(R.id.recyclerView_secondary);
        recyclerViewSecondary.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mBrushDetailsItems = new ArrayList<>();
        mBrushDetailsItems.add(new BrushDetailsViewModel("粗细", 10));

        mBrushDetailsAdapter = new BrushDetailsAdapter(mBrushDetailsItems, new BrushDetailsAdapter.OnSeekBarChangeListener() {
            @Override
            public void onValueChanged(int position, int value) {
                if (mBrushDetailsItems.get(position).getLabel().equals("粗细")) {
                    photoEditor.setBrushSize((float) value);
                }
            }
        });
        recyclerViewSecondary.setAdapter(mBrushDetailsAdapter);


        findViewById(R.id.imageButton_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaveDialog();
            }
        });

        findViewById(R.id.imageButton_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.imageButton_undo).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageButton_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoEditor.undo();
            }
        });

        findViewById(R.id.imageButton_redo).setVisibility(View.INVISIBLE);
        findViewById(R.id.imageButton_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoEditor.redo();
            }
        });

        // 返回操作回调
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
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
        else if (requestCode == GET_PATH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedSaveUri = data.getData();
            Toast.makeText(this, "已选择路径: " + selectedSaveUri.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImageFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String imageUriExtra = intent.getStringExtra("imageUri");
            if (imageUriExtra != null) {
                Uri imageUri = Uri.parse(imageUriExtra);
                try {
                    photoEditorView.getSource().setImageURI(imageUri);
                } catch (Exception e) {
                    Toast.makeText(this, "加载图片失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void rotateImage() {
        Drawable drawable = photoEditorView.getSource().getDrawable();
        if (drawable == null || !(drawable instanceof BitmapDrawable)) {
            Toast.makeText(this, "无法旋转：未找到有效图片", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap originalBitmap = ((BitmapDrawable) drawable).getBitmap();
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap rotatedBitmap = Bitmap.createBitmap(
                originalBitmap,
                0,
                0,
                originalBitmap.getWidth(),
                originalBitmap.getHeight(),
                matrix,
                true
        );
        photoEditorView.getSource().setImageBitmap(rotatedBitmap);
    }


    private void addText() {
        // 添加文字
        photoEditor.addText("输入文字", Color.BLACK);

        photoEditor.setOnPhotoEditorListener(new OnPhotoEditorListener() {
            @Override
            public void onEditTextChangeListener(View rootView, String text, int colorCode) {
                AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
                builder.setTitle("编辑文字");

                final EditText input = new EditText(rootView.getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(text);
                builder.setView(input);

                builder.setPositiveButton("确定", (dialog, which) -> {
                    photoEditor.editText(rootView, input.getText().toString(), colorCode);
                });
                builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());
                builder.show();
            }
            @Override
            public void onTouchSourceImage(@NonNull MotionEvent motionEvent) {}
            @Override
            public void onStopViewChangeListener(@NonNull ViewType viewType) {}
            @Override
            public void onStartViewChangeListener(@NonNull ViewType viewType) {}
            @Override
            public void onRemoveViewListener(@NonNull ViewType viewType, int i) {}
            @Override
            public void onAddViewListener(@NonNull ViewType viewType, int i) {}
        });
    }

    private void showTextInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加文字");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String text = input.getText().toString();
            if (!text.isEmpty()) {
                photoEditor.addText(text, Color.BLACK);
            }
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void enableBrushDrawing() {
        // 显示二级菜单
        ConstraintLayout overlayContainer = findViewById(R.id.overlay_container);

        // 启用画笔模式
        if (isBrushEnabel) {
            photoEditor.setBrushDrawingMode(false);
            overlayContainer.setVisibility(View.GONE);
            isBrushEnabel  = false;
            return;
        } else {
            photoEditor.setBrushDrawingMode(true);
            overlayContainer.setVisibility(View.VISIBLE);
            isBrushEnabel  = true;
        }

        // ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(this);

        // 设置画笔颜色和大小
        photoEditor.setBrushColor(Color.RED);
        photoEditor.setBrushSize((float) mBrushDetailsItems.get(0).getValue());
    }

    private void addEmoji() {
        // 添加表情符号
        // photoEditor.addEmoji("😀");
        // 表情选择器
        showEmojiPicker();
    }

    private void showEmojiPicker() {
        String[] emojis = {"😀", "😂", "😍", "😢", "😡", "👍", "❤️", "🎉"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择表情");

        GridView gridView = new GridView(this);
        gridView.setNumColumns(4);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, emojis) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextSize(24);
                textView.setGravity(Gravity.CENTER);
                return textView;
            }
        };

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            photoEditor.addEmoji(emojis[position]);
        });

        builder.setView(gridView);
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    // 添加贴纸
    private void addSticker() {
        Bitmap stickerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ar_stickers_24px);
        if (stickerBitmap == null) {
            Toast.makeText(this, "加载贴纸失败", Toast.LENGTH_SHORT).show();
            return;
        }
        photoEditor.addImage(stickerBitmap);
    }

    private void applyFilter() {
        // 应用滤镜
        showFilterDialog();
    }

    private void showFilterDialog() {
        String[] filters = {"原图", "黑白", "复古", "冷色调", "暖色调"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择滤镜");
        builder.setItems(filters, (dialog, which) -> {
            switch (which) {
                case 0:
                    // photoEditor.clearAllViews(); // 清除所有效果
                    applyOriginFilter();
                    break;
                case 1:
                    applyGrayscaleFilter();
                    break;
                case 2:
                    applyVintageFilter();
                    break;
                case 3:
                    applyCoolFilter();
                    break;
                case 4:
                    applyWarmFilter();
                    break;
            }
        });
        builder.show();
    }

    private void applyOriginFilter() {
        // 应用冷色调滤镜
        ColorMatrix colorMatrix = new ColorMatrix();
        float[] coolMatrix = {
                1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
        };
        colorMatrix.set(coolMatrix);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        photoEditorView.getSource().setColorFilter(filter);
    }

    private void applyGrayscaleFilter() {
        // 应用黑白滤镜
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0); // 设置饱和度为0实现黑白效果

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        photoEditorView.getSource().setColorFilter(filter);
    }

    private void applyVintageFilter() {
        // 应用复古滤镜
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.6f);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        photoEditorView.getSource().setColorFilter(filter);
    }

    private void applyCoolFilter() {
        // 应用冷色调滤镜
        ColorMatrix colorMatrix = new ColorMatrix();
        float[] coolMatrix = {
                1.0f, 0.0f, 0.2f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.2f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
        };
        colorMatrix.set(coolMatrix);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        photoEditorView.getSource().setColorFilter(filter);
    }

    private void applyWarmFilter() {
        // 应用暖色调滤镜
        ColorMatrix colorMatrix = new ColorMatrix();
        float[] warmMatrix = {
                1.2f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.8f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
        };
        colorMatrix.set(warmMatrix);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        photoEditorView.getSource().setColorFilter(filter);
    }

    private void showSaveDialog() {
        new AlertDialog.Builder(this)
                .setTitle("保存图片")
                .setMessage("是保存到相册？")
                .setPositiveButton("是", (dialog, which) -> saveImage(true))  // 保存到相册
                .setNegativeButton("否", (dialog, which) -> saveImage(false)) // 保存到内部目录
                .show();
    }

    private void saveImage(boolean toAlbum) {
        // 保存编辑后的图片
        SaveSettings saveSettings = new SaveSettings.Builder()
                .setClearViewsEnabled(false)
                .setTransparencyEnabled(true)
                .build();

        String fileName = System.currentTimeMillis() + ".jpg";
        String savePath;

        if (toAlbum) {
            savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + fileName;
        } else {
            savePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + fileName;
        }

        photoEditor.saveAsFile(savePath,
                saveSettings, new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        Toast.makeText(PictureEditActivity.this, "图片保存成功: " + imagePath, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(PictureEditActivity.this, "保存失败: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}