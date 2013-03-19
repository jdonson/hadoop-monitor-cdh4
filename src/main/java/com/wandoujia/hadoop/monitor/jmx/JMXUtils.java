package com.wandoujia.hadoop.monitor.jmx;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import com.wandoujia.common.httpclient.HttpClientUtils;

public class JMXUtils {
    public static int CONN_TIMEOUT = 30 * 1000;

    public static int SO_TIMEOUT = 30 * 1000;

    public static String fetchJMX(String url) throws IOException {
        HttpClient client = HttpClientUtils.getNewClient(SO_TIMEOUT,
                CONN_TIMEOUT);
        GetMethod get = new GetMethod(url);
        int status = client.executeMethod(get);
        if (status == HttpStatus.SC_OK) {
            return get.getResponseBodyAsString();
        }
        throw new IOException("http request error code: " + status);
    }
}
