package com.jj.cryptotrack.activity;

import static com.jj.cryptotrack.utils.AppConstant.COIN_MARKETCAP_ALL_COINS_URL;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jj.cryptotrack.R;
import com.jj.cryptotrack.adapter.CryptoListAdapter;
import com.jj.cryptotrack.model.CMCCoin;
import com.jj.cryptotrack.utils.APIHelper;
import com.jj.cryptotrack.utils.APIResponseListener;
import com.jj.cryptotrack.utils.CustomItemClickListener;
import com.jj.cryptotrack.utils.DividerItemDecoration;
import com.jj.cryptotrack.utils.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class CryptoListActivity extends BaseAnimationActivity implements SearchView.OnQueryTextListener{

    boolean doubleBackToExitPressedOnce = false;
    public static String IMAGE_URL_FORMAT = "https://files.coinmarketcap.com/static/img/coins/64x64/%s.png";
    public final static String DAY = "24h";
    public final static String WEEK = "7d";
    public final static String HOUR = "1h";

    private RecyclerView currencyRecyclerView;
    private CryptoListAdapter adapter;
    private ArrayList<CMCCoin> currencyItemList;
    private ArrayList<CMCCoin> filteredList = new ArrayList<>();
    private Hashtable<String, CMCCoin> currencyItemMap;
    private HashMap<String, String> searchedSymbols = new HashMap<>();
    public static boolean searchViewFocused = false;
    public static String currQuery = "";

    private MenuItem searchItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list_tabs);
        final Toolbar toolbar = findViewById(R.id.toolbar_currency_list);
        setSupportActionBar(toolbar);


        currencyRecyclerView = findViewById(R.id.currency_list_recycler_view);
        currencyRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        LinearLayoutManager llm = new LinearLayoutManager(CryptoListActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        currencyRecyclerView.setLayoutManager(llm);
        currencyItemList = new ArrayList<>();
        currencyItemMap = new Hashtable<>();
        adapter = new CryptoListAdapter(currencyItemList, CryptoListActivity.this, new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(CryptoListActivity.this, ChartActivity.class);
                intent.putExtra(ChartActivity.ARG_SYMBOL, currencyItemList.get(position).getSymbol());
                intent.putExtra(ChartActivity.ARG_ID, currencyItemList.get(position).getId());
                intent.putExtra(ChartActivity.COIN_OBJECT, currencyItemList.get(position));
                startActivity(intent);
            }
        });

        currencyRecyclerView.setAdapter(adapter);

        getCurrencyList();

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        currQuery = query;
        query = query.toLowerCase();
        filteredList.clear();
        for (CMCCoin coin : currencyItemList) {
            if (coin.getSymbol().toLowerCase().contains(query) || coin.getName().toLowerCase().contains(query)) {
                filteredList.add(coin);
            }
        }
        // TODO: Try to make this faster somehow
        adapter = new CryptoListAdapter(filteredList, this, new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(CryptoListActivity.this, ChartActivity.class);
                intent.putExtra(ChartActivity.ARG_SYMBOL, filteredList.get(position).getSymbol());
                intent.putExtra(ChartActivity.ARG_ID, filteredList.get(position).getId());
                intent.putExtra(ChartActivity.COIN_OBJECT, filteredList.get(position));
                startActivity(intent);
            }
        });
        currencyRecyclerView.setAdapter(adapter);
        return true;
    }

    @Override
    protected void onLeaveThisActivity() {
    }

    public void getCurrencyList() {

        APIHelper apiHelper = new APIHelper(this);
        apiHelper.callJsonWsGet(COIN_MARKETCAP_ALL_COINS_URL,null, coinListener,true);
    }

    private APIResponseListener coinListener = new APIResponseListener() {
        @Override
        public void handleResponse(final String response) {

            try {
                ObjectMapper mapper = new ObjectMapper();
                List<CMCCoin> cmcCoinList = mapper.readValue(response, new TypeReference<List<CMCCoin>>() {
                });

                Parcelable recyclerViewState;
                recyclerViewState = currencyRecyclerView.getLayoutManager().onSaveInstanceState();
                searchedSymbols.clear();
                if (searchViewFocused) {
                    for (CMCCoin coin : filteredList) {
                        searchedSymbols.put(coin.getSymbol(), coin.getSymbol());
                    }
                } else {
                    currencyItemList.clear();
                    currencyItemMap.clear();
                }
                try {
                    if (searchViewFocused) { // Copy some code here to make the checks faster
                        ArrayList<CMCCoin> tempList = new ArrayList<>();
                        for (CMCCoin coin : cmcCoinList) {
                            if (searchedSymbols.get(coin.getSymbol()) != null) {
                                tempList.add(coin);
                            }
                        }
                        adapter.setCurrencyList(tempList);
                    } else {
                        for (CMCCoin coin : cmcCoinList) {
                            currencyItemList.add(coin);
                            currencyItemMap.put(coin.getSymbol(), coin);
                        }
                        adapter.setCurrencyList(currencyItemList);
                    }
                    adapter.notifyDataSetChanged();
                    currencyRecyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                currencyRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            }
            catch (Exception e){
                Log.e("ERROR", "Server Error: " + e.getMessage(),e);
            }
        }

        @Override
        public void handleError(final String response) {
            Log.d("ERROR", "Server Error: " + response);
        }
    };

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap back again to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.all_currency_list_tab_menu, menu);
        searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        // Detect SearchView icon clicks
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchViewFocused = true;
                setItemsVisibility(menu, searchItem, false);
            }
        });
        // Detect SearchView close
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchViewFocused = false;
                setItemsVisibility(menu, searchItem, true);
                return false;
            }
        });
        if (searchViewFocused) getSupportActionBar().setTitle("");
        return super.onCreateOptionsMenu(menu);
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
        if (!visible) {
            getSupportActionBar().setTitle("");
        } else {
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.currency_refresh_button:
                onRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onRefresh() {
        getCurrencyList();
    }


}
