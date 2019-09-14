package com.seventv.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import com.seventv.R;
import com.seventv.SevenTVApplication;
import com.seventv.file.FileBasic;
import com.seventv.network.NetworkBasic;

import java.util.Locale;

public class SettingFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting, rootKey);

        Preference githubPreference = findPreference("github");
        githubPreference.setIntent(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://github.com/over-driver/SevenTV")));

        Preference cleanFavorite = findPreference("clean_favorite");
        cleanFavorite.setOnPreferenceClickListener(preference -> {
                (new AlertDialog.Builder(getActivity())).setMessage(getResources().getString(R.string.ask_clean_favorite))
                        .setCancelable(true)
                        .setPositiveButton(getResources().getString(R.string.delete), (dialog, which) -> {
                            SevenTVApplication.DB_HELPER.cleanDb();
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {
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
        long cacheSize = (FileBasic.dirSize(getActivity().getCacheDir()) + FileBasic.dirSize(getActivity().getExternalCacheDir()))  / 1024 / 1024;
        cleanCache.setSummary(cacheSize + " MB");
        cleanCache.setOnPreferenceClickListener(preference -> {
            FileBasic.deleteDir(getActivity().getCacheDir());
            FileBasic.deleteDir(getActivity().getExternalCacheDir());
            preference.setSummary(((FileBasic.dirSize(getActivity().getCacheDir()) + FileBasic.dirSize(getActivity().getExternalCacheDir())) / 1024 / 1024) + " MB");
            return true;
        });

        Preference cleanAPK = findPreference("clean_apk");
        long apkSize = FileBasic.dirSize(FileBasic.getDownloadDir(getActivity())) / 1024 / 1024;
        cleanAPK.setSummary(apkSize + " MB");
        cleanAPK.setOnPreferenceClickListener(preference -> {
            FileBasic.deleteDir(FileBasic.getDownloadDir(getActivity()));
            preference.setSummary((FileBasic.dirSize(FileBasic.getDownloadDir(getActivity())) / 1024 / 1024) + " MB");
            return true;
        });
    }


}
