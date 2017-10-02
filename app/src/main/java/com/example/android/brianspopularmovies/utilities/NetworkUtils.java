package com.example.android.brianspopularmovies.utilities;

/*
  Created by gilli on 8/10/2017.
 */

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.brianspopularmovies.data.AppPreferences;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();


    //ToDo: Swap in your own API key!
    private static final String POP_MOVIE_URL =

    private static  final String TOP_MOVIE_URL =

    //private final static String SORT_PARAM = "sort_by";
    private final static  String VOTE_MINIMUM = "vote_count.gte";
    private final static  String LANG = "language";
    private final static String ORIG_LAN = "with_original_language";
    final static String COUNTRY_PARAM = "certification_country";
    final static  String RELEASE_PARAM = "primary_release_year";
    final static String GENRE_PARAM = "with_genres";

    public static URL getUrl(Context context) {
        return buildUrl(0);
    }

    public static URL buildUrl(int movieQuery) {
        String passURL = POP_MOVIE_URL;
        if( movieQuery == 0) {
            passURL = POP_MOVIE_URL;
        }
        if (movieQuery == 1) {
            passURL = TOP_MOVIE_URL;
        }
        Uri builtUri = Uri.parse(passURL).buildUpon()
                .appendQueryParameter(VOTE_MINIMUM, "500")
                .appendQueryParameter(LANG, "en-us")
                .appendQueryParameter(ORIG_LAN, "en")
              //  .appendQueryParameter(SORT_PARAM, movieQuery)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}