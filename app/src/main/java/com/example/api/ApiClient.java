package com.example.api;

import android.content.Context;
import com.example.utils.AppPreferences;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente centralizado para comunicação com a API REST do GymBuddy II.
 * A URL base está centralizada aqui para facilitar a troca de servidor.
 */
public class ApiClient {

    /**
     * URL BASE CENTRALIZADA conforme solicitado na especificação:
     * Pode ser alterada aqui ou dinamicamente nas Configurações do aplicativo.
     */
    public static final String DEFAULT_BASE_URL = "http://ENDERECO_DA_API/";

    private static Retrofit retrofit = null;
    private static GymBuddyApiService apiService = null;
    private static CommunityService communityService = null;
    private static String currentBaseUrl = "";

    private static synchronized void ensureRetrofit(Context context) {
        String configuredUrl = DEFAULT_BASE_URL;
        if (context != null) {
            AppPreferences prefs = new AppPreferences(context);
            configuredUrl = prefs.getApiBaseUrl();
            if (configuredUrl == null || configuredUrl.trim().isEmpty()) {
                configuredUrl = DEFAULT_BASE_URL;
            }
        }

        // Garante que a URL termine com "/"
        if (!configuredUrl.endsWith("/")) {
            configuredUrl += "/";
        }

        if (retrofit == null || !configuredUrl.equals(currentBaseUrl)) {
            currentBaseUrl = configuredUrl;
            apiService = null;
            communityService = null;

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(8, TimeUnit.SECONDS)
                    .writeTimeout(8, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json");

                        // Adiciona token se disponível
                        if (context != null) {
                            AppPreferences prefs = new AppPreferences(context);
                            String token = prefs.getAuthToken();
                            if (token != null && !token.isEmpty()) {
                                builder.header("Authorization", "Bearer " + token);
                            }
                        }
                        return chain.proceed(builder.build());
                    })
                    .addInterceptor(logging)
                    .build();

            // Valida se a URL é sintaticamente aceitável pelo Retrofit
            String validUrl = currentBaseUrl;
            if (!validUrl.startsWith("http://") && !validUrl.startsWith("https://")) {
                validUrl = "http://10.0.2.2:8080/";
            }

            try {
                retrofit = new Retrofit.Builder()
                        .baseUrl(validUrl)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            } catch (Exception e) {
                retrofit = new Retrofit.Builder()
                        .baseUrl("http://127.0.0.1:8080/")
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }
    }

    public static synchronized GymBuddyApiService getApiService(Context context) {
        ensureRetrofit(context);
        if (apiService == null && retrofit != null) {
            apiService = retrofit.create(GymBuddyApiService.class);
        }
        return apiService;
    }

    public static synchronized CommunityService getCommunityService(Context context) {
        ensureRetrofit(context);
        if (communityService == null && retrofit != null) {
            communityService = retrofit.create(CommunityService.class);
        }
        return communityService;
    }

    public static void resetClient() {
        retrofit = null;
        apiService = null;
        communityService = null;
        currentBaseUrl = "";
    }
}
