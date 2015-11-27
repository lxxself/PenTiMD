package com.lxxself.pentimd.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.flyco.tablayout.SlidingTabLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lxxself.pentimd.R;
import com.lxxself.pentimd.fragment.DuanZiFragment;
import com.lxxself.pentimd.fragment.LeHuoFragment;
import com.lxxself.pentimd.fragment.TuGuaFragment;
import com.lxxself.pentimd.fragment.YiTuFragment;
import com.lxxself.pentimd.model.ItemObj;
import com.lxxself.pentimd.util.ViewFindUtils;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private static final String[] titles={"图卦","乐活","意图","段子"};
    private List<Fragment> fragments = new ArrayList<Fragment>();
    private Realm realm;
    private SharedPreferences sp;
    private boolean nightMode = false;

    @Override
    protected void onStart() {
        super.onStart();
        if (nightMode!=sp.getBoolean("nightMode", false)) {
            recreate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp=getSharedPreferences("Setting",MODE_PRIVATE);
        if (sp.getBoolean("nightMode", false)) {
            setTheme(R.style.NightTheme);
            nightMode = true;
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_main);
//        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
//        Realm.deleteRealm(realmConfig);
//        realm = Realm.getInstance(realmConfig);
        realm = Realm.getInstance(this);
        //开发时打开
//        realm.beginTransaction();
//        if (!realm.getTable(ItemObj.class).isEmpty()) {
//            realm.clear(ItemObj.class);
//        }
//        realm.commitTransaction();

        initView();
//        testRequest();
    }

    private void testRequest() {

        String url = "http://appb.dapenti.com/index.php?s=/Home/api/tugua";
        RequestParams params = new RequestParams();
        params.put("p", 1);
        params.put("limit", 5);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                KLog.d(response.toString());
                JSONObject jsonObject = response;
                try {
                    JSONArray array = jsonObject.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String title = object.getString("title");
                        if (!title.equals("AD")) {
                            KLog.d("title", title);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                KLog.e(errorResponse.toString());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initView() {
        View decorView = getWindow().getDecorView();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        addFragment();
        ViewPager vp = ViewFindUtils.find(decorView, R.id.tab_viewpager);
        vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        SlidingTabLayout tabLayout = ViewFindUtils.find(decorView, R.id.tab_layout);
        tabLayout.setViewPager(vp, titles);


    }

    private void addFragment() {
        fragments.add(TuGuaFragment.getInstance());
        fragments.add(LeHuoFragment.getInstance());
        fragments.add(YiTuFragment.getInstance());
        fragments.add(DuanZiFragment.getInstance());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
