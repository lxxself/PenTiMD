package com.lxxself.pentimd.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lxxself.pentimd.activity.WebViewActivity;
import com.lxxself.pentimd.adapter.RealmAdapter;
import com.lxxself.pentimd.model.ItemObj;
import com.lxxself.pentimd.R;
import com.lxxself.pentimd.util.RestClient;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.lxxself.pentimd.util.JSONUtil.getList;
import static com.lxxself.pentimd.util.NetConnectionUtil.isNetworkAvailable;
import static com.lxxself.pentimd.util.ShareUtil.shareTO;

/**
 * Created by lxxself on 2015/11/18.
 */
public class BaseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RealmAdapter.onMyItemLongClickListener, RealmAdapter.onMyItemClickListener {

    protected Realm realm;
    protected RealmResults<ItemObj> list;
    protected WorkerThread workerThread;
    protected ListView recyclerView;
    protected SwipeRefreshLayout refreshLayout;
    protected View view;
    protected int p = 1;
    protected int limit = 10;
    protected int lastVisibleItem;
    protected RealmAdapter adapter;
    protected String url = "http://appb.dapenti.com/index.php?s=/Home/api/";
    protected String type = "";
    protected int latestId = 0;
    protected String lastJSON = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(getContext());
        if (!isNetworkAvailable(getActivity())) {
            Toast.makeText(getContext(), "当前网络状态不可用", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        KLog.d("onCreateView");
        view = inflater.inflate(R.layout.fragment_common, null);
        recyclerView = (ListView) view.findViewById(R.id.list_tugua);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
        refreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimaryNight, R.color.colorPrimaryLight);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

//        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setHasFixedSize(true);
        recyclerView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && view.getLastVisiblePosition() + 1 >= adapter.getRealmResults().size()) {
                    if (isNetworkAvailable(getActivity())) {
                        refreshLayout.setRefreshing(true);
                        requestNetMoreList(++p, limit);
                    } else {
                        Toast.makeText(getContext(), "当前网络状态不可用", Toast.LENGTH_LONG).show();
                    }
//                    KLog.d("onScrollStateChanged",view.getLastVisiblePosition()+"");
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

        list = realm.where(ItemObj.class).equalTo("type", type).findAllSorted("id", RealmResults.SORT_ORDER_DESCENDING);
        adapter = new RealmAdapter(getContext(), list, true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        requestList();
//        requestNetRefreshList();
        return view;
    }

    private void requestList() {
        if (isNetworkAvailable(getActivity())) {
            if (list == null || list.size() == 0) {
                refreshLayout.setRefreshing(true);
                requestNetMoreList(p, limit);
            } else {
                latestId = list.get(0).getId();
            }
        }


    }


    private void requestNetRefreshList() {
        RequestParams params = new RequestParams();
        params.put("p", 0);
        params.put("limit", 1);
        RestClient.postJson(url + type, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONObject jsonObject = response;
                try {
                    String link = response.getJSONArray("data").getJSONObject(0).getString("link");
                    int id = Integer.parseInt(link.substring(link.indexOf("id=") + 3));
                    KLog.d("RefreshList", id + "---" + latestId);
                    if (id != latestId) {
                        requestNetMoreList(p, limit);
                    } else {
                        Toast.makeText(getContext(),"已是最新内容",Toast.LENGTH_LONG).show();
                        refreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "没有获取到内容，请稍后再试", Toast.LENGTH_SHORT).show();
//                Snackbar.make(view, "操作过于频繁", Snackbar.LENGTH_SHORT).show();
                KLog.e("没有获取到信息");
            }
        });
    }

    protected void requestNetMoreList(int p, final int limit) {
        RequestParams params = new RequestParams();
        params.put("p", p);
        params.put("limit", limit);
        RestClient.postJson(url + type, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        KLog.d("requestNetMoreList",lastJSON.toString());
                        try {
                            if (response.getJSONArray("data").getJSONObject(0).getString("pubDate").equals(lastJSON)) {
                                Toast.makeText(getContext(),"没有更多内容了",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            getList(getContext(), response, type);
                            if (list.size() != 0) {
                                latestId = list.get(0).getId();
                            }
                            KLog.d("MoreList", response.toString());
                            lastJSON = response.getJSONArray("data").getJSONObject(0).getString("pubDate");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            refreshLayout.setRefreshing(false);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable
                            throwable, JSONObject errorResponse) {
                        refreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), "没有获取到内容，请稍后再试", Toast.LENGTH_SHORT).show();
                        KLog.e("没有获取到信息");
                    }
                }

        );


    }

    @Override
    public void onRefresh() {
        if (isNetworkAvailable(getActivity())) {
            requestNetRefreshList();
        } else {
            refreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), "当前网络状态不可用", Toast.LENGTH_LONG).show();
        }
        refreshLayout.invalidate();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        String title = list.get(position).getTitle();
        String content = list.get(position).getMobileLink();
        shareTO(getContext(), title, content);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        String url = list.get(position).getMobileLink();
        String title = list.get(position).getTitle();
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        KLog.d("onItemClick", url);
        KLog.d("onItemClick", position + "");
        startActivity(intent);
    }
}
