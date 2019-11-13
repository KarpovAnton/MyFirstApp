package com.socializer.vacuum.activities.chatlist;

import com.socializer.vacuum.di.base.BasePresenter;
import com.socializer.vacuum.di.base.BaseView;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.views.adapters.PhotoEditAdapter;

import java.util.ArrayList;

public interface ChatListContract {

    interface Router {

        void openAccountActivity();
    }

    interface View extends BaseView<Presenter> {

        void loadChatList();
    }

    interface Presenter extends BasePresenter<View> {


    }
}
