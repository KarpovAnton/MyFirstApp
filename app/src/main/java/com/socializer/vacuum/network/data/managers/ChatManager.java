package com.socializer.vacuum.network.data.managers;

import android.app.Application;

import androidx.annotation.NonNull;

import com.socializer.vacuum.network.VacuumApi;
import com.socializer.vacuum.network.data.AbstractManager;
import com.socializer.vacuum.network.data.FailTypes;
import com.socializer.vacuum.network.data.dto.ResponseDto;
import com.socializer.vacuum.network.data.dto.socket.ChatCallback;
import com.socializer.vacuum.network.data.dto.socket.DialogsResponseDto;
import com.socializer.vacuum.network.data.dto.socket.LastMessagesResponseDto;
import com.socializer.vacuum.utils.ErrorUtils;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.socializer.vacuum.utils.Consts.CHAT_ID_URL;
import static com.socializer.vacuum.utils.Consts.CHAT_LIST_URL;
import static com.socializer.vacuum.utils.Consts.TOKEN_NOT_FOUND;
import static com.socializer.vacuum.utils.Consts.UNAUTHORIZED;

public class ChatManager extends AbstractManager {

    @Inject
    ChatManager(VacuumApi vacuumApi, ErrorUtils errorUtils, Application application) {
        super(vacuumApi, errorUtils, application);
    }

    public interface ChatListCallback<T extends ResponseDto> {
        void onSuccessful(@NonNull List<DialogsResponseDto> response);
        void onFailed(FailTypes fail);
    }

    public void getLastMsgs(@NonNull String chatId, @NonNull final ChatCallback<?> callback) {

        if (!checkNetworkAvailable(callback)) return;

        Call<List<LastMessagesResponseDto>> call = mVacuumApi.getLastMsgs(CHAT_ID_URL + chatId, getTokenString());
        call.enqueue(new Callback<List<LastMessagesResponseDto>>() {
            @Override
            public void onResponse(Call<List<LastMessagesResponseDto>> call, Response<List<LastMessagesResponseDto>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccessful(response.body());
                } else {
                    if (response.code() == TOKEN_NOT_FOUND || response.code() == UNAUTHORIZED)
                        callback.onFailed(FailTypes.AUTH_REQUIRED);
                }
            }

            @Override
            public void onFailure(Call<List<LastMessagesResponseDto>> call, Throwable t) {
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
            }
        });
    }

    public void getChatList(@NonNull final ChatListCallback<?> callback) {

        if (!checkNetworkAvailable(callback)) return;

        Call<List<DialogsResponseDto>> call = mVacuumApi.getChatList(CHAT_LIST_URL, getTokenString());
        call.enqueue(new Callback<List<DialogsResponseDto>>() {
            @Override
            public void onResponse(Call<List<DialogsResponseDto>> call, Response<List<DialogsResponseDto>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccessful(response.body());
                } else {
                    if (response.code() == TOKEN_NOT_FOUND || response.code() == UNAUTHORIZED)
                        callback.onFailed(FailTypes.AUTH_REQUIRED);
                }
            }

            @Override
            public void onFailure(Call<List<DialogsResponseDto>> call, Throwable t) {
                callback.onFailed(FailTypes.UNKNOWN_ERROR);
            }
        });
    }
}
