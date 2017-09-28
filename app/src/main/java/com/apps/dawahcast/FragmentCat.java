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

import com.apps.adapter.AdapterCat;
import com.apps.item.ItemCat;
import com.apps.utils.Constant;
import com.apps.utils.JsonUtils;
import com.apps.utils.RecyclerItemClickListener;
import com.apps.utils.ZProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentCat extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ItemCat> arrayList;
    AdapterCat adapterCat;
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
        gridLayoutManager = new GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new LoadCat().execute(Constant.URL_CAT);
        } else {

        }

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Intent intent = new Intent(getActivity(),SongByCatActivity.class);
//                intent.putExtra("cid",arrayList.get(getPosition(adapterCat.getID(position))).getCategoryId());
//                startActivity(intent);
                Constant.isBackStack = true;

                FragmentManager fm = getFragmentManager();
                FragmentSongByCat f1 = new FragmentSongByCat();
                FragmentTransaction ft = fm.beginTransaction();

                Bundle bundl = new Bundle();
                bundl.putString("cid", arrayList.get(getPosition(adapterCat.getID(position))).getCategoryId());
                bundl.putString("cname", arrayList.get(getPosition(adapterCat.getID(position))).getCategoryName());
                f1.setArguments(bundl);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(getFragmentManager().findFragmentByTag("Categories"));
                ft.add(R.id.fragment, f1,"sbc");
//                ft.replace(R.id.fragment, f1, "sbc");
                ft.addToBackStack("sbc");
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
                recyclerView.setAdapter(adapterCat);
                adapterCat.notifyDataSetChanged();
            } else {
                adapterCat.getFilter().filter(s);
                adapterCat.notifyDataSetChanged();
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

                    String id = objJson.getString(Constant.TAG_CID);
                    String name = objJson.getString(Constant.TAG_CAT_NAME);

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

                adapterCat = new AdapterCat(getActivity(),arrayList);
                recyclerView.setAdapter(adapterCat);

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
            if(id.equals(arrayList.get(i).getCategoryId()))
            {
                count = i;
                break;
            }
        }
        return count;
    }

    @Override
    public void onResume() {
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Lecturer");
        super.onResume();
    }
}
