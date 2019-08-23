package com.karpov.vacuum.network.data.dto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ProfileListResponseDto extends ResponseDto {

    @NonNull
    List<ProfilePreviewDto> result;

    @Nullable
    String next;

    @NonNull
    public List<ProfilePreviewDto> getResult() {
        return result;
    }

    @Nullable
    public String getNext() {
        return next;
    }
}
