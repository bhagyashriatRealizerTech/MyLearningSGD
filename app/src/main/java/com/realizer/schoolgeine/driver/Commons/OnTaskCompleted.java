package com.realizer.schoolgeine.driver.Commons;

import com.realizer.schoolgeine.driver.model.TrackModel;

import java.util.ArrayList;

/**
 * Created by shree on 11/21/2015.
 */
public interface OnTaskCompleted {

    void onTaskCompleted(String s, ArrayList<TrackModel> obj);
}
