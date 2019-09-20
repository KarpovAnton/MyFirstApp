package com.socializer.vacuum.views.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.socializer.vacuum.R;
import com.socializer.vacuum.network.data.dto.socket.ChatMessageInDto;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageInViewHolder extends RecyclerView.ViewHolder {

    Context context;

    @BindView(R.id.messageText)
    TextView messageText;

    @BindView(R.id.avatarImage)
    ImageView avatarImage;

    public MessageInViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = itemView.getContext();
    }

    public void bind(ChatMessageInDto item) {
        messageText.setText(item.getMessage().getText());
    }

    public void setAvatarVisibility(boolean lastMsgSame) {
        if (lastMsgSame) {
            avatarImage.setVisibility(View.VISIBLE);
        } else {
            avatarImage.setVisibility(View.INVISIBLE);
        }
    }
}
