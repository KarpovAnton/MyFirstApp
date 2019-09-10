package com.socializer.vacuum.utils;

import com.socializer.vacuum.network.data.dto.ApiError;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

@Singleton
public class ErrorUtils {

    private final Retrofit mRetrofit;

    @Inject
    public ErrorUtils(Retrofit retrofit) {
        mRetrofit = retrofit;
    }

    public ApiError parseError(Response response) {
        Converter<ResponseBody, ApiError> converter =
                mRetrofit.responseBodyConverter(ApiError.class, new Annotation[0]);
        ApiError error;

        try {
            ResponseBody responseBody = response.errorBody();
            if (responseBody != null) {
                error = converter.convert(responseBody);
            } else {
                error = new ApiError(ApiError.PARSE_ERROR, "");
            }
        } catch (IOException e) {
            return new ApiError(ApiError.UNDEFINED, e.getMessage());
        }

        return error;
    }
}
