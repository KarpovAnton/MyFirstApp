package com.karpov.vacuum.network.data.dto;

import java.util.ArrayList;

public class PhotoDeleteRequestDto {

    private ArrayList<PhotoDeleteRequestInnerDto> images = new ArrayList<>();

    public PhotoDeleteRequestDto(String url) {
        images.add(new PhotoDeleteRequestInnerDto(1, url, 1));
    }

    private class PhotoDeleteRequestInnerDto {

        public PhotoDeleteRequestInnerDto(int kind, String url, int action) {
            this.kind = kind;
            this.url = url;
            this.action = action;
        }

        private int kind;

        private String url;

        private int action;
    }
}
