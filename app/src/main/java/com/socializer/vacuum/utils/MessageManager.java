package com.socializer.vacuum.utils;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import timber.log.Timber;

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
        Timber.d("moe change state " + hasNewMsg);
    }

    public void subscribe(NewMsgListener listener) {
        listeners.add(listener);
    }

    public interface NewMsgListener {
        void update(boolean hasNewMsg);
    }
}