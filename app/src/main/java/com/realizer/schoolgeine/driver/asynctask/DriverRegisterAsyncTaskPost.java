package com.realizer.schoolgeine.driver.asynctask;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.realizer.schoolgeine.driver.Commons.OnTaskCompleted;
import com.realizer.schoolgeine.driver.Commons.Utils;
import com.realizer.schoolgeine.driver.model.DriverInfo;
import com.realizer.schoolgeine.driver.model.TrackModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DriverRegisterAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder> {
    StringBuilder builder;
    private OnTaskCompleted listener;
    DriverInfo obj;
    public DriverRegisterAsyncTaskPost(OnTaskCompleted listener, DriverInfo obj)
    {
        this.listener = listener;
        this.obj = obj;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Log.d("Test", "onPreExecute");
    }

    @Override
    protected StringBuilder doInBackground(Void... params1) {
        builder = new StringBuilder();
        String url= Utils.url+"RegisterDriver";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);



        System.out.println(url);

        String json = "";
        StringEntity se = null;

        JSONObject jobj = new JSONObject();
        try {

            jobj.put("schoolCode",obj.getSchoolode());
            jobj.put("name",obj.getFName());
            jobj.put("address",obj.getAddress());
            jobj.put("contactNo",obj.getMobileNo());
            jobj.put("alternateContactNo",obj.getAlternateNo());
            jobj.put("VehicleNo",obj.getVehicleNo());
            jobj.put("UserId",obj.getUserID());
            jobj.put("DriverUniqueNo",obj.getDriverUUID());

            json = jobj.toString();

            Log.d("STRINGOP", json);
            se = new StringEntity(json);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            httpPost.setEntity(se);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            Log.d("StatusCode", "" + statusCode);
            if (statusCode == 200) {
                HttpEntity entity = httpResponse.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

            } else {
                // Log.e("Error", "Failed to Login");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder;
    }

    @Override
    protected void onPostExecute(StringBuilder stringBuilder) {
        super.onPostExecute(stringBuilder);
        listener.onTaskCompleted(stringBuilder.toString()+"@@@Register",null);
    }
}
