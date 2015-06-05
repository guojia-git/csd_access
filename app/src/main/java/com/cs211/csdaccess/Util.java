package com.cs211.csdaccess;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.util.Log;

import android.net.Uri;

/**
 * Created by Administrator on 2015/5/31.
 * // Run external binary reference: http://stackoverflow.com/questions/5583487/hosting-an-executable-within-android-application
 * // The runcommand function and getassetsfile function: https://github.com/carmenloklok/ChangeMac
 */
public class Util {
    // path of busybox
    private static Uri busybox_uri;
    public static String BUSYBOX_PATH = "";

    public static void init(Context context) {
        //busybox_uri = Uri.parse("file:///android_asset/busybox");
        //BUSYBOX_PATH = busybox_uri.toString();
        BUSYBOX_PATH = "/data/data/com.cs211.csdaccess/busybox";
    }

    public static int getassetsfile(Context context, String fileName,File tagFile) {
        int retVal = 0;
        try {
            InputStream in = context.getAssets().open(fileName);
            if (in.available() == 0) {
                return retVal;
            }
            FileOutputStream out = new FileOutputStream(tagFile);
            int read;
            byte[] buffer = new byte[4096];
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
            out.close();
            in.close();
            retVal = 1;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return retVal;
    }

    public static String getMac(Context context) {
        //String[] results = runCommand("ls");
        String[] results = runCommand(BUSYBOX_PATH + " iplink show wlan0");
        // Parse iplink command
        String[] parsed = results[1].split(" ");
        String mac;
        for (int j = 0; j < parsed.length; ++j){
            if (parsed[j].equals("link/ether")) {
                mac = parsed[j+1];
                return mac;
            }
        }
        return "error";
    }

    public static void setMac(String mac) {
        runCommand(BUSYBOX_PATH + " ifconfig wlan0 hw ether " + mac);
    }

    public static String[] runCommand(String command) {
        Log.e("DEBUG", command);
        Process process = null;
        DataOutputStream os = null;
        String path = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            InputStream is = process.getInputStream();
            byte[] bs = new byte[4096];
            is.read(bs);
            path = new String(bs);
            process.waitFor();
        } catch (Exception e) {
            Log.e("DEBUG",
                    "Unexpected error - Here is what I know: " + e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                // nothing
            }
        }
        String[] paths = null;
        if (path != null && !path.trim().equals("")) {
            paths = path.substring(0, path.lastIndexOf("\n")).split("\n");
        }
        return paths;
    }
}
