package com.example.mixture;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mixture.Settings.SettingsAdapter;
import com.example.mixture.Settings.SettingsViewModel;
import com.example.mixture.Utils.FolderPicker;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // 图片保存路径相关常量
    private static final String PREF_SAVE_PATH_URI = "image_save_path_uri";
    private static final String PREF_SAVE_FILE_NAME = "picture_editor_cfg";
    private static final String PREF_SAVE_PATH_DISPLAY = "picture_save_path";
    private static String PREF_SAVE_PATH_DEFAULT;

    private String mParam1;
    private String mParam2;

    // FolderPicker实例
    private FolderPicker folderPicker;
    private SettingsAdapter settingsAdapter;

    public MineFragment() {
        // Required empty public constructor
    }

    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // 初始化FolderPicker
        folderPicker = new FolderPicker(this);

        SharedPreferences prefs = requireActivity().getSharedPreferences(PREF_SAVE_FILE_NAME, Context.MODE_PRIVATE);
        PREF_SAVE_PATH_DEFAULT = requireActivity().getFilesDir().getAbsolutePath() + "/Pictures";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_settings);
        List<SettingsViewModel> items = new ArrayList<>();

        // 获取当前保存路径显示文本
        String currentPath = getCurrentSavePathDisplay();
        items.add(new SettingsViewModel(R.drawable.folder_24px, "图片保存路径", false, false, currentPath));
        items.add(new SettingsViewModel(R.drawable.contrast_24px, "更改主题", false, false));
        items.add(new SettingsViewModel(R.drawable.sync_24px, "检查更新", true, true));

        settingsAdapter = new SettingsAdapter(items, (item, position) -> {
            switch (position) {
                case 0:
                    showSavePathDialog();
                    break;
                case 1:
                    showThemeDialog();
                    break;
                case 2:
                    checkForUpdates();
                    break;
            }
        });
        recyclerView.setAdapter(settingsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    /**
     * 显示保存路径选择对话框
     */
    private void showSavePathDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择图片保存位置");

        String[] options = {"使用默认路径", "选择自定义路径", "查看当前路径"};

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    setDefaultSavePath();
                    break;
                case 1:
                    openFolderPicker();
                    break;
                case 2:
                    showCurrentPath();
                    break;
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * 设置默认保存路径
     */
    private void setDefaultSavePath() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREF_SAVE_FILE_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(PREF_SAVE_PATH_URI)
                .putString(PREF_SAVE_PATH_DISPLAY, PREF_SAVE_PATH_DEFAULT)
                .apply();

        Toast.makeText(getContext(), "已设置为默认保存路径", Toast.LENGTH_SHORT).show();
        refreshSettingsDisplay();
    }

    /**
     * 使用FolderPicker打开文件夹选择器
     */
    private void openFolderPicker() {
        folderPicker.openFolderPicker(new FolderPicker.OnFolderSelectedListener() {
            @Override
            public void onFolderSelected(FolderPicker.FolderInfo folderInfo) {
                // 文件夹选择成功
                handleFolderSelected(folderInfo);
            }

            @Override
            public void onFolderSelectionFailed(String error) {
                // 文件夹选择失败
                if (error != null) {
                    Toast.makeText(getContext(), "选择文件夹失败: " + error, Toast.LENGTH_LONG).show();
                }
                // error为null表示用户取消，不显示错误信息
            }
        });
    }

    /**
     * 处理文件夹选择成功
     */
    private void handleFolderSelected(FolderPicker.FolderInfo folderInfo) {
        if (!folderInfo.isWritable()) {
            Toast.makeText(getContext(), "警告: 选择的文件夹可能无法写入", Toast.LENGTH_LONG).show();
        }

        // 保存到SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREF_SAVE_FILE_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(PREF_SAVE_PATH_URI, folderInfo.getUri().toString())
                .putString(PREF_SAVE_PATH_DISPLAY, folderInfo.getDisplayPath())
                .apply();

        Toast.makeText(getContext(), "图片保存路径已设置: " + folderInfo.getDisplayPath(),
                Toast.LENGTH_LONG).show();

        // 刷新界面显示
        refreshSettingsDisplay();
    }

    /**
     * 显示当前保存路径信息
     */
    private void showCurrentPath() {
        String currentPath = getCurrentSavePathDisplay();
        Uri savedUri = getSavedFolderUri();

        String message = "当前图片保存路径:\n" + currentPath;

        if (savedUri != null) {
            message += "\n\nURI: " + savedUri.toString();

            // 使用FolderPicker验证路径有效性
            if (FolderPicker.isUriValid(getContext(), savedUri)) {
                message += "\n\n✅ 路径有效，可以正常保存";
            } else {
                message += "\n\n❌ 路径无效，请重新选择";
            }
        }

        new AlertDialog.Builder(getContext())
                .setTitle("当前保存路径")
                .setMessage(message)
                .setPositiveButton("确定", null)
                .setNeutralButton("重新选择", (dialog, which) -> openFolderPicker())
                .show();
    }

    /**
     * 获取当前保存路径显示文本
     */
    private String getCurrentSavePathDisplay() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREF_SAVE_FILE_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREF_SAVE_PATH_DISPLAY, "默认路径");
    }

    /**
     * 获取保存的文件夹URI
     */
    private Uri getSavedFolderUri() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREF_SAVE_FILE_NAME, Context.MODE_PRIVATE);
        String uriString = prefs.getString(PREF_SAVE_PATH_URI, null);
        return uriString != null ? Uri.parse(uriString) : null;
    }

    /**
     * 刷新设置界面显示
     */
    private void refreshSettingsDisplay() {
        if (settingsAdapter != null) {
            String currentPath = getCurrentSavePathDisplay();
            settingsAdapter.updateItemDescription(0, currentPath);
        }
    }

    /**
     * 获取图片保存的目标URI（供其他地方调用）
     */
    public Uri getImageSaveUri(String fileName) {
        Uri savedUri = getSavedFolderUri();

        if (savedUri != null) {
            // 使用FolderPicker在自定义路径创建文件
            return folderPicker.createFileInFolder(savedUri, fileName, "image/jpeg");
        }

        // 使用默认路径逻辑
        return getDefaultImageSaveUri(fileName);
    }

    /**
     * 获取默认图片保存URI
     */
    private Uri getDefaultImageSaveUri(String fileName) {
        // 这里实现默认保存逻辑
        return null;
    }

    /**
     * 检查更新
     */
    private void checkForUpdates() {
        Toast.makeText(getContext(), "检查更新功能待实现", Toast.LENGTH_SHORT).show();
    }

    private void showThemeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择主题");

        String[] themes = {"浅色主题", "深色主题"};

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isDark = prefs.getBoolean("is_dark_theme", false);
        int checkedItem = isDark ? 1 : 0;

        builder.setSingleChoiceItems(themes, checkedItem, (dialog, which) -> {
            boolean newIsDark = (which == 1);
            prefs.edit().putBoolean("is_dark_theme", newIsDark).apply();

            if (newIsDark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            dialog.dismiss();
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }
}