package com.seventv.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.seventv.BuildConfig;
import com.seventv.network.parser.item.VersionInfo;

import java.io.File;

public class FileBasic {

    public static long dirSize(File dir){
        long result = 0;
        if(dir != null && dir.exists()){
            File[] fileList = dir.listFiles();
            if(fileList == null){
                return result;
            }
            for(File f : fileList){
                if(f.isDirectory()){
                    result += dirSize(f);
                } else {
                    result += f.length();
                }
            }
        }
        return result;
    }

    public static boolean deleteDir(File dir){
        if(dir != null && dir.exists()){
            File[] fileList = dir.listFiles();
            if(fileList != null){
                for(File f : fileList){
                    if(f.isDirectory()){
                        if(!deleteDir(f)){
                            return false;
                        }
                    } else {
                        if(!f.delete()){
                            return false;
                        }
                    }
                }
            }
            return dir.delete();
        }else{
            return false;
        }
    }

    public static Intent getInstallIntent(Context context, String apkName){
        Intent intent;
        File apkFile = getApkFile(context, apkName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            Uri apkUri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".fileprovider", apkFile);
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            Uri apkUri = Uri.fromFile(apkFile);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    public static String getDownloadDirType(){
        return "/download/";
    }

    public static File getDownloadDir(Context context){
        return context.getExternalFilesDir(getDownloadDirType());
    }

    public static File getApkFile(Context context, String fileName){
        return new File(getDownloadDir(context), fileName);
    }

    public static String getApkName(String versionName){
        return "SevenTV-release-" + versionName + ".apk";
    }

    public static String getApkUri(String versionName){
        return "https://github.com/over-driver/SevenTV/releases/download/" + versionName + "/" + getApkName(versionName);
    }

    public static void downloadInstallApk(Context context, VersionInfo versionInfo){

        String apkName = getApkName(versionInfo.versionName);
        File apk = getApkFile(context, apkName);

        if(apk.exists()){
            context.startActivity(getInstallIntent(context, apkName));
        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(FileBasic.getApkUri(versionInfo.versionName)));
            request.setDestinationInExternalFilesDir(context.getApplicationContext(), getDownloadDirType(), apkName);
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = downloadManager.enqueue(request);
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == downloadId){
                        context.startActivity(getInstallIntent(context, apkName));
                    }
                }
            }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

}
