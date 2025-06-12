package com.example.mixture.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

/**
 * 文件夹选择器工具类
 * 封装了Android Storage Access Framework的文件夹选择功能
 */
public class FolderPicker {

    private Context context;
    private ActivityResultLauncher<Intent> folderPickerLauncher;
    private OnFolderSelectedListener listener;

    /**
     * 文件夹选择结果回调接口
     */
    public interface OnFolderSelectedListener {
        /**
         * 文件夹选择成功
         * @param folderInfo 选择的文件夹信息
         */
        void onFolderSelected(FolderInfo folderInfo);

        /**
         * 文件夹选择失败或取消
         * @param error 错误信息，null表示用户取消
         */
        void onFolderSelectionFailed(String error);
    }

    /**
     * 文件夹信息类
     */
    public static class FolderInfo {
        private Uri uri;
        private String displayName;
        private String displayPath;
        private boolean isWritable;

        public FolderInfo(Uri uri, String displayName, String displayPath, boolean isWritable) {
            this.uri = uri;
            this.displayName = displayName;
            this.displayPath = displayPath;
            this.isWritable = isWritable;
        }

        public Uri getUri() { return uri; }
        public String getDisplayName() { return displayName; }
        public String getDisplayPath() { return displayPath; }
        public boolean isWritable() { return isWritable; }

        @Override
        public String toString() {
            return "FolderInfo{" +
                    "displayName='" + displayName + '\'' +
                    ", displayPath='" + displayPath + '\'' +
                    ", isWritable=" + isWritable +
                    '}';
        }
    }

    /**
     * 构造函数 - 用于Activity
     * @param activity Activity实例
     */
    public FolderPicker(AppCompatActivity activity) {
        this.context = activity;
        initializeLauncher(activity, null);
    }

    /**
     * 构造函数 - 用于Fragment
     * @param fragment Fragment实例
     */
    public FolderPicker(Fragment fragment) {
        this.context = fragment.getContext();
        initializeLauncher(null, fragment);
    }

