package com.realizer.schoolgeine.driver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.realizer.schoolgeine.driver.adapter.TrackTableListAdapter;
import com.realizer.schoolgeine.driver.backend.DatabaseQueries;
import com.realizer.schoolgeine.driver.model.TrackModel;

import java.util.ArrayList;

/**
 * Created by Bhagyashri on 9/29/2016.
 */
public class ShowListActivity extends AppCompatActivity {

    ListView shoList;
    DatabaseQueries qr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_list_layout);
        qr = new DatabaseQueries(ShowListActivity.this);
        shoList = (ListView)findViewById(R.id.dblist);

        ArrayList<TrackModel> tempList = qr.getAllLocData();
        if (tempList.size() > 0) {
            shoList.setVisibility(View.VISIBLE);
            shoList.setAdapter(new TrackTableListAdapter(ShowListActivity.this, tempList));
        }
    }
}
