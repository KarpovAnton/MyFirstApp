package com.socializer.vacuum.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class ImageUtils {
    private static final int MAX_PREVIEW_PIXELS = 150;
    private static final int PREVIEW_JPEG_QUALITY = 70;

    private static final int MAX_IMAGE_WIDTH = 1536;
    private static final double MAX_ASCPECT = 16./9.;

    public class BitmapTransform implements Transformation {

        int maxWidth;
        int maxHeight;

        public BitmapTransform(int maxWidth, int maxHeight) {
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            int targetWidth, targetHeight;
            double aspectRatio;

            if (source.getWidth() > source.getHeight()) {
                targetWidth = maxWidth;
                aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                targetHeight = (int) (targetWidth * aspectRatio);
            } else {
                targetHeight = maxHeight;
                aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                targetWidth = (int) (targetHeight * aspectRatio);
            }

            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return maxWidth + "x" + maxHeight;
        }

    };

    public void setAuthImage(Context context, String token, ImageView target, String imageUrl,
                             String imagePreview, int imageDefault) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", token)
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Picasso picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .build();

        if (!TextUtils.isEmpty(imagePreview)) {
            BitmapTask bitmapTask = new BitmapTask(target);
            bitmapTask.execute(imagePreview);
        }
        if (!TextUtils.isEmpty(imageUrl)) {
            RequestCreator requestCreator = picasso
                    .load(imageUrl)
                    .transform(new BitmapTransform(300, 300))
                    .resize(300, 300)
                    .centerCrop();
            if (TextUtils.isEmpty(imagePreview))
                requestCreator.placeholder(imageDefault);
            requestCreator.into(target);
        } else {
            setImagePreview(target, imagePreview, imageDefault);
        }
    }

    public void setImage(ImageView target, String imageUrl, String imagePreview, int imageDefault) {
        if (!TextUtils.isEmpty(imagePreview)) {
            BitmapTask bitmapTask = new BitmapTask(target);
            bitmapTask.execute(imagePreview);
        }
        if (!TextUtils.isEmpty(imageUrl)) {
            RequestCreator requestCreator = Picasso.get().load(imageUrl);
            if (TextUtils.isEmpty(imagePreview))
                requestCreator.placeholder(imageDefault);
            requestCreator.into(target);
        } else {
            setImagePreview(target, imagePreview, imageDefault);
        }
    }

    public void setImage(ImageView target, String imageUrl, String imagePreview) {
        if (!TextUtils.isEmpty(imagePreview)) {
            BitmapTask bitmapTask = new BitmapTask(target);
            bitmapTask.execute(imagePreview);
        }
        if (!TextUtils.isEmpty(imageUrl))
            Picasso.get().load(imageUrl).into(target);
    }

    public void setImagePreview(ImageView target, String imagePreview, int imageDefault) {
        if (!TextUtils.isEmpty(imagePreview)) {
            BitmapTask bitmapTask = new BitmapTask(target);
            bitmapTask.execute(imagePreview);
        } else {
            target.setImageResource(imageDefault);
        }
    }

    public static String getImagePreview(ImageView source) {
        Drawable d = source.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        Bitmap resizedBitmap = getResizedBitmap(bitmap, MAX_PREVIEW_PIXELS, MAX_PREVIEW_PIXELS, true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, PREVIEW_JPEG_QUALITY, stream);
        byte[] bitmapdata = stream.toByteArray();
        byte[] res = Base64.encode(bitmapdata, Base64.NO_WRAP);
        resizedBitmap.recycle();
        return new String(res);
    }

    public class BitmapTask extends AsyncTask<String, Void, Drawable> {
        ImageView imageView;

        public BitmapTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            try {
                byte[] data = Base64.decode(params[0], Base64.DEFAULT);
                return new BitmapDrawable(BitmapFactory.decodeByteArray(data, 0, data.length));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable image) {
            if (imageView.getDrawable() == null)
                imageView.setImageDrawable(image);
        }
    }

    public static String getRealPathFromURIAndResize(Context context, Uri imageUri) {
        String path = getRealPathFromURI(context, imageUri);
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, o);
        int imageHeight = o.outHeight;
        int imageWidth = o.outWidth;
        double aspect = (double)imageWidth/imageHeight;
        if (aspect < 1.) aspect = 1./aspect;
        if (imageHeight <= MAX_IMAGE_WIDTH && imageWidth <= MAX_IMAGE_WIDTH && aspect < MAX_ASCPECT) {
            return path;
        }

        o.inJustDecodeBounds = false;
        Bitmap b = BitmapFactory.decodeFile(path, o);
        Matrix matrix = new Matrix();
        Bitmap resizedBitmap = null;
        if (aspect > MAX_ASCPECT) {
            if (imageHeight > imageWidth) {
                int newHeight0;
                int width = imageWidth;
                int height = imageHeight;
                newHeight0 = (int)(MAX_ASCPECT * imageWidth);
                if (newHeight0 > MAX_IMAGE_WIDTH) {
                    float scale = ((float) MAX_IMAGE_WIDTH) / newHeight0;
                    matrix.postScale(scale, scale);
                    Bitmap resizedBitmap1 = Bitmap.createBitmap(
                            b, 0, 0, imageWidth, imageHeight, matrix, false);
                    b.recycle();
                    b = resizedBitmap1;
                    width = b.getWidth();
                    height = b.getHeight();
                }

                newHeight0 = (int)(MAX_ASCPECT * width);
                int y = (int)((height-newHeight0)*0.5);
                height -= 2*y;
                resizedBitmap = Bitmap.createBitmap(b, 0, y, width, height);
            } else {
                int newWidth0;
                int width = imageWidth;
                int height = imageHeight;
                newWidth0 = (int)(MAX_ASCPECT * imageHeight);
                if (newWidth0 > MAX_IMAGE_WIDTH) {
                    float scale = ((float) MAX_IMAGE_WIDTH) / newWidth0;
                    matrix.postScale(scale, scale);
                    Bitmap resizedBitmap1 = Bitmap.createBitmap(
                            b, 0, 0, imageWidth, imageHeight, matrix, false);
                    b.recycle();
                    b = resizedBitmap1;
                    width = b.getWidth();
                    height = b.getHeight();
                }

                newWidth0 = (int)(MAX_ASCPECT * height);
                int x = (int)((width-newWidth0)*0.5);
                width -= 2*x;
                resizedBitmap = Bitmap.createBitmap(b, x, 0, width, height);
            }
        } else {
            float scaleWidth = ((float) MAX_IMAGE_WIDTH) / imageWidth;
            float scaleHeight = ((float) MAX_IMAGE_WIDTH) / imageHeight;
            scaleWidth = Math.min(scaleWidth, scaleHeight);
            scaleWidth = Math.min(scaleWidth, 1);
            matrix.postScale(scaleWidth, scaleWidth);
            resizedBitmap = Bitmap.createBitmap(
                    b, 0, 0, imageWidth, imageHeight, matrix, false);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        resizedBitmap.recycle();
        b.recycle();
        try{
            OutputStream outputStream = new FileOutputStream(path);
            stream.writeTo(outputStream);
            stream.writeTo(outputStream);
        } catch (Exception e) {
            Timber.e("Failed write file %s, error: %s", path, e);
        }

        return path;
    }

    public static String getRealPathFromURI(Context context, Uri imageUri) {
        return FileUtils.getPath(context, imageUri);
    }

    private static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight, boolean aspect) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        if (aspect) {
            scaleWidth = Math.min(scaleWidth, scaleHeight);
            scaleHeight = scaleWidth;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        //bm.recycle();
        return resizedBitmap;
    }
}
