package com.mapbox.mapboxsdk.android.testapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.mapbox.mapboxsdk.android.testapp.DB.PlaceDB;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by hainguyen on 18/04/2014.
 */
class WebserviceActivity extends AsyncTask<String, Void,String> {

    private Exception exception;

    private Fragment fragment;
    private static boolean firstTimePlaceLoading=true;
    public WebserviceActivity(Fragment mainActivity)
    {
        this.fragment = mainActivity;

    }

    protected String  doInBackground(String... url) {
        if (!isNetworkConnected(this.fragment))
        {
            Log.d("Curios","No internet - SHOW DEBUG");
            getNoInternetDialog(fragment.getActivity()).show();
            return null;
        }

        DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
        HttpGet httppost = new HttpGet(url[0]);
// Depends on your web service
//        httppost.setHeader("Content-type", "application/json");

        InputStream inputStream = null;
        String result = null;
        try {
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            System.out.println(entity);
            inputStream = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            result = sb.toString();

        } catch (Exception e) {
            // Oops
            System.out.println(e);
        }
        finally {
            try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
        }
        return result;
    }

    protected void onPostExecute(String result) {
        if (result == null) return;
        if (fragment instanceof MapFragment) {
            JSONArray jArray = null;
            try {
                jArray = new JSONArray(result);

                Place[] places = new Place[jArray.length()];
                MapView mv =  ((MapFragment) fragment).getMapView();
                //if (!checkDatabaseExisted(fragment.getActivity(),PlaceDB.DATABASE_NAME))
                    PlaceDB db = new PlaceDB(fragment.getActivity());

                List<Place> placeList =  db.getAllPlaces();

                if (placeList.isEmpty()) {
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject place = jArray.getJSONObject(i);
                        String type = place.getString("type");
                        if (type == null)
                            type = "Unknown type";
                        Place p = new Place(place.getString("id"), place.getString("title"), place.getString("desc"), type ,place.getDouble("x"), place.getDouble("y"), place.getInt("suggested"));
                        db.addPlace(p);
                    }
                    placeList =  db.getAllPlaces();
                }
                Log.e("CURIOS",placeList.toString());
                for (Place p : placeList) {
                    //if (p.isSuggested()>0)
                    {
                        Marker m = new Marker(mv, p.getTitle(), p.getID(), new LatLng(p.getLat(), p.getLng()));
                        m.setIcon(new Icon(fragment.getActivity(), Icon.Size.MEDIUM, "marker-stroked", "FFFF00"));
                        mv.addMarker(m);
                    }
                }
                final String title = places.length == 1 ? places[0].getTitle() : "Outer Hebrides Map ";
           //    MapsWithMeApi.showPointsOnMap(this.fragment, title, RecordDetailsActivity.getPendingIntent(this.fragment), points);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else {
            try {
                JSONObject jobject = new JSONObject(result);
              //  ((RecordDetailsActivity) fragment).showASinglePlace(jobject);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public  boolean isNetworkConnected(Fragment frag) {
        ConnectivityManager cm = (ConnectivityManager) frag.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo()!=null);
    }
    public AlertDialog.Builder getNoInternetDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet connection.");
        builder.setMessage("You have no internet connection, please check your settings or try again.");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        return builder;
    }

    private static boolean checkDatabaseExisted(ContextWrapper context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }
}