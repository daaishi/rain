package com.arsias.rain;

import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.location.*;
import android.support.v4.content.Loader;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Activity implements LocationListener, LocationSource, OnClickListener, OnMapLongClickListener {

    private static final int ADDRESS_LOADER_ID = 0;
    //private static final int WEATHER_LOADER_ID = 1;

    private GoogleMap mMap;
    private OnLocationChangedListener mListener;
    private LocationManager locationManager;
    private double mLat = 0;
    private double mLon = 0;
    public EditText edText1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null) {
            boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean netIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (gpsIsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 2.0f, this);
            }
            if (netIsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 2.0f, this);
            }

//        	if(gpsIsEnabled){
//        		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 2.0f, this);
//        	} else if(netIsEnabled) {
//        		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 2.0f, this);
//        	}
        } else {
            Toast.makeText(this, "locationManager is null", Toast.LENGTH_SHORT).show();
        }

        setUpMapIfNeeded();

        LoaderManager lm = getLoaderManager();
        lm.initLoader(ADDRESS_LOADER_ID, args, new AddressLoader());
        //lm.initLoader(WEATHER_LOADER_ID, args, new WeatherLoader());

        Button btn1 = (Button) findViewById(R.id.button1);
        btn1.setOnClickListener(this);
        edText1 = (EditText) findViewById(R.id.editText1);
    }

    public void addressUpdated() {
        if (result != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < result.getMaxAddressLineIndex() + 1; i++) {
                String item = result.getAddressLine(i);
                if (item == null) {
                    break;
                }
                sb.append(item);
            }
            edText1.setText(sb.toString());
        }

    }
//    public void weatherUpdated() {
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        setUpMapIfNeeded();

        if (locationManager != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        super.onStop();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map01)).getMap();
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.setOnMapLongClickListener(this);
            }
            mMap.setLocationSource(this);
        }
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        // TODO Auto-generated method stub
        mListener = listener;
    }

    @Override
    public void deactivate() {
        // TODO Auto-generated method stub
        mListener = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if (mListener != null) {
            mListener.onLocationChanged(location);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            mLat = location.getLatitude();
            mLon = location.getLongitude();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "provider disabled", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "provider enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

//    @Override
//    public Loader<Address> onCreateLoader(int id, Bundle args) {
//        // TODO Auto-generated method stub
//        double lat = args.getDouble("lat");
//        double lon = args.getDouble("lon");
//        return new AddressTaskLoader(this, lat, lon);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Address> loader, Address result) {
//        // TODO Auto-generated method stub
//        if (result != null) {
//            StringBuilder sb = new StringBuilder();
//            for (int i = 1; i < result.getMaxAddressLineIndex() + 1; i++) {
//                String item = result.getAddressLine(i);
//                if (item == null) {
//                    break;
//                }
//                sb.append(item);
//            }
//            edText1.setText(sb.toString());
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Address> loader) {
//        // TODO Auto-generated method stub
//
//    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.button1) {
            getAddressByLoader();
        }
    }
    private void getAddressByLoader() {
        if (mLat != 0) {
            Bundle args = new Bundle();
            args.putDouble("lat", mLat);
            args.putDouble("lon", mLon);

            getLoaderManager().restartLoader(ADDRESSLOADER_ID, args, this);
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        // TODO Auto-generated method stub
        //edText1.setText("point:" + point);
        //getWeatherXML(point);

    }
    private void getWeatherXML(LatLng point) {
        byte[] xml_byte = null;

            DecimalFormat df = new DecimalFormat();

            df.applyPattern("0");

            df.setMinimumFractionDigits(2);
            df.setMaximumFractionDigits(2);

            String latitude_str = df.format(point.latitude);
            String longitude_str = df.format(point.longitude);


            String weather_url = "http://api.worldweatheronline.com/free/v1/weather.ashx?q=" +
                    latitude_str + "," + longitude_str + "&format=xml&num_of_days=1&key=j73u9k9jq523cuawn66jaaut";

            //Bundle args = new Bundle();
            //args.putString("weather_url", weather_url);
            //getLoaderManager().restartLoader(2, args, this);

        if (xml_byte == null) return;
        parseXML(xml_byte);
    }

    private void parseXML(byte[] xml_byte) {

        XmlPullParser parser = Xml.newPullParser();

        String temp_c = null;

        String weather_icon_url = null;

        try {

            parser.setInput(new StringReader(new String(xml_byte, "UTF-8")));

            int eventType = parser.getEventType();

            boolean tag_current_condition = false;


            while (eventType != XmlPullParser.END_DOCUMENT && (temp_c == null || weather_icon_url == null)) {
                switch (eventType) {

                    case XmlPullParser.START_TAG:

                        if (parser.getName().equals("current_condition"))
                            tag_current_condition = true;

                        if (tag_current_condition) {

                            if (parser.getName().equals("temp_C")) {

                                eventType = parser.next();
                                if (eventType == XmlPullParser.TEXT) {

                                    temp_c = parser.getText();
                                }
                            } else if (parser.getName().equals("weatherIconUrl")) {

                                eventType = parser.next();
                                if (eventType == XmlPullParser.TEXT) {

                                    weather_icon_url = parser.getText();
                                }
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:

                        if (parser.getName().equals("current_condition"))
                            tag_current_condition = false;
                        break;
                }

                eventType = parser.next();
            }
        } catch (Exception e) {
        }
//        if (weather_icon_url != null) {
//
//            byte[] byteArray = getHttp(weather_icon_url);
//
//            Bitmap weather_icon_bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//
//            ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
//            imageView1.setImageBitmap(weather_icon_bitmap);
//
//            imageView1.invalidate();
//        }
    }
}

public class AddressLoader implements AsyncTaskLoader<Address> {
    private Geocoder mGeocoder = null;
    private double mLat;
    private double mLon;

    public AddressLoader(AddressLoaderCallbacks context, double lat, double lon) {
        super(context);
        mGeocoder = new Geocoder(context, Locale.getDefault());
        mLat = lat;
        mLon = lon;

    public Address loadInBackground() {
            Address result = null;
            try {
                List<Address> results = mGeocoder.getFromLocation(mLat, mLon, 1);
                if(result != null && !results.isEmpty()) {
                    result = results.get(0);
                }
            } catch (IOException e) {
                Log.e("AddressTaskLoader", e.getMessage());
            }
        return result;
    }
}
//public class WeatherLoader implements AsyncTaskLoader<byte[]> {
//    public byte[] loadInBackground() {
//
//    }
//}

public class AddressLoaderCallbacks implements LoaderCallbacks<Address> {
    public Loader<Address> onCreateLoader(int id, Bundle args){
        double lat = args.getDouble("lat");
        double lon = args.getDouble("lon");
        return new AddressLoader(this, lat, lon);
    }
    public void onLoadFinished(Loader<Address> loader, Address data){
        activity.wetherUpdated();
    }
}

//public class WeatherLoderCallbacks implements LoaderCallbacks<byte[]> {
//    public Loader<byte[]> onCreateLoader(int id, Bundle data) {
//        return new WeatherLoader();
//    }
//    public void onLoadFinished(Loader<byte[]> loader, byte[] data) {
//        activity.weatherUpdated();
//    }

}