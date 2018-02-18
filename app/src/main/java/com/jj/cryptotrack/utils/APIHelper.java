package com.jj.cryptotrack.utils;

import android.app.ProgressDialog;
import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jj.cryptotrack.R;
import java.util.Map;


public class APIHelper {


    private Context myContext;

    public APIHelper(Context context) {
        myContext = context;
    }

    public void callJsonWsGet(String url, Map<String, String> jsonParams, APIResponseListener listener,
                              boolean showProgress) {
        callJsonWs(url, jsonParams, listener, showProgress, Request.Method.GET, getRetryPolicy());
    }

    public void callJsonWs(final String url, final Map<String, String> jsonParams, final APIResponseListener listener,
                           final boolean showProgress, int requestMethod, RetryPolicy retryPolicy) {

        final ProgressDialog progressDialog = new ProgressDialog(myContext);
        if (showProgress) {
            progressDialog.setCancelable(false);
            progressDialog.setMessage(myContext.getResources().getString(R.string.loading_message));
            progressDialog.show();
        }

        RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
        StringRequest postRequest = new StringRequest(requestMethod, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.handleResponse(response);
                if (showProgress && progressDialog.isShowing()) {
                    progressDialog.cancel();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (showProgress && progressDialog.isShowing()) {
                    progressDialog.cancel();
                }
                if (error instanceof NetworkError) {
                    listener.handleError("Internet not available. Please check connectivity and retry");
                } else if (error instanceof ServerError) {
                    listener.handleError("Server is not responding at the moment");
                } else if (error instanceof AuthFailureError) {
                    listener.handleError("Server Authentication Failed");
                } else if (error instanceof ParseError) {
                    listener.handleError("Server is not responding at the moment");
                } else if (error instanceof NoConnectionError) {
                    listener.handleError("Internet not available");
                } else if (error instanceof TimeoutError) {
                    listener.handleError("Request time out due to slow Internet. Please Retry!");
                } else {
                    listener.handleError("Failed to process request at this time. Please retry");
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return jsonParams;
            }
        };
        postRequest.setRetryPolicy(retryPolicy);
        postRequest.setShouldCache(true);
        queue.add(postRequest);
    }

    /**
     * Retry policy for get data calls
     *
     * @return
     */
    public static DefaultRetryPolicy getRetryPolicy() {
        return new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

}


