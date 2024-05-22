package com.example.lab5;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentDetails extends Fragment {

    private ImageView imageView;
    private String imagePath;
    private static final String IMAGE_PATH_KEY = "imagePath";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        imageView = view.findViewById(R.id.imageView);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(IMAGE_PATH_KEY, imagePath);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            imagePath = savedInstanceState.getString(IMAGE_PATH_KEY);
            displayImage(imagePath);
        }
    }

    public void displayImage(String imagePath) {
        this.imagePath = imagePath;
        Uri imageUri = Uri.parse(imagePath);
        imageView.setImageURI(imageUri);
    }
}
