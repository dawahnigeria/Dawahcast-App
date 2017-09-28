package com.apps.dawahcast;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.adapter.AdapterSongList;
import com.apps.item.ItemSong;
import com.apps.utils.Constant;
import com.apps.utils.JsonUtils;
import com.apps.utils.OnLoadMoreListener;
import com.apps.utils.RecyclerItemClickListener;
import com.apps.utils.ZProgressHUD;
import com.google.android.gms.ads.AdListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentSongByCat extends Fragment {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<ItemSong> arrayList;
    public static AdapterSongList adapterSongList;
    ZProgressHUD progressHUD;
    LinearLayoutManager linearLayoutManager;
    SearchView searchView;
    String cid = "", cname = "";
    TextView textView_empty;
    boolean isLoadMore = false, isFirst = true;
    int page = 1;
    int TOTAL_LIST_ITEMS;
    public int NUM_ITEMS_PAGE;
    private int noOfBtns;
    String mp3url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View rootView = inflater.inflate(R.layout.activity_song_by_cat, container, false);

        progressHUD = ZProgressHUD.getInstance(getActivity());
        progressHUD.setMessage(getResources().getString(R.string.loading));
        progressHUD.setSpinnerType(ZProgressHUD.FADED_ROUND_SPINNER);

        cid = getArguments().getString("cid");
        cname = getArguments().getString("cname");
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(cname);
        arrayList = new ArrayList<>();
        textView_empty = (TextView) rootView.findViewById(R.id.textView_empty_artist);
        NUM_ITEMS_PAGE = Integer.parseInt(getString(R.string.noofloadmore));

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_songbycat);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new LoadSongs().execute(Constant.URL_SONG_BY_CAT + cid + "&page=" + page);
            Log.e("mainimg", "" + Constant.URL_SONG_BY_CAT + cid + "&page=" + page);
        }

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showInter(position);
            }
        }));

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
//            recyclerView.setTextFilterEnabled(true);

            if (searchView.isIconified()) {
//                listView.clearTextFilter();
                recyclerView.setAdapter(adapterSongList);
                adapterSongList.notifyDataSetChanged();
                adapterSongList.setLoaded();
            } else {
                adapterSongList.getFilter().filter(s);
                adapterSongList.notifyDataSetChanged();
                adapterSongList.setLoaded();
            }
            return true;
        }
    };

    private class LoadSongs extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            if (isFirst)
                progressHUD.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            if (isFirst)
                progressHUD.dismiss();
            else {
                arrayList.remove(arrayList.size() - 1);
                adapterSongList.notifyItemRemoved(arrayList.size());
                adapterSongList.notifyDataSetChanged();
            }
            try {
                String json = JsonUtils.getJSONString(strings[0]);

                JSONObject mainJson = new JSONObject(json);
                JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
                JSONObject objJson = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    objJson = jsonArray.getJSONObject(i);

                    String id = objJson.getString(Constant.TAG_ID);
                    String cid = objJson.getString(Constant.TAG_CAT_ID);
                    String cname = objJson.getString(Constant.TAG_CAT_NAME);
                    String artist = objJson.getString(Constant.TAG_ARTIST);
                    String name = objJson.getString(Constant.TAG_SONG_NAME);
                    String url = objJson.getString(Constant.TAG_MP3_URL);
                    String desc = objJson.getString(Constant.TAG_SHARE_LINK);
                    String duration = objJson.getString(Constant.TAG_DURATION);
                    String thumb = objJson.getString(Constant.TAG_THUMB_B).replace(" ", "%20");
                    String thumb_small = objJson.getString(Constant.TAG_THUMB_B).replace(" ", "%20");
                    TOTAL_LIST_ITEMS = Integer.parseInt(objJson.getString("total_rec"));
                    mp3url = objJson.getString(Constant.TAG_SHARE_LINK);

                    ItemSong objItem = new ItemSong(id, cid, cname, artist, url, thumb, thumb_small, name, duration, desc, mp3url);
                    arrayList.add(objItem);
                }
                int val = TOTAL_LIST_ITEMS % NUM_ITEMS_PAGE;
                val = val == 0 ? 0 : 1;
                noOfBtns = TOTAL_LIST_ITEMS / NUM_ITEMS_PAGE + val;
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
            if (isFirst) {
                adapterSongList = new AdapterSongList(getActivity(), arrayList, recyclerView);
                recyclerView.setAdapter(adapterSongList);
                adapterSongList.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {

                        if (page >= noOfBtns) {
                            Toast.makeText(getActivity(), "NO MORE VIDEO", Toast.LENGTH_LONG).show();

                        } else {
                            arrayList.add(null);
                            recyclerView.post(new Runnable() {
                                public void run() {
                                    adapterSongList.notifyItemInserted(arrayList.size() - 1);
                                }
                            });

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if (JsonUtils.isNetworkAvailable(getActivity())) {
                                        isFirst = false;
                                        page = page + 1;
                                        new LoadSongs().execute(Constant.URL_SONG_BY_CAT + cid + "&page=" + page);
                                        Log.e("mainimg", "" + Constant.URL_SONG_BY_CAT + cid + "&page=" + page);
                                    }
                                }
                            }, 500);
                        }
                    }
                });
            } else {
                adapterSongList.notifyDataSetChanged();
                adapterSongList.setLoaded();
            }

            if (arrayList.size() == 0) {
                textView_empty.setVisibility(View.VISIBLE);
            } else {
                textView_empty.setVisibility(View.GONE);
            }
            super.onPostExecute(s);
        }
    }

    private int getPosition(String id) {
        int count = 0;
        adapterSongList.notifyDataSetChanged();
        for (int i = 0; i < arrayList.size(); i++) {
            if (id.equals(arrayList.get(i).getId())) {
                count = i;
                break;

            }
        }

        return count;
    }

//    private void loadInter() {
//        mInterstitial = new InterstitialAd(getActivity());
//        mInterstitial.setAdUnitId(getResources().getString(R.string.admob_intertestial_id));
//        mInterstitial.loadAd(new AdRequest.Builder().build());
//    }

    private void showInter(final int pos) {
        Constant.adCount = Constant.adCount + 1;
        if (Constant.adCount % Constant.adDisplay == 0) {
            ((MainActivity) getActivity()).mInterstitial.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                    playIntent(pos);
                    super.onAdClosed();
                }
            });
            if (((MainActivity) getActivity()).mInterstitial.isLoaded()) {
                ((MainActivity) getActivity()).mInterstitial.show();
                ((MainActivity) getActivity()).loadInter();
            } else {
                playIntent(pos);
            }
        } else {
            playIntent(pos);
        }
    }

    private void playIntent(int position) {
        Constant.frag = "cat";
        Constant.arrayList_play.clear();
        Constant.arrayList_play.addAll(arrayList);
        Constant.playPos = getPosition(adapterSongList.getID(position));
        ;
        ((MainActivity) getActivity()).changeText(arrayList.get(position).getMp3Name(), arrayList.get(position).getCategoryName(), position + 1, arrayList.size(), arrayList.get(position).getDuration(), arrayList.get(position).getImageBig(), "cat");

        Constant.context = getActivity();
        Intent intent = new Intent(getActivity(), PlayerService.class);
        intent.setAction(PlayerService.ACTION_FIRST_PLAY);
        getActivity().startService(intent);
    }
}