package com.example.android.brianspopularmovies.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gilli on 8/15/2017.
 */

public class OpenMovieDataUtils {
    public static JSONArray getMoviePages(String jsonResponse) throws JSONException {
        System.out.print("JSON " + jsonResponse);
        return new JSONObject(jsonResponse).getJSONArray("results");
    }
}
