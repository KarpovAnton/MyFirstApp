package com.karpov.vacuum.network.data;

import androidx.annotation.NonNull;

import com.karpov.vacuum.network.data.dto.ProfilePreviewDto;
import com.karpov.vacuum.network.data.dto.ResponseDto;

import java.util.List;

public interface DtoListCallback<T extends ResponseDto> {
    void onSuccessful(@NonNull List<ProfilePreviewDto> response);
    void onFailed(FailTypes fail);
}
