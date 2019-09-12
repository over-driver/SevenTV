package com.seventv.file;

import java.io.File;

public class FileBasic {

    public static long dirSize(File dir){
        long result = 0;
        if(dir.exists()){
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
        if(dir.exists()){
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



}
