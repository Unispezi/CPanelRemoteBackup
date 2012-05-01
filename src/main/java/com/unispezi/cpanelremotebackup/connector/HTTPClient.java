package com.unispezi.cpanelremotebackup.connector;

import com.unispezi.cpanelremotebackup.Log;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import sun.net.www.protocol.http.AuthCache;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Carsten
 * Date: 01.05.12
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
public class HTTPClient {
    private DefaultHttpClient httpClient;
    private HttpHost host;
    private BasicHttpContext localcontext;

    public HTTPClient(String hostName, int port, boolean secure, String userName, String password) {
        this.httpClient = new DefaultHttpClient();
        host = new HttpHost(hostName, port, secure ? "https" : null);
        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(hostName, port),
                new UsernamePasswordCredentials(userName, password));

//        HttpHost proxy = new HttpHost("localhost", 8888);
//        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        // Set up HTTP basic auth (without challenge) or "preemptive auth"
        // Taken from http://hc.apache.org/httpcomponents-client-ga/tutorial/html/authentication.html#d5e1031

        // Create AuthCache instance
        BasicAuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(host, basicAuth);

        // Add AuthCache to the execution context
        localcontext = new BasicHttpContext();
        localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
    }

    public void post(String uri, Map<String, Object> params) {

        //Construct request
        HttpRequest request = new HttpPost(uri);
        Log.debug("Preparing POST of " + getUriLogString(uri));

        //set params
        HttpParams httpParams = new BasicHttpParams();
        for (String key: params.keySet()){
            Object value = params.get(key);
            httpParams.setParameter(key, value);
            Log.debug("Adding param " + key + "=" + value);
        }
        request.setParams(httpParams);

        //Post
        try {
            Log.debug("POSTing " + getUriLogString(uri));
            HttpResponse response = httpClient.execute(host, request, localcontext);
            Log.debug("Response was " + response);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                Log.info("POSTing " + getUriLogString(uri) + " was successful");
            } else {
                Log.error("POSTing " + getUriLogString(uri) + " failed with status code " + statusCode);
                throw new HTTPException("POSTing " + getUriLogString(uri) + " failed with status code " + statusCode);
            }
        } catch (IOException e) {
            Log.error("POSTing " + getUriLogString(uri) + " failed with exception " + e);
            throw new HTTPException("POSTing " + getUriLogString(uri) + " failed with exception ", e);
        }
    }

    private String getUriLogString(String uri) {
        return "\"" + host + uri +"\"";
    }
}
