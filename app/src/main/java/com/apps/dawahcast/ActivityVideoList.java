package com.apps.dawahcast;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.apps.adapter.AdapterVideoList;
import com.apps.item.ItemVideo;
import com.apps.utils.Constant;
import com.apps.utils.JsonUtils;
import com.apps.utils.OnLoadMoreListener;
import com.apps.utils.ZProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ActivityVideoList extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ItemVideo> arrayList;
    AdapterVideoList adapterArtist;
    ZProgressHUD progressHUD;
    GridLayoutManager gridLayoutManager;
    SearchView searchView;
    boolean isLoadMore = false, isFirst = true;
    int page = 1;
    int TOTAL_LIST_ITEMS;
    public int NUM_ITEMS_PAGE;
    private int noOfBtns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videocat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(Constant.TAG_VIDEO_CAT_NAMEE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusBar));
        }

        progressHUD = ZProgressHUD.getInstance(ActivityVideoList.this);
        progressHUD.setMessage(ActivityVideoList.this.getResources().getString(R.string.loading));
        progressHUD.setSpinnerType(ZProgressHUD.FADED_ROUND_SPINNER);

        arrayList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_cat);
        gridLayoutManager = new GridLayoutManager(ActivityVideoList.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        NUM_ITEMS_PAGE = Integer.parseInt(getString(R.string.noofloadmore));

        if (JsonUtils.isNetworkAvailable(ActivityVideoList.this)) {
            new LoadCat().execute(Constant.URL_VIDEO_CATLIST + Constant.TAG_VIDEO_CAT_IDD + "&page=" + page);
            Log.e("url1", "" + Constant.URL_VIDEO_CATLIST + Constant.TAG_VIDEO_CAT_IDD + "&page=" + page);
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
                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText.toString().toLowerCase(Locale.getDefault());
                if (adapterArtist != null) {
                    adapterArtist.filter(text);
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
            super.onPreExecute();
            if (isFirst)
                progressHUD.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (isFirst)
                progressHUD.dismiss();
            else {
                arrayList.remove(arrayList.size() - 1);
                adapterArtist.notifyItemRemoved(arrayList.size());
                adapterArtist.notifyDataSetChanged();
            }
            try {


                JSONObject mainJson = new JSONObject(result);
                JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
                JSONObject objJson = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    objJson = jsonArray.getJSONObject(i);

                    String id = objJson.getString(Constant.TAG_VIDEO_CATLIST_ID);
                    String name = objJson.getString(Constant.TAG_VIDEO_CATLIST_NAME);
                    String image = objJson.getString(Constant.TAG_VIDEO_CATLIST_IMAGE);
                    String url = objJson.getString(Constant.TAG_VIDEO_CATLIST_VID);
                    String num = objJson.getString(Constant.TAG_VIDEO_CATLIST_NUM);
                    TOTAL_LIST_ITEMS = Integer.parseInt(objJson.getString("total_rec"));
                    ItemVideo objItem = new ItemVideo(id, name, image, url, num);
                    arrayList.add(objItem);
                }
                int val = TOTAL_LIST_ITEMS % NUM_ITEMS_PAGE;
                val = val == 0 ? 0 : 1;
                noOfBtns = TOTAL_LIST_ITEMS / NUM_ITEMS_PAGE + val;

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isFirst) {
                displayData();
            } else {
                adapterArtist.notifyDataSetChanged();
                adapterArtist.setLoaded();
            }

        }

        private void displayData() {
            progressHUD.dismissWithSuccess(getResources().getString(R.string.success));

            adapterArtist = new AdapterVideoList(ActivityVideoList.this, arrayList, recyclerView);
            recyclerView.setAdapter(adapterArtist);

            adapterArtist.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {

                    if (page >= noOfBtns) {
                        Toast.makeText(ActivityVideoList.this, "NO MORE VIDEO", Toast.LENGTH_LONG).show();

                    } else {
                        arrayList.add(null);
                         recyclerView.post(new Runnable() {
                            public void run() {
                                adapterArtist.notifyItemInserted(arrayList.size() - 1);
                            }
                        });
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (JsonUtils.isNetworkAvailable(ActivityVideoList.this)) {
                                    isFirst = false;
                                    page = page + 1;
                                    new LoadCat().execute(Constant.URL_VIDEO_CATLIST + Constant.TAG_VIDEO_CAT_IDD + "&page=" + page);
                                }

                            }
                        }, 500);
                    }
                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}