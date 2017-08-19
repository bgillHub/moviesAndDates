package com.example.android.brianspopularmovies.utilities;

import com.example.android.brianspopularmovies.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gilli on 8/15/2017.
 */

public class OpenMovieDataUtils {
    public static JSONArray getMovieCards(MainActivity mainActivity, String jsonResponse) throws JSONException {
        System.out.print("JSON " + jsonResponse);
        JSONArray results = new JSONObject(jsonResponse).getJSONArray("results");
        return results;
    }
}
