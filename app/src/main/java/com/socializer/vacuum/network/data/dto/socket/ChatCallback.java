package com.socializer.vacuum.network.data.dto.socket;

import androidx.annotation.NonNull;

import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.dto.ResponseDto;

import java.util.List;

public interface ChatCallback<T extends ResponseDto> {
    void onSuccessful(@NonNull List<LastMessagesResponseDto> response);
    void onFailed(FailTypes fail);
}
