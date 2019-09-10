package com.socializer.vacuum.network.data;

import androidx.annotation.NonNull;

import com.socializer.vacuum.network.data.dto.ProfilePreviewDto;
import com.socializer.vacuum.network.data.dto.ResponseDto;

import java.util.List;

public interface DtoListCallback<T extends ResponseDto> {
    void onSuccessful(@NonNull List<ProfilePreviewDto> response);
    void onFailed(FailTypes fail);
}
