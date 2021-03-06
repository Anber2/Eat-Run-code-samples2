package com.mawaqaa.eatandrun.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mawaqaa.eatandrun.Constants.AppConstants;
import com.mawaqaa.eatandrun.R;
import com.mawaqaa.eatandrun.Utilities.PreferenceUtil;
import com.mawaqaa.eatandrun.activity.EatndRunBaseActivity;
import com.mawaqaa.eatandrun.adapter.NotificationListAdapter;
import com.mawaqaa.eatandrun.data.NotificationListData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by HP on 11/27/2017.
 */
public class NotificationListFragment extends EatndRunBaseFragment {

    String TAG = "ResListFrg";
    NotificationListAdapter notificationListAdapter;
    ListView listvnotflist;
    ArrayList<NotificationListData> notificationListDataArrayList;
    NotificationListData notificationListData;
    CheckBox checkBox_notification_item;
    Button btn_notificationEdit;
    private ProgressDialog progressBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        Activity = (EatndRunBaseActivity) this.getActivity();
    }

    public void onResume() {
        Log.d(TAG, "onResume" + this.getClass().getName());
        super.onResume();
        ((EatndRunBaseActivity) getActivity()).BaseFragment = this;
        //((AjaratyMainActivity) getActivity()).hideLogo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View v = inflater.inflate(R.layout.fragment_registerorlogin, container, false);
        View v = inflater.inflate(R.layout.notificationlistfragment, container, false);

        listvnotflist = (ListView) v.findViewById(R.id.listvnotflist);

        btn_notificationEdit = (Button) v.findViewById(R.id.btn_notificationEdit);
        btn_notificationEdit.setVisibility(View.GONE);
        /*btn_notificationEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationListAdapter.ViewHolder.checkBox_notification_item.setVisibility(View.VISIBLE);

            }
        });*/

        progressBar = ProgressDialog.show(getActivity(), "", getString(R.string.progressbar_please_wait), true, false);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    JSONObject jsonObject = new JSONObject();

                    jsonObject.putOpt("CustomerId", PreferenceUtil.getUserId(Activity));

                    jsonObject.putOpt(AppConstants.LanguageId, PreferenceUtil.getLanguage(Activity));

                    jsonObject.putOpt(AppConstants.SecurityKey, AppConstants.SecurityKeyValue);

                    makeJsonRestaurantCustGuideSNotificationListtringReq(AppConstants.EatndRun_PUSHMOBILENOTIFICATIONLIST, jsonObject);

                } catch (Exception xx) {
                    xx.toString();
                }

            }
        }).start();


        return v;
    }

    private String makeJsonRestaurantCustGuideSNotificationListtringReq(String urlPost, final JSONObject jsonObject) {
        StringRequest stringRequest = null;
        final String[] resultConn = {""};
        String string_json = "";


        try {
            RequestQueue queue = Volley.newRequestQueue(this.getActivity());

            final String finalString_json = string_json;

            stringRequest = new StringRequest(Request.Method.POST, urlPost,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                if (jsonObj != null) {

                                    JSONArray jsonArray = jsonObj.getJSONArray("notificationItemList");
                                    notificationListDataArrayList = new ArrayList<NotificationListData>();

                                    for (int t = 0; t < jsonArray.length(); t++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(t);


                                        String NotificationId = jsonObject1.getString("NotificationId");
                                        String NotificationDescription = jsonObject1.getString("NotificationDescription");
                                        String LastModifiedDate = jsonObject1.getString("LastModifiedDate");

                                        notificationListData = new NotificationListData(NotificationId, NotificationDescription, LastModifiedDate);
                                        notificationListDataArrayList.add(notificationListData);
                                    }

                                    notificationListAdapter = new NotificationListAdapter(Activity, notificationListDataArrayList);

                                    listvnotflist.setAdapter(notificationListAdapter);

                                    if (listvnotflist.getCount() == 0) {

                                        Toast.makeText(getActivity(), R.string.notificationlistfragment_empty_txt, Toast.LENGTH_LONG).show();

                                    }


                                }


                            } catch (Exception xx) {
                                Log.e(TAG, "   " + xx.toString());
                                xx.toString();
                            }
                            progressBar.dismiss();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    String xx = error.toString();
                    progressBar.dismiss();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();

                        }
                    });

                }
            }) {


                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    try {

                        Iterator<?> keys = jsonObject.keys();

                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            String value = jsonObject.getString(key);
                            params.put(key, value);

                        }


                    } catch (Exception xx) {
                        xx.toString();
                    }
                    return params;
                }


                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    try {

                        String jsonString = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers));

                        return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));


                    } catch (UnsupportedEncodingException e) {
                        return Response.error(new ParseError(e));
                    }
                }


            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(stringRequest);


        } catch (Exception e) {
            progressBar.dismiss();
            e.toString();
            return e.toString();
        }

        return resultConn[0];

    }


}



