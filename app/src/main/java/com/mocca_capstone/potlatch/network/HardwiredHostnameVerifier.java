package com.mocca_capstone.potlatch.network;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by nektario on 11/8/2014.
 */
public class HardwiredHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String s, SSLSession sslSession) {
        return true;
    }
}
