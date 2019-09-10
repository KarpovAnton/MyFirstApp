package com.socializer.vacuum.network.data.dto;

import java.util.ArrayList;

public class PhotoRequestDto {

    private ArrayList<PhotoRequestInnerDto> images = new ArrayList<>();

    public PhotoRequestDto(String data) {
        images.add(new PhotoRequestInnerDto(1, data, "jpg"));
    }

    private class PhotoRequestInnerDto {

        public PhotoRequestInnerDto(int kind, String data, String ext) {
            this.kind = kind;
            this.data = data;
            this.ext = ext;
        }

        private int kind;

        private String data;

        private String ext;
    }
}
