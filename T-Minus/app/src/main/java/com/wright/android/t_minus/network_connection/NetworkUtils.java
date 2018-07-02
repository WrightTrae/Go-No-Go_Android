// Trae Wright
// JAV2 - C201803
// NetworkUtils.java
package com.wright.android.t_minus.network_connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    public static boolean isConnected(Context _context) {

        ConnectivityManager mgr = (ConnectivityManager)_context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(mgr != null) {
            NetworkInfo info = mgr.getActiveNetworkInfo();

            if(info != null) {
                return info.isConnected();
            }
        }

        return false;
    }

    public static String getNetworkData(String _url) {
        try {
            URL url = new URL(_url);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.connect();

            InputStream is = connection.getInputStream();

            String data = IOUtils.toString(is, "UTF-8");

            is.close();

            connection.disconnect();

            return data;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "Error Retrieving data";
    }
}
