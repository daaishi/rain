package com.arsias.rain;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ShoichiIshida on 2013/05/17.
 */
public class WeatherTaskLoader extends AsyncTaskLoader<byte[]>{
    HttpURLConnection connect = null;
    InputStream istream = null;
    ByteArrayOutputStream ostream = null;
    byte[] result = null;
    URL url;

    public WeatherTaskLoader(Context context, String url_str) throws MalformedURLException {
        super(context);
        url = new URL(url_str);
    }



    @Override
    public byte[] loadInBackground() {
        try {
            connect = (HttpURLConnection)url.openConnection();
            connect.setRequestMethod("GET");
            istream = connect.getInputStream();
            ostream = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            while (true) {
                int size = istream.read(buf);
                if (size < 0) break;
                ostream.write(buf, 0, size);
            }
            result = ostream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connect != null) connect.disconnect();
            if (istream != null) try {
                istream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (ostream != null) try {
                ostream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
