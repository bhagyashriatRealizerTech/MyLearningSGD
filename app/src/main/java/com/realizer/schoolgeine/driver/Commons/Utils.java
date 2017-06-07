package com.realizer.schoolgeine.driver.Commons;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Bhagyashri on 9/28/2016.
 */
public class Utils {


   // public static String url = "http://104.217.254.180/SJRestWCF/svcEmp.svc/";
   public static final String url ="http://45.35.4.250/SJRestWCF/svcEmp.svc/";
    /**
     * @param title to set
     * @return title SpannableString
     */
    public static SpannableString actionBarTitle(String title,Context context) {
        SpannableString s = new SpannableString(title);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/font.ttf");
        s.setSpan(new CustomTypefaceSpan("", face), 0, s.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return s;
    }




    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public static Uri getOutputMediaFileUri(int type) {

        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "DriverApp");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Camera", "Oops! Failed create "
                        + ".DriverApp" + " directory");
                return null;
            }
        }

        // Create search_layout media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");


        return mediaFile;
    }


    //Encode image to Base64 to send to server
    public static String setPhoto(Bitmap bitmapm,Context context) {
        // External sdcard location
        String filPath = null;
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "DriverApp");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Cameara", "Oops! Failed create "
                        + ".DriverApp" + " directory");

            }
        }
        else {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //4
            File file = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpeg");

            try {
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                //5
                fo.write(bytes.toByteArray());
                fo.close();
                filPath = file.getAbsolutePath();
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }

        return filPath;
    }

    public static void hideSoftKeyboardWithoutReq(Context context, View view) {
        try {
            if (view != null) {
                final InputMethodManager inputMethodManager =
                        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {

        }
    }

    public static String getMediumDate(String date) {
        String datetimevalue = null;
        try {
            SimpleDateFormat dfinput = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat dfoutput = new SimpleDateFormat("dd MMM yyyy");

            Date inDate = dfinput.parse(date);
            datetimevalue = dfoutput.format(inDate);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return datetimevalue;
    }

    public static String getDateForPost(String date) {
        String datetimevalue = null;
        try {
            SimpleDateFormat  dfoutput = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat dfinput = new SimpleDateFormat("dd MMM yyyy");

            Date inDate = dfinput.parse(date);
            datetimevalue = dfoutput.format(inDate);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return datetimevalue;
    }


    public  class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        TextView txt_view;
        public DatePickerFragment(TextView v)
        {
            txt_view = v;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day)
        {
            // Do something with the date chosen by the user
            txt_view.setText(getMediumDate( day+ "/" + (month + 1) + "/" + year));
        }
    }

    public class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        TextView time_view;
        public TimePickerFragment(TextView v)
        {
            time_view = v;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of DatePickerDialog and return it
           return new TimePickerDialog(getActivity(),this,hourOfDay,minute,true);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            time_view.setText(hourOfDay+":"+minute);
        }
    }
}
