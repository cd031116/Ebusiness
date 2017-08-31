/**
 * 
 */
package com.eb.sc.utils;

import android.os.Environment;

import java.io.File;

public class ClientGlobal {
    
    public static class Path {
        public static final String SDCardDir = Environment.getExternalStorageDirectory().getAbsolutePath();

        public static final String ClientDir = SDCardDir + File.separator + "ZkcService";
    }

}
