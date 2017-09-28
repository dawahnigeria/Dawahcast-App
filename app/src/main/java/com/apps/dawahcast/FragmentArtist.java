package com.apps.dawahcast;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.apps.adapter.AdapterArtist;
import com.apps.item.ItemArtist;
import com.apps.utils.Constant;
import com.apps.utils.JsonUtils;
import com.apps.utils.RecyclerItemClickListener;
import com.apps.utils.ZProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentArtist extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ItemArtist> arrayList;
    AdapterArtist adapterArtist;
    ZProgressHUD progressHUD;
    GridLayoutManager gridLayoutManager;
    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View rootView = inflater.inflate(R.layout.fragment_cat, container, false);

        progressHUD = ZProgressHUD.getInstance(getActivity());
        progressHUD.setMessage(getActivity().getResources().getString(R.string.loading));
        progressHUD.setSpinnerType(ZProgressHUD.FADED_ROUND_SPINNER);

        arrayList = new ArrayList<>();
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView_cat);
        gridLayoutManager = new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new LoadCat().execute(Constant.URL_ARTIST);
        } else {

        }

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Intent intent = new Intent(getActivity(),SongByArtistActivity.class);
//                intent.putExtra("artist",arrayList.get(getPosition(adapterArtist.getID(position))).getName());
//                intent.putExtra("image",arrayList.get(getPosition(adapterArtist.getID(position))).getImage());
//                startActivity(intent);

                Constant.isBackStack = true;

                FragmentManager fm = getFragmentManager();
                FragmentSongByArtist f1 = new FragmentSongByArtist();
                FragmentTransaction ft = fm.beginTransaction();

                Bundle bundl = new Bundle();
                bundl.putString("artist",arrayList.get(getPosition(adapterArtist.getID(position))).getName());
                bundl.putString("image",arrayList.get(getPosition(adapterArtist.getID(position))).getImage());
                f1.setArguments(bundl);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(getFragmentManager().findFragmentByTag("Artist"));
                ft.add(R.id.fragment, f1,"sba");
//                ft.replace(R.id.fragment, f1, "sba");
                ft.addToBackStack("sba");
                ft.commit();
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
                recyclerView.setAdapter(adapterArtist);
                adapterArtist.notifyDataSetChanged();
            } else {
                adapterArtist.getFilter().filter(s);
                adapterArtist.notifyDataSetChanged();
            }
            return true;
        }
    };

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

                    String id = objJson.getString(Constant.TAG_ID);
                    String name = objJson.getString(Constant.TAG_ARTIST_NAME);
                    String image = objJson.getString(Constant.TAG_ARTIST_IMAGE);
                    String thumb = objJson.getString(Constant.TAG_ARTIST_THUMB);

                    ItemArtist objItem = new ItemArtist(id,name,image,thumb);
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

                adapterArtist = new AdapterArtist(getActivity(),arrayList);
                recyclerView.setAdapter(adapterArtist);

            } else {
                progressHUD.dismissWithFailure(getResources().getString(R.string.error));
                Toast.makeText(getActivity(), getResources().getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    private int getPosition(String id) {
        int count=0;
        for(int i=0;i<arrayList.size();i++)
        {
            if(id.equals(arrayList.get(i).getId()))
            {
                count = i;
                break;
            }
        }
        return count;
    }

    @Override
    public void onResume() {
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Categories");
        super.onResume();
    }
}
