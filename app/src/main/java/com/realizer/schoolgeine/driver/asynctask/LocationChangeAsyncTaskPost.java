package com.realizer.schoolgeine.driver.asynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.realizer.schoolgeine.driver.Commons.OnTaskCompleted;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LocationChangeAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder> {
    ProgressDialog diaglog;
    StringBuilder builder;
    String empId;
    String latitude, longitude;
    private OnTaskCompleted listener;
    String locDate;
    ArrayList<TrackModel> obj;
    String cellNo;

    public LocationChangeAsyncTaskPost(OnTaskCompleted listener, String _empId, ArrayList<TrackModel> obj, String cellNo)
    {
        this.listener = listener;
        this.empId = _empId;
        this.obj = obj;
        this.cellNo = cellNo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Log.d("Test", "onPreExecute");
    }

    @Override
    protected StringBuilder doInBackground(Void... params1) {
        builder = new StringBuilder();
        String url="http://104.217.254.180/SJRestWCF/svcEmp.svc/SetPLocation";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        System.out.println(url);

        String json = "";
        StringEntity se = null;

        JSONObject jobj = new JSONObject();
        try {
            JSONArray locCollection = new JSONArray();
            for(int i=0;i<obj.size();i++)
            {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("latitude",obj.get(i).getLati());
                jsonObj.put("longitude",obj.get(i).getLangi());
                jsonObj.put("locDate",obj.get(i).getLocationTime());
                locCollection.put(i,jsonObj);
            }

            jobj.put("UserName",empId);
            jobj.put("CellNumber",cellNo);
            jobj.put("LocLst",locCollection);

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

        listener.onTaskCompleted(stringBuilder.toString(),obj);
    }
}
