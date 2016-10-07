package com.shopmate.api.net;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JsonEndpoint {
    private final URL baseUrl;

    public JsonEndpoint(String baseUrl) throws MalformedURLException {
        this.baseUrl = new URL(baseUrl);
    }

    public <TBody, TResponse> TResponse post(String relativeUrl, TBody body, Type responseType) throws IOException {
        URL url = new URL(this.baseUrl.toString() + relativeUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        Gson gson = new Gson();
        String bodyJson = gson.toJson(body);
        writer.write(bodyJson);
        writer.close();
        InputStream stream;
        try {
            stream = connection.getInputStream();
        } catch (IOException e) {
            stream = connection.getErrorStream();
        }
        String responseJson = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
        stream.close();
        return (TResponse)gson.fromJson(responseJson, responseType);
    }
}
