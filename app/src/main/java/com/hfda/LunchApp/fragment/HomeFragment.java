package com.hfda.LunchApp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hfda.LunchApp.R;
import com.hfda.LunchApp.activity.MainActivity;
import com.hfda.LunchApp.app.AppConfig;
import com.hfda.LunchApp.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.hfda.LunchApp.R.id.imageView;
import static com.hfda.LunchApp.R.string.fri;
import static com.hfda.LunchApp.R.string.homeAction;
import static com.hfda.LunchApp.R.string.mon;
import static com.hfda.LunchApp.R.string.sat;
import static com.hfda.LunchApp.R.string.sun;
import static com.hfda.LunchApp.R.string.thu;
import static com.hfda.LunchApp.R.string.tue;
import static com.hfda.LunchApp.R.string.wed;

public class HomeFragment extends Fragment {

    Integer day;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(homeAction);

        return view;
    }


    public void onBackPressed() {
        Log.d("Laupet", "BACK");
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Laupet", "On View Created");
        getOpening();
    }

    //Get menu from mySQL
    private void getOpening() {
        // Tag used to cancel the request
        String tag_string_req = "req_opening";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_OPENING, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Laupet", "Opening Hours Response: " + response);
                String r = "";


                try {

                    TextView tvOpening = (TextView) getView().findViewById(R.id.tvDag);


                    JSONArray jObj = new JSONArray(response);

                    for (int i = 0; i < jObj.length(); i++) {
                        JSONObject row = jObj.getJSONObject(i);

                        //checks the day from the database and refers to a string to change the name of the day depending on language
                        if (row.getString("day").equals("monday")) {
                            day = mon;
                        } else if (row.getString("day").equals("tuesday")) {
                            day = tue;
                        } else if (row.getString("day").equals("wednesday")) {
                            day = wed;
                        } else if (row.getString("day").equals("thursday")) {
                            day = thu;
                        } else if (row.getString("day").equals("friday")) {
                            day = fri;
                        } else if (row.getString("day").equals("saturday")) {
                            day = sat;
                        } else if (row.getString("day").equals("sunday")) {
                            day = sun;
                        }

                        r += getString(day) + "\n" + row.getString("openingHours") + "\n\n";

                        Log.d("Laupet", r);
                        tvOpening.setText(r);
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.d("Laupet", "JSONEXception" + e.getMessage());
                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Laupet", "menu Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to request queue
        Log.d("Laupet", "getInstance:" + strReq + " - " + tag_string_req);
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