    /**
     * 初始化ActivityResultLauncher
     */
    private void initializeLauncher(AppCompatActivity activity, Fragment fragment) {
        if (activity != null) {
            folderPickerLauncher = activity.registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> handleResult(result.getResultCode(), result.getData())
            );
        } else if (fragment != null) {
            folderPickerLauncher = fragment.registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> handleResult(result.getResultCode(), result.getData())
            );
        }
    }

    /**
     * 打开文件夹选择器
     * @param listener 结果回调监听器
     */
    public void openFolderPicker(OnFolderSelectedListener listener) {
        this.listener = listener;

        if (folderPickerLauncher == null) {
            if (listener != null) {
                listener.onFolderSelectionFailed("FolderPicker未正确初始化");
            }
            return;
        }

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // 设置初始位置为外部存储根目录（API 26+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI,
                    DocumentsContract.buildRootUri("com.android.externalstorage.documents", "primary"));
        }

        try {
            folderPickerLauncher.launch(intent);
        } catch (Exception e) {
            if (listener != null) {
                listener.onFolderSelectionFailed("无法打开文件夹选择器: " + e.getMessage());
            }
        }
    }

    /**
     * 打开文件夹选择器并指定初始路径
     * @param listener 结果回调监听器
     * @param initialUri 初始路径URI
     */
    public void openFolderPicker(OnFolderSelectedListener listener, Uri initialUri) {
        this.listener = listener;

        if (folderPickerLauncher == null) {
            if (listener != null) {
                listener.onFolderSelectionFailed("FolderPicker未正确初始化");
            }
            return;
        }

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // 设置初始位置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && initialUri != null) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri);
        }

        try {
            folderPickerLauncher.launch(intent);
        } catch (Exception e) {
            if (listener != null) {
                listener.onFolderSelectionFailed("无法打开文件夹选择器: " + e.getMessage());
            }
        }
    }

    /**
     * 处理选择结果
     */
    private void handleResult(int resultCode, Intent data) {
        if (listener == null) return;

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri treeUri = data.getData();
            if (treeUri != null) {
                processFolderSelection(treeUri);
            } else {
                listener.onFolderSelectionFailed("无法获取文件夹URI");
            }
        } else {
            listener.onFolderSelectionFailed(null); // null表示用户取消
        }
    }

    /**
     * 处理文件夹选择
     */
    private void processFolderSelection(Uri treeUri) {
        try {
            // 获取持久化权限
            context.getContentResolver().takePersistableUriPermission(treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            // 创建文件夹信息
            FolderInfo folderInfo = createFolderInfo(treeUri);

            if (listener != null) {
                listener.onFolderSelected(folderInfo);
            }

        } catch (SecurityException e) {
            if (listener != null) {
                listener.onFolderSelectionFailed("无法获取文件夹权限: " + e.getMessage());
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onFolderSelectionFailed("处理文件夹选择时出错: " + e.getMessage());
            }
        }
    }

    /**
     * 创建文件夹信息对象
     */
    private FolderInfo createFolderInfo(Uri treeUri) {
        String displayName = "未知文件夹";
        String displayPath = "未知路径";
        boolean isWritable = false;

        try {
            DocumentFile documentFile = DocumentFile.fromTreeUri(context, treeUri);
            if (documentFile != null) {
                displayName = documentFile.getName() != null ? documentFile.getName() : "未知文件夹";
                isWritable = documentFile.canWrite();
            }

            // 尝试获取更友好的显示路径
            displayPath = getFriendlyPath(treeUri);

        } catch (Exception e) {
            // 使用默认值
        }

        return new FolderInfo(treeUri, displayName, displayPath, isWritable);
    }

    /**
     * 获取友好的路径显示
     */
    private String getFriendlyPath(Uri treeUri) {
        try {
            String docId = DocumentsContract.getTreeDocumentId(treeUri);
            String[] split = docId.split(":");
            String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
                if (split.length > 1) {
                    return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + split[1];
                } else {
                    return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
                }
            } else {
                // 外部存储卡等
                if (split.length > 1) {
                    return type + "/" + split[1];
                } else {
                    return type;
                }
            }
        } catch (Exception e) {
            return "自定义路径";
        }
    }

    /**
     * 验证URI是否仍然有效
     * @param uri 要验证的URI
     * @return 是否有效
     */
    public static boolean isUriValid(Context context, Uri uri) {
        if (uri == null || context == null) {
            return false;
        }

        try {
            DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri);
            return documentFile != null && documentFile.exists();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 在指定文件夹中创建文件
     * @param folderUri 文件夹URI
     * @param fileName 文件名
     * @param mimeType MIME类型
     * @return 创建的文件URI，失败返回null
     */
    public Uri createFileInFolder(Uri folderUri, String fileName, String mimeType) {
        if (folderUri == null || fileName == null || context == null) {
            return null;
        }

        try {
            DocumentFile documentFile = DocumentFile.fromTreeUri(context, folderUri);
            if (documentFile != null && documentFile.canWrite()) {
                DocumentFile newFile = documentFile.createFile(mimeType, fileName);
                return newFile != null ? newFile.getUri() : null;
            }
        } catch (Exception e) {
            // 创建失败
        }

        return null;
    }

    /**
     * 获取文件夹中的所有文件
     * @param folderUri 文件夹URI
     * @return 文件列表，失败返回null
     */
    public DocumentFile[] listFiles(Uri folderUri) {
        if (folderUri == null || context == null) {
            return null;
        }

        try {
            DocumentFile documentFile = DocumentFile.fromTreeUri(context, folderUri);
            if (documentFile != null && documentFile.isDirectory()) {
                return documentFile.listFiles();
            }
        } catch (Exception e) {
            // 列出文件失败
        }

        return null;
    }
}
