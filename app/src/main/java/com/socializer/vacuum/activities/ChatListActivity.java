package com.socializer.vacuum.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.socializer.vacuum.R;
import com.socializer.vacuum.models.chat.Dialog;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

public class ChatListActivity extends DaggerAppCompatActivity {

    @BindView(R.id.dialogsList)
    DialogsList dialogsList;

    DialogsListAdapter<Dialog> dialogsListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
/*        dialogsListAdapter = new DialogsListAdapter<>(dialogs, new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                //If you using another library - write here your way to load image

            }
        });*/

        dialogsList.setAdapter(dialogsListAdapter);
    }
}
