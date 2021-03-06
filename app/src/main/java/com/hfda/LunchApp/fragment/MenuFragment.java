package com.hfda.LunchApp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hfda.LunchApp.R;
import com.hfda.LunchApp.activity.MainActivity;
import com.hfda.LunchApp.app.AppConfig;
import com.hfda.LunchApp.app.AppController;
import com.hfda.LunchApp.helper.DividerItemDecoration;
import com.hfda.LunchApp.helper.LunchDBhelper;
import com.hfda.LunchApp.helper.MenuAdapter;
import com.hfda.LunchApp.objectClass.Menu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.hfda.LunchApp.R.string.menuAction;

public class MenuFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipeLayout;

    private List<Menu> menuList = new ArrayList<>();
    private MenuAdapter mAdapter;
    private LunchDBhelper db;
    private String price;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(menuAction);

        db = new LunchDBhelper(getActivity().getApplicationContext());

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        //Getting menu items from mySQL and putting them in arraylist
        getMenu();

        //setting up the recyclerView
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mAdapter = new MenuAdapter(menuList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onRefresh() {
        //new myTask().execute();
        menuList.clear();
        mAdapter.notifyDataSetChanged();

        getMenu();
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //Get menu from mySQL
    private void getMenu() {
        // Tag used to cancel the request
        String tag_string_req = "req_menu";


        String apiURL = AppConfig.URL_MENU;
        price = "studentPris";

        if (!db.getStudent()) {
            apiURL = AppConfig.URL_MENU_EMPLOYEE;
            price = "ansattPris";
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Laupet", "Menu Response: " + response);

                try {
                    JSONArray jObj = new JSONArray(response);

                    for (int i = 0; i < jObj.length(); i++) {
                        JSONObject row = jObj.getJSONObject(i);

                        //Getting info from mySQL
                        String merke = row.getString("merke");
                        String type = row.getString("type");
                        String pris = row.getString(price);
                        String kategori = row.getString("kategori");

                        //creating objects of data from mySQL
                        Menu menuItem = new Menu(merke, type, pris, kategori);
                        menuList.add(menuItem);
                    }

                    //Notifies the adapter to update the list
                    mAdapter.notifyDataSetChanged();

                    //Tells the swipelayout that the refresh is done
                    swipeLayout.setRefreshing(false);

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

