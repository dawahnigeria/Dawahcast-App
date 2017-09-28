package com.apps.dawahcast;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.apps.adapter.AdapterVideoCat;
import com.apps.item.ItemCat;
import com.apps.utils.Constant;
import com.apps.utils.JsonUtils;
import com.apps.utils.ZProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityVideoCat extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ItemCat> arrayList;
    AdapterVideoCat adapterCat;
    ZProgressHUD progressHUD;
    GridLayoutManager gridLayoutManager;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videocat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.menu_video));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusBar));
        }
        progressHUD = ZProgressHUD.getInstance(ActivityVideoCat.this);
        progressHUD.setMessage(ActivityVideoCat.this.getResources().getString(R.string.loading));
        progressHUD.setSpinnerType(ZProgressHUD.FADED_ROUND_SPINNER);

        arrayList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_cat);
        gridLayoutManager = new GridLayoutManager(ActivityVideoCat.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        if (JsonUtils.isNetworkAvailable(ActivityVideoCat.this)) {
            new LoadCat().execute(Constant.URL_VIDEO_CAT);
        } else {

        }

     }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
                .getActionView();

        final MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchView.isIconified()) {
//                listView.clearTextFilter();
                    recyclerView.setAdapter(adapterCat);
                    adapterCat.notifyDataSetChanged();
                } else {
                    adapterCat.getFilter().filter(newText);
                    adapterCat.notifyDataSetChanged();
                }

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do something
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    private class LoadCat extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressHUD.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String json = JsonUtils.getJSONString(strings[0]);

                JSONObject mainJson = new JSONObject(json);
                JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
                JSONObject objJson = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    objJson = jsonArray.getJSONObject(i);

                    String id = objJson.getString(Constant.TAG_VIDEO_CAT_ID);
                    String name = objJson.getString(Constant.TAG_VIDEO_CAT_NAME);

                    ItemCat objItem = new ItemCat(id,name);
                    arrayList.add(objItem);
                }

                return "1";
            } catch (JSONException e) {
                e.printStackTrace();
                return "0";
            } catch (Exception ee) {
                ee.printStackTrace();
                return "0";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("1")) {
                progressHUD.dismissWithSuccess(getResources().getString(R.string.success));

                adapterCat = new AdapterVideoCat(ActivityVideoCat.this,arrayList);
                recyclerView.setAdapter(adapterCat);

            } else {
                progressHUD.dismissWithFailure(getResources().getString(R.string.error));
                Toast.makeText(ActivityVideoCat.this, getResources().getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    private int getPosition(String id) {
        int count=0;
        for(int i=0;i<arrayList.size();i++)
        {
            if(id.equals(arrayList.get(i).getCategoryId()))
            {
                count = i;
                break;
            }
        }
        return count;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}
