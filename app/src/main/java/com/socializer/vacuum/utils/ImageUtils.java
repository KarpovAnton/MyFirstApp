package com.socializer.vacuum.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import timber.log.Timber;

public class ImageUtils {
    private static final int MAX_PREVIEW_PIXELS = 150;
    private static final int PREVIEW_JPEG_QUALITY = 70;

    private static final int MAX_IMAGE_WIDTH = 1536;
    private static final double MAX_ASCPECT = 16./9.;

    public void setAuthCircleImage(Context context, ImageView target, String imageUrl,
                                   String imagePreview, int imageDefault) {

        if (!TextUtils.isEmpty(imagePreview)) {
            BitmapCircleTask bitmapCircleTask = new BitmapCircleTask(target);
            bitmapCircleTask.execute(imagePreview);
        }
        if (!TextUtils.isEmpty(imageUrl)) {
            RequestOptions options = new RequestOptions();
            options.circleCrop();
            if (TextUtils.isEmpty(imagePreview))
                options.placeholder(imageDefault);
            Glide.with(context)
                    .load(imageUrl)
                    .apply(options)
                    .into(target);
        } else {
            setImagePreview(target, imagePreview, imageDefault);
        }
    }

    public void setAuthImage(Context context, ImageView target, String imageUrl,
                             String imagePreview, int imageDefault) {

        if (!TextUtils.isEmpty(imagePreview)) {
            BitmapTask bitmapTask = new BitmapTask(target);
            bitmapTask.execute(imagePreview);
        }
        if (!TextUtils.isEmpty(imageUrl)) {
            RequestOptions options = new RequestOptions();
            options.centerCrop();
            if (TextUtils.isEmpty(imagePreview))
                options.placeholder(imageDefault);
            Glide.with(context)
                    .load(imageUrl)
                    .apply(options)
                    .into(target);
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
    
    private class BitmapTask extends AsyncTask<String, Void, Drawable> {
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

    private class BitmapCircleTask extends AsyncTask<String, Void, Drawable> {
        ImageView imageView;

        public BitmapCircleTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            try {
                byte[] data = Base64.decode(params[0], Base64.DEFAULT);
                Drawable result = new BitmapDrawable(BitmapFactory.decodeByteArray(data, 0, data.length));
                Bitmap resultBitmap = drawableToBitmap(result);
                resultBitmap = getRoundedCroppedBitmap(resultBitmap);
                return new BitmapDrawable(resultBitmap);
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

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }



    private Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {
        int widthLight = bitmap.getWidth();
        int heightLight = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);

        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));

        canvas.drawRoundRect(rectF, widthLight / 2 ,heightLight / 2,paintColor);

        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, 0, 0, paintImage);

        return output;
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
