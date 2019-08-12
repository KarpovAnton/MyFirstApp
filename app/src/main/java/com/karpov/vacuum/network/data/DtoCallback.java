package com.karpov.vacuum.network.data;

import androidx.annotation.NonNull;

import com.karpov.vacuum.network.data.dto.ResponseDto;

public interface DtoCallback<T extends ResponseDto> {
    void onSuccessful(@NonNull ResponseDto response);
    void onFailed(FailTypes fail);
}
