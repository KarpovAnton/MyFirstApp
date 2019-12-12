package com.socializer.vacuum.utils;

import java.util.ArrayList;

import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class MessageManager{

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

    public interface NewMsgListener {
        void update(boolean hasNewMsg);
    }
}

