package com.realizer.schoolgeine.driver;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Bhagyashri on 9/28/2016.
 */
public class ProfileActivity extends AppCompatActivity implements OnTaskCompleted{

    Button submit;
    TextView startTime, endTime,userId,driverUUID,schoolcode;
    EditText firstName,  address, mobileNo, vehicleNo,alterNteNo;
    ProgressWheel loading;
    DatabaseQueries qr;
    DriverInfo driverInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        initiateView();
        listeners();
        chaneView(false);
        hideSoftKeyboard();

        new getDriverInfoAsyncTask().execute();

    }
    public void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }



    public void chaneView(boolean flag)
    {
        firstName.setEnabled(flag);
        mobileNo.setEnabled(flag);
        alterNteNo.setEnabled(flag);
        address.setEnabled(flag);
        vehicleNo.setEnabled(flag);
        startTime.setEnabled(flag);
        endTime.setEnabled(flag);

        if(flag)
        submit.setVisibility(View.VISIBLE);
        else
            submit.setVisibility(View.GONE);

    }

    public void setData(DriverInfo obj)
    {
        if(obj != null)
        {
            firstName.setText(obj.getFName());
            mobileNo.setText(obj.getMobileNo());
            alterNteNo.setText(obj.getAlternateNo());
            address.setText(obj.getAddress());
            vehicleNo.setText(obj.getVehicleNo());
            userId.setText(obj.getUserID());
            driverUUID.setText(obj.getDriverUUID());
            schoolcode.setText(obj.getSchoolode());
            startTime.setText(obj.getStartTime());
            endTime.setText(obj.getEndTime());
        }

        loading.setVisibility(View.GONE);
    }

    public void initiateView() {
        submit = (Button) findViewById(R.id.btnSubmit);
        startTime = (TextView) findViewById(R.id.txtstartTime);
        endTime = (TextView) findViewById(R.id.txtendTime);
        firstName = (EditText) findViewById(R.id.edtFname);
        mobileNo = (EditText) findViewById(R.id.edtMobNo);
        address = (EditText) findViewById(R.id.edtAddress);
        vehicleNo = (EditText) findViewById(R.id.edtVehicleNo);
        alterNteNo = (EditText) findViewById(R.id.edtalternateMobNo);
        userId = (TextView) findViewById(R.id.txtUserID);
        driverUUID = (TextView) findViewById(R.id.txtUUID);
        schoolcode = (TextView) findViewById(R.id.txtSchoolcode);


        loading = (ProgressWheel)findViewById(R.id.loading);

        Typeface face = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");
        submit.setTypeface(face);

        getSupportActionBar().setTitle(Utils.actionBarTitle("Profile", ProfileActivity.this));

        qr =  new DatabaseQueries(ProfileActivity.this);

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
                    Toast.makeText(ProfileActivity.this, "Please Enter First Name", Toast.LENGTH_SHORT).show();
                } else if (mobileNo.getText().toString().trim().length() == 0) {
                    Toast.makeText(ProfileActivity.this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                } else if (address.getText().toString().trim().length() == 0) {
                    Toast.makeText(ProfileActivity.this, "Please Enter Address", Toast.LENGTH_SHORT).show();
                } else if (vehicleNo.getText().toString().trim().length() == 0) {
                    Toast.makeText(ProfileActivity.this, "Please Enter Vehicle Number", Toast.LENGTH_SHORT).show();
                } else if (startTime.getText().toString().trim().length() == 0) {
                    Toast.makeText(ProfileActivity.this, "Please Enter Start Time", Toast.LENGTH_SHORT).show();
                } else if (endTime.getText().toString().trim().length() == 0) {
                    Toast.makeText(ProfileActivity.this, "Please Enter End Time", Toast.LENGTH_SHORT).show();
                } else {
                    submitData();
                }
            }
        });


    }

    public void submitData() {

        loading.setVisibility(View.VISIBLE);

        DriverInfo obj = new DriverInfo();
        String user[] = firstName.getText().toString().trim().split(" ");
        String username = firstName.getText().toString().trim();
        for(int i=0;i<user.length;i++)
        {
            if(i==(user.length-1))
                username = user[i];
        }

        obj.setDriverUUID(driverUUID.getText().toString());
        obj.setFName(firstName.getText().toString().trim());
        obj.setMobileNo(mobileNo.getText().toString().trim());
        obj.setAddress(address.getText().toString().trim());
        obj.setVehicleNo(vehicleNo.getText().toString().trim());
        obj.setStartTime(startTime.getText().toString().trim());
        obj.setEndTime(endTime.getText().toString().trim());
        obj.setAlternateNo(alterNteNo.getText().toString().trim());
        obj.setSchoolode(schoolcode.getText().toString());
        obj.setUserID(username);

        long n = qr.updateDriverInfo(obj);
        Toast.makeText(ProfileActivity.this,"Profile Updated Successflly",Toast.LENGTH_SHORT).show();
        chaneView(false);
     loading.setVisibility(View.GONE);
        if(n>0)
        {
            //new DriverRegisterAsyncTaskPost(ProfileActivity.this,obj).execute();
        }


    }

    public class getDriverInfoAsyncTask extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

           ArrayList<DriverInfo>  temp = qr.getDriverInfo();
            driverInfo = temp.get(temp.size()-1);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setData(driverInfo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat_action_edit:
                chaneView(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTaskCompleted(String s, ArrayList<TrackModel> obj) {


    }
}
