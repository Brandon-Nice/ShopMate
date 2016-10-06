package com.shopmate.api.net;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;

public class JsonEndpoint {
    private final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private final CloseableHttpClient client = HttpClients.custom()
            .setConnectionManager(cm)
            .build();

    private final URL baseUrl;

    public JsonEndpoint(String baseUrl) throws MalformedURLException {
        this.baseUrl = new URL(baseUrl);
    }

    public <TBody, TResponse> TResponse post(String relativeUrl, TBody body, Type responseType) throws IOException {
        Gson gson = new Gson();
        String bodyJson = gson.toJson(body);
        HttpPost post = new HttpPost(baseUrl.toString() + relativeUrl);
        post.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));
        CloseableHttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        String responseJson = EntityUtils.toString(entity);
        response.close();
        return (TResponse)gson.fromJson(responseJson, responseType);
    }
}
