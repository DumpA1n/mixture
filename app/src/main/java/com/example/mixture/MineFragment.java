package com.example.mixture;

import static androidx.core.app.ActivityCompat.recreate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.mixture.Settings.SettingsAdapter;
import com.example.mixture.Settings.SettingsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MineFragment.
     */
    // TODO: Rename and change types and number of parameters
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_settings);
        List<SettingsViewModel> items = new ArrayList<>();
        items.add(new SettingsViewModel(R.drawable.folder_24px, "文件保存路径", false, false));
        items.add(new SettingsViewModel(R.drawable.contrast_24px, "更改主题", false, false));
        items.add(new SettingsViewModel(R.drawable.language_24px, "更改语言", false, false));
        items.add(new SettingsViewModel(R.drawable.sync_24px, "检查更新", true, true));

        SettingsAdapter adapter = new SettingsAdapter(items, (item, position) -> {
            switch (position) {
                case 0:
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, PictureEditActivity.GET_PATH_REQUEST_CODE);
                    break;
                case 1:
                    showThemeDialog();
                    break;
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Inflate the layout for this fragment
        return view;
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