package com.example.lab5;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DrawingCanvas extends View {

    private Paint paint;
    private Path path;
    private List<Path> paths = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();
    private List<Float> startXs = new ArrayList<>();
    private List<Float> startYs = new ArrayList<>();
    private List<Float> endXs = new ArrayList<>();
    private List<Float> endYs = new ArrayList<>();
    private List<Integer> startCircleColors = new ArrayList<>();
    private List<Integer> endCircleColors = new ArrayList<>();
    private int currentColor = Color.WHITE; // def path color
    private int currentCircleColor = Color.WHITE; // def color for circles
    private OnImageSavedListener listener;

    public DrawingCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(6f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        path = new Path();
        paths.add(path);
        colors.add(currentColor);
    }

    public void setColor(int color) {
        currentColor = color;
        currentCircleColor = color;
        paint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < paths.size(); i++) {
            paint.setColor(colors.get(i));
            canvas.drawPath(paths.get(i), paint);
            if (startXs.size() > i) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(startCircleColors.get(i)); // setting color for the start circle
                canvas.drawCircle(startXs.get(i), startYs.get(i), 15, paint);
                paint.setColor(endCircleColors.get(i)); // setting color for the end circle
                canvas.drawCircle(endXs.get(i), endYs.get(i), 15, paint);
                paint.setStyle(Paint.Style.STROKE);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path = new Path();
                paths.add(path);
                colors.add(currentColor);
                path.moveTo(x, y);
                startXs.add(x);
                startYs.add(y);
                endXs.add(x); // initializing with same value, will be updated on ACTION_UP
                endYs.add(y);
                startCircleColors.add(currentCircleColor);
                endCircleColors.add(currentCircleColor);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                endXs.set(endXs.size() - 1, x); // updating with the latest point
                endYs.set(endYs.size() - 1, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                endXs.set(endXs.size() - 1, x); // updating the x end point
                endYs.set(endYs.size() - 1, y);
                endCircleColors.set(endCircleColors.size() - 1, currentCircleColor);
                invalidate();
                break;
        }
        return true;
    }

    public void clearCanvas() {
        paths.clear();
        colors.clear();
        startXs.clear();
        startYs.clear();
        endXs.clear();
        endYs.clear();
        startCircleColors.clear();
        endCircleColors.clear();
        path.reset();
        paths.add(path);
        colors.add(currentColor);
        invalidate();
    }

    public void saveImage() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);

        ContentResolver resolver = getContext().getContentResolver();
        Uri imageCollection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        ContentValues imageDetails = new ContentValues();
        String timeStamp = new SimpleDateFormat(getContext().getString(R.string.yyyymmdd_hhmmss)).format(new Date());
        String fileName = getContext().getString(R.string.img) + timeStamp + getContext().getString(R.string.png);
        imageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 1);
        }

        Uri imageUri = resolver.insert(imageCollection, imageDetails);
        try (OutputStream fos = resolver.openOutputStream(imageUri)) {
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
        } catch (IOException e) {
            Log.e(getContext().getString(R.string.drawingcanvas), getContext().getString(R.string.error_saving_image), e);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear();
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(imageUri, imageDetails, null, null);
        }

        if (listener != null) {
            listener.onImageSaved(imageUri.toString());
        }
    }

    public void setOnImageSavedListener(OnImageSavedListener listener) {
        this.listener = listener;
    }

    public interface OnImageSavedListener {
        void onImageSaved(String imagePath);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(getContext().getString(R.string.superstate), super.onSaveInstanceState());
        bundle.putSerializable(getContext().getString(R.string.paths), new ArrayList<>(paths));
        bundle.putSerializable(getContext().getString(R.string.colors), new ArrayList<>(colors));
        bundle.putSerializable(getContext().getString(R.string.startxs), new ArrayList<>(startXs));
        bundle.putSerializable(getContext().getString(R.string.startys), new ArrayList<>(startYs));
        bundle.putSerializable(getContext().getString(R.string.endxs), new ArrayList<>(endXs));
        bundle.putSerializable(getContext().getString(R.string.endys), new ArrayList<>(endYs));
        bundle.putSerializable(getContext().getString(R.string.startcirclecolors), new ArrayList<>(startCircleColors));
        bundle.putSerializable(getContext().getString(R.string.endcirclecolors), new ArrayList<>(endCircleColors));
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            paths = (List<Path>) bundle.getSerializable(getContext().getString(R.string.paths));
            colors = (List<Integer>) bundle.getSerializable(getContext().getString(R.string.colors));
            startXs = (List<Float>) bundle.getSerializable(getContext().getString(R.string.startxs));
            startYs = (List<Float>) bundle.getSerializable(getContext().getString(R.string.startys));
            endXs = (List<Float>) bundle.getSerializable(getContext().getString(R.string.endxs));
            endYs = (List<Float>) bundle.getSerializable(getContext().getString(R.string.endys));
            startCircleColors = (List<Integer>) bundle.getSerializable(getContext().getString(R.string.startcirclecolors));
            endCircleColors = (List<Integer>) bundle.getSerializable(getContext().getString(R.string.endcirclecolors));
            super.onRestoreInstanceState(bundle.getParcelable(getContext().getString(R.string.superstate)));
        } else {
            super.onRestoreInstanceState(state);
        }
    }
}
