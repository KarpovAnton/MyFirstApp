package com.socializer.vacuum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.multidex.MultiDex;

import com.socializer.vacuum.di.component.AppComponent;
import com.socializer.vacuum.di.component.DaggerAppComponent;
import com.socializer.vacuum.network.data.prefs.AuthSession;
import com.socializer.vacuum.utils.Consts;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import java.io.IOException;
import java.net.URISyntaxException;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class VacuumApplication extends DaggerApplication implements Application.ActivityLifecycleCallbacks {

    @SuppressLint("StaticFieldLeak")
    private static volatile VacuumApplication instance;
    private static AppComponent component;

    Activity currentActivity;
    public static volatile Context applicationContext;
    Socket mSocket;

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {//TODO ADD
                // VKAccessToken is invalid
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        applicationContext = getApplicationContext();
        Timber.plant(new Timber.DebugTree());
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(applicationContext);
    }

    public void initSocket() {
        if (mSocket != null) return;
        try {
            AuthSession as = AuthSession.getInstance();
            if (as == null) return;

            IO.Options options = new IO.Options();
            options.query = "token=" + as.getToken();
            mSocket = IO.socket(Consts.CHAT_SERVER_URL, options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Picasso getPicasso(String token) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", token)
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Picasso picasso = new Picasso.Builder(applicationContext)
                .indicatorsEnabled(true)
                .downloader(new OkHttp3Downloader(client))
                .build();
        //Picasso.setSingletonInstance(picasso);
        return picasso;
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        component = DaggerAppComponent.builder().application(this).build();
        return component;
    }

    public static VacuumApplication getInstance() {
        VacuumApplication localInstance = instance;
        if (localInstance == null) {
            synchronized (VacuumApplication.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new VacuumApplication();
                }
            }
        }
        return localInstance;
    }

    public static AppComponent getComponent() {
        return component;
    }

    public Socket getSocket() {
        return mSocket;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        currentActivity = activity;
        Timber.d( "lfc onCreate %s", activity.getClass().getSimpleName());
    }
    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
        Timber.d( "lfc onStarted %s", activity.getClass().getSimpleName());
    }
    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
        Timber.d( "lfc Resumed %s", activity.getClass().getSimpleName());
    }
    @Override
    public void onActivityPaused(Activity activity) {
        Timber.d( "lfc onPause %s", activity.getClass().getSimpleName());
    }
    @Override
    public void onActivityStopped(Activity activity) {
        Timber.d( "lfc onStop %s", activity.getClass().getSimpleName());
    }
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        Timber.d( "lfc onSaveInstanceState %s", activity.getClass().getSimpleName());
    }
    @Override
    public void onActivityDestroyed(Activity activity) {
        Timber.d( "lfc onStop %s", activity.getClass().getSimpleName());
    }
}
