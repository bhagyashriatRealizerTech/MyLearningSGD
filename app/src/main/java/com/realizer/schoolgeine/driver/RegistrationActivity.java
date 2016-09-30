package com.realizer.schoolgeine.driver;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.realizer.schoolgeine.driver.Commons.OnTaskCompleted;
import com.realizer.schoolgeine.driver.Commons.ProgressWheel;
import com.realizer.schoolgeine.driver.Commons.Utils;
import com.realizer.schoolgeine.driver.asynctask.DriverRegisterAsyncTaskPost;
import com.realizer.schoolgeine.driver.asynctask.SchoolCodeAsyncTaskGet;
import com.realizer.schoolgeine.driver.backend.DatabaseQueries;
import com.realizer.schoolgeine.driver.model.DriverInfo;
import com.realizer.schoolgeine.driver.model.TrackModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Bhagyashri on 9/28/2016.
 */
public class RegistrationActivity extends AppCompatActivity implements OnTaskCompleted {

    Button submit;
    TextView heading, startTime, endTime;
    EditText firstName,  address, mobileNo, vehicleNo,alterNteNo;
    DatabaseQueries qr;
    ProgressWheel loading;
    Spinner schoolSpinner;
    ArrayList<String> schoolname,schoolcode;
    String schoolCode;
    String username ,  driverUUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registarion_layout);
        initiateView();
        listeners();
        qr = new DatabaseQueries(RegistrationActivity.this);

        loading.setVisibility(View.VISIBLE);
        new SchoolCodeAsyncTaskGet(RegistrationActivity.this,RegistrationActivity.this).execute();
    }

    public void initiateView() {
        submit = (Button) findViewById(R.id.btnSubmit);
        heading = (TextView) findViewById(R.id.txtHeading);
        startTime = (TextView) findViewById(R.id.txtstartTime);
        endTime = (TextView) findViewById(R.id.txtendTime);
        firstName = (EditText) findViewById(R.id.edtFname);
        mobileNo = (EditText) findViewById(R.id.edtMobNo);
        address = (EditText) findViewById(R.id.edtAddress);
        vehicleNo = (EditText) findViewById(R.id.edtVehicleNo);
        alterNteNo = (EditText) findViewById(R.id.edtalternateMobNo);
        schoolSpinner = (Spinner) findViewById(R.id.spinnerSchool);

        schoolname = new ArrayList<>();
        schoolcode = new ArrayList<>();

        loading = (ProgressWheel)findViewById(R.id.loading);

        Typeface face = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");
        submit.setTypeface(face);
        heading.setTypeface(face);

    }

    public void listeners() {


        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new Utils().new TimePickerFragment(startTime);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new Utils().new TimePickerFragment(endTime);
                newFragment.show(getFragmentManager(), "timePicker");

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firstName.getText().toString().trim().length() == 0) {
                    Toast.makeText(RegistrationActivity.this, "Please Enter First Name", Toast.LENGTH_SHORT).show();
                } else if (mobileNo.getText().toString().trim().length() == 0) {
                    Toast.makeText(RegistrationActivity.this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                } else if (address.getText().toString().trim().length() == 0) {
                    Toast.makeText(RegistrationActivity.this, "Please Enter Address", Toast.LENGTH_SHORT).show();
                } else if (vehicleNo.getText().toString().trim().length() == 0) {
                    Toast.makeText(RegistrationActivity.this, "Please Enter Vehicle Number", Toast.LENGTH_SHORT).show();
                } else if (startTime.getText().toString().trim().length() == 0) {
                    Toast.makeText(RegistrationActivity.this, "Please Enter Start Time", Toast.LENGTH_SHORT).show();
                } else if (endTime.getText().toString().trim().length() == 0) {
                    Toast.makeText(RegistrationActivity.this, "Please Enter End Time", Toast.LENGTH_SHORT).show();
                } else {
                    submitData();
                }
            }
        });

        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                schoolCode = schoolcode.get(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void submitData() {

        loading.setVisibility(View.VISIBLE);

        DriverInfo obj = new DriverInfo();
        String user[] = firstName.getText().toString().trim().split(" ");
        username = firstName.getText().toString().trim();

        for(int i=0;i<user.length;i++)
        {
            if(i==(user.length-1))
            username = user[i];
        }

        driverUUID = user[0]+new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());

        obj.setDriverUUID(driverUUID);
        obj.setFName(firstName.getText().toString().trim());
        obj.setMobileNo(mobileNo.getText().toString().trim());
        obj.setAddress(address.getText().toString().trim());
        obj.setVehicleNo(vehicleNo.getText().toString().trim());
        obj.setStartTime(startTime.getText().toString().trim());
        obj.setEndTime(endTime.getText().toString().trim());
        obj.setAlternateNo(alterNteNo.getText().toString().trim());
        obj.setSchoolode(schoolCode);
        obj.setUserID(username);

        long n = qr.insertDriverInfo(obj);

        if(n>0)
        {
            new DriverRegisterAsyncTaskPost(RegistrationActivity.this,obj).execute();
        }


    }

    @Override
    public void onTaskCompleted(String s, ArrayList<TrackModel> trackobj) {

        loading.setVisibility(View.GONE);

        String success[] = s.split("@@@");


            if(success[1].equalsIgnoreCase("SchoolCode")) {

                String json = success[0];
                try {
                    JSONArray jsonArr = new JSONArray(json);
                    if(jsonArr.length()>0)
                    {
                        schoolname.add(0,"Select School");
                        schoolcode.add(0,"Select School");
                        for (int i=0;i<jsonArr.length();i++)
                        {
                            JSONObject jobj = jsonArr.getJSONObject(i).getJSONObject("school");
                            schoolname.add(i+1,jobj.getString("name"));
                            schoolcode.add(i+1,jobj.getString("Code"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegistrationActivity.this,
                                R.layout.spiner_select, schoolname);
                        adapter.setDropDownViewResource(R.layout.spiner_dropdown);
                        for (int i = 0; i < adapter.toString().length(); i++) {
                            schoolSpinner.setAdapter(adapter);
                            break;
                        }

                        schoolSpinner.setSelection(0);
                    }
                    else {
                        if (success[1].equalsIgnoreCase("SchoolCode")) {

                            Toast.makeText(RegistrationActivity.this, "School Code Not Loading", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        else
            {
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor edit = sharedpreferences.edit();
                edit.putBoolean("Login", true);
                edit.putString("UserID", username);
                edit.putString("DriverUUID",driverUUID);
                edit.putBoolean("IsStart", false);
                edit.putString("UserName",username);
                edit.commit();

                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
            }
       /* if (success[0].equalsIgnoreCase("true")) {
            if(success[1].equalsIgnoreCase("Register")) {

                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor edit = sharedpreferences.edit();
                edit.putBoolean("Login", true);
                edit.commit();

                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
        else
        {
             if(success[1].equalsIgnoreCase("Register"))
             {
                Toast.makeText(RegistrationActivity.this,"Registarion Fail",Toast.LENGTH_SHORT).show();
             }
        }*/
    }


}
