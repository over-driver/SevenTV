package com.seventv.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import com.seventv.R;
import com.seventv.SevenTVApplication;
import com.seventv.file.FileBasic;
import com.seventv.network.NetworkBasic;

public class SettingFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting, rootKey);

        Preference githubPreference = findPreference("github");
        githubPreference.setIntent(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://github.com/over-driver/SevenTV")));

        Preference cleanFavorite = findPreference("clean_favorite");
        cleanFavorite.setOnPreferenceClickListener(preference -> {
                (new AlertDialog.Builder(getActivity())).setMessage("是否清空收藏夹?")
                        .setCancelable(true)
                        .setPositiveButton("删除", (dialog, which) -> {
                            SevenTVApplication.DB_HELPER.cleanDb();
                        })
                        .setNegativeButton("取消", (dialog, which) -> {
                        }).create().show();
                return true;
            });

        Preference versionPreference = findPreference("version");
        try {
            PackageInfo pinfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionPreference.setSummary(pinfo.versionName);
        } catch (PackageManager.NameNotFoundException e){
            Log.e("Setting", e.toString());
        }
        versionPreference.setOnPreferenceClickListener(preference -> {
            NetworkBasic.checkUpdate(getActivity(), true);
            return true;
        });

        Preference cleanCache = findPreference("clean_cache");
        long cacheSize = FileBasic.dirSize(getActivity().getCacheDir()) / 1024 / 1024;
        cleanCache.setSummary(cacheSize + " MB");
        cleanCache.setOnPreferenceClickListener(preference -> {
            FileBasic.deleteDir(getActivity().getCacheDir());
            preference.setSummary((FileBasic.dirSize(getActivity().getCacheDir()) / 1024 / 1024) + " MB");
            return true;
        });
    }


}
