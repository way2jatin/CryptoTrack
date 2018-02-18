package com.jj.cryptotrack.utils;

/**
 * Created by modi on 04/11/16.
 */

public interface APIResponseListener {

    public void handleResponse(String response);

    public void handleError(String response);

}
