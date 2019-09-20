package com.socializer.vacuum.models.chat;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;

public class Message implements IMessage/*, IUser*/, MessageContentType.Image {

    private String id;
    private String text;
    private Date createdAt;
    private MessageAuthor messageAuthor;
    private Image image;

    public Message(String id, MessageAuthor messageAuthor, String text) {
        this(id, messageAuthor, text, new Date());
    }

    public Message(String id, MessageAuthor messageAuthor, String text, Date createdAt) {
        this.id = id;
        this.text = text;
        this.messageAuthor = messageAuthor;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public MessageAuthor getUser() {
        return this.messageAuthor;
    }

    @Override
    public String getImageUrl() {
        return image == null ? null : image.url;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public static class Image {

        private String url;

        public Image(String url) {
            this.url = url;
        }
    }

/*    @Override
    public String getName() {
        return messageAuthor.getName();
    }

    @Override
    public String getAvatar() {
        return null;
    }*/
}
