package com.wandoujia.hadoop.monitor.jmx;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import com.wandoujia.common.httpclient.HttpClientUtils;

public class DataNodeJMX {

    public static String fetchJMX(String url) throws IOException {
        HttpClient httpClient = HttpClientUtils.getClient();
        GetMethod get = new GetMethod(url);
        int status = httpClient.executeMethod(get);
        if (status == HttpStatus.SC_OK) {
            return get.getResponseBodyAsString();
        }
        throw new IOException("http request error code: " + status);
    }
}
