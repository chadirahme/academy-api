package com.academy.data.config;

import java.io.File;

public abstract class GenerateFileName {

    static String newFileName;
    public abstract void sound();

    public static String getFileExtension(final String path) {
        if (path != null && path.lastIndexOf('.') != -1) {
            return path.substring(path.lastIndexOf('.'));
        }
        return null;
    }

    public static String getFileName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public static String getUniqueFileName(String parent, String child, String fileName) {
        final File dir = new File(parent, child);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        int num = 0;
        final String ext = getFileExtension(fileName);
        final String name = getFileName(fileName);
        File file = new File(dir, fileName);
        while (file.exists()) {
            num++;
            file = new File(dir, name + "-" + num + ext);
        }
        newFileName=file.getName();
        return file.getName();
    }

    public static String getNewFileName(){
        return  newFileName;
    }
}
