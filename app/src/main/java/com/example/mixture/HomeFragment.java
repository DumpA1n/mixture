package com.example.mixture;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);



        TextView textView = view.findViewById(R.id.textview_description);
        String fullText = getString(R.string.jump_to_github);
        SpannableString spannableString = new SpannableString(fullText);

        // 设置“点击这里”这段文字可点击
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // 跳转到网页链接
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DumpA1n/mixture"));
                widget.getContext().startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);       // 链接颜色
                ds.setUnderlineText(true);     // 是否显示下划线
            }
        };

        // 设置这段文字范围内添加点击事件
        String targetStr = "Click here";
        int start = fullText.indexOf(targetStr);
        int end = start + targetStr.length();
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance()); // 使点击事件生效
        textView.setHighlightColor(Color.TRANSPARENT); // 点击时不要高亮背景

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateTextViews(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTextViews(getView());
    }

    private void updateTextViews(View view) {
        if (view == null) return;

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("picture_editor_cfg", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String modelPath = sharedPreferences.getString("model_save_path", null);
        if (modelPath == null) {
            modelPath = requireActivity().getFilesDir().getAbsolutePath() + "/models";
            File targetDir = new File(modelPath);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            editor.putString("model_save_path", modelPath);
        }

        String picturePath = sharedPreferences.getString("picture_save_path", null);
        if (picturePath == null) {
            picturePath = requireActivity().getFilesDir().getAbsolutePath() + "/Pictures";
            File targetDir = new File(picturePath);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            editor.putString("picture_save_path", picturePath);
        }

        String deviceName = sharedPreferences.getString("device_name", null);
        if (deviceName == null) {
            deviceName = Build.BRAND + " " + Build.MODEL;
            editor.putString("device_name", deviceName);
        }

        String systemVersion = sharedPreferences.getString("system_version", null);
        if (systemVersion == null) {
            systemVersion = Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")";
            editor.putString("system_version", systemVersion);
        }

        String fingerprint = sharedPreferences.getString("fingerprint", null);
        if (fingerprint == null) {
            fingerprint = Build.FINGERPRINT;
            editor.putString("fingerprint", fingerprint);
        }

        String selinuxStatus = sharedPreferences.getString("selinux_status", null);
        if (selinuxStatus == null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                selinuxStatus = "强制模式";
                // selinuxStatus = android.os.SELinux.isSELinuxEnforced() ? "强制模式" : "宽容模式";
            } else {
                selinuxStatus = "未知";
            }
            editor.putString("selinux_status", selinuxStatus);
        }

        editor.apply();

        ((TextView) view.findViewById(R.id.textView_model_saved_path)).setText(modelPath);
        ((TextView) view.findViewById(R.id.textView_picture_saved_path)).setText(picturePath);
        ((TextView) view.findViewById(R.id.textView_device_name)).setText(deviceName);
        ((TextView) view.findViewById(R.id.textView_system_version)).setText(systemVersion);
        ((TextView) view.findViewById(R.id.textView_fingerprint)).setText(fingerprint);
        ((TextView) view.findViewById(R.id.textView_selinux)).setText(selinuxStatus);
    }
}