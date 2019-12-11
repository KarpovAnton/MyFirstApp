package com.socializer.vacuum.utils;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import timber.log.Timber;

import static android.os.Looper.getMainLooper;
import static com.socializer.vacuum.network.data.prefs.PrefsModule.NAMED_PREF_UNREAD_MSG;

@Singleton
public class MessageManager{

    @Inject
    @Named(NAMED_PREF_UNREAD_MSG)
    StringPreference unreadMsgSP;

    private ArrayList<NewMsgListener> listeners = new ArrayList<>();

    public void changeState(boolean hasNewMsg) {
        for (NewMsgListener listener : listeners) {
            listener.update(hasNewMsg);
        }
        Timber.d("zxc change state " + hasNewMsg);
    }

    public void subscribe(NewMsgListener listener) {
        listeners.add(listener);
    }

    public void changeIconVisibility(boolean hasNewMsg, ImageView imageView) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                imageView.setVisibility(hasNewMsg ? View.VISIBLE : View.GONE);
            }
        });

        if (unreadMsgSP != null)
            unreadMsgSP.set(String.valueOf(hasNewMsg));

    }

    public interface NewMsgListener {
        void update(boolean hasNewMsg);
    }
}

