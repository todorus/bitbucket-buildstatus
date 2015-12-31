package com.todorus.bitbucketbuildstatus.control;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import hudson.remoting.Base64;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

import java.util.concurrent.TimeUnit;

/**
 * Created by todorus on 12/31/15.
 */
public class RetrofitAdapter {

    public static Client client;
    private static BasicAuthInterceptor basicAuthInterceptor;

    public static final String BASE_URL = "https://api.bitbucket.org/2.0";

    public static RestAdapter getAdapter(){
        // explicitly set the timeout values, so that this can be used in the background
        if(client == null) {
            OkHttpClient httpClient = new OkHttpClient();
            httpClient.setConnectTimeout(60, TimeUnit.SECONDS);
            httpClient.setReadTimeout(60, TimeUnit.SECONDS);
            client = new OkClient(new OkHttpClient());
        }

        RestAdapter.Builder  builder =  new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setConverter(getGsonConverter())
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setClient(client)
                .setRequestInterceptor(getBasicAuthInterceptor());

        return builder.build();
    }

    public static GsonConverter getGsonConverter(){
        return new GsonConverter(new Gson());
    }

    private static BasicAuthInterceptor getBasicAuthInterceptor(){
        if(basicAuthInterceptor == null){
            basicAuthInterceptor = new BasicAuthInterceptor();
        }

        return basicAuthInterceptor;
    }

    public static void setUsername(String username){
        getBasicAuthInterceptor().setUsername(username);
    }

    public static void setPassword(String password){
        getBasicAuthInterceptor().setPassword(password);
    }

    /**
     * Interceptor used to authorize requests.
     */
    private static class BasicAuthInterceptor implements RequestInterceptor {

        String username;

        String password;

        @Override
        public void intercept(RequestFacade requestFacade) {

            if (username != null && password != null) {
                final String authorizationValue = encodeCredentialsForBasicAuthorization();
                requestFacade.addHeader("Authorization", authorizationValue);
            }
        }

        private String encodeCredentialsForBasicAuthorization() {
            final String usernameAndPassword = username + ":" + password;
            return "Basic " + Base64.encode(usernameAndPassword.getBytes());
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
