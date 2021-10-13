package com.example.api_implement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    List<data_model> data_models = new ArrayList<>();
    RecyclerView recyclerView;
    ProgressDialog dialog;
    adapter_rectcler adapter_rectcler;
    EditText search;
    CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        textView = findViewById(R.id.text);
        recyclerView = findViewById(R.id.recyvler);
        search = findViewById(R.id.search_text);
        coordinatorLayout=findViewById(R.id.coordinate_la);
        enableSwipeToDeleteAndUndo();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
          filterlist(editable.toString());
            }
        });
        dialog = new ProgressDialog(getApplicationContext());
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                RearrangeItems();
            }
        });
//dialog.show();
//dialog.setCancelable(false);
        getApi();
    }

    private void filterlist(String toString) {
        ArrayList<data_model> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (data_model item : data_models) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getTitle().toLowerCase().contains(toString.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter_rectcler.filterList(filteredlist);
        }
    }


    private void getApi() {
        StringRequest request = new StringRequest(Request.Method.GET, "https://jsonplaceholder.typicode.com/photos", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                textView.append(response);

                try {
//                    dialog.dismiss();
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String image = object.getString("url");
                        String thmb = object.getString("thumbnailUrl");
                        String tit = object.getString("title");
                        data_models.add(new data_model(tit, image, thmb));
                        recyclerView.setHasFixedSize(true);
//                          recyclerView.setAdapter(adapter);
                        adapter_rectcler = new adapter_rectcler(data_models, getApplicationContext());
                        recyclerView.setAdapter(adapter_rectcler);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//
                    }
//                   JSONObject object = array.getJSONObject(1);
//                   JSONObject object1 = object.getJSONObject("address");
//                   String name =object1.getString("street");
//                   textView.setText(array.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                dialog.dismiss();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void RearrangeItems() {
//        Collections.shuffle(images, new Random(System.currentTimeMillis()));
        Collections.sort(data_models, new Comparator<data_model>() {
            @Override
            public int compare(data_model data_model, data_model t1) {
                return data_model.getTitle().compareToIgnoreCase(t1.getTitle());
            }
        });
        adapter_rectcler = new adapter_rectcler(data_models, MainActivity.this);
        recyclerView.setAdapter(adapter_rectcler);
    }
    private void enableSwipeToDeleteAndUndo() {
        swipe_delete swipeToDeleteCallback = new swipe_delete(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final data_model item = adapter_rectcler.getData().get(position);

                adapter_rectcler.removeItem(position);


                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        adapter_rectcler.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }
}