package com.example.lab5;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class FragmentList extends Fragment {

    private ListView listView;
    private ArrayList<String> imageList;
    private ListAdapter listAdapter;
    private OnImageSelectedListener listener;

    private static final String IMAGE_LIST_KEY = "imageList";

    public interface OnImageSelectedListener {
        void onImageSelected(String imagePath);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnImageSelectedListener) {
            listener = (OnImageSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString() + getString(R.string.must_implement_onimageselectedlistener));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        listView = view.findViewById(R.id.listView);
        if (savedInstanceState != null) {
            imageList = savedInstanceState.getStringArrayList(IMAGE_LIST_KEY);
        } else {
            imageList = new ArrayList<>();
        }
        listAdapter = new ListAdapter(getContext(), imageList);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedImage = imageList.get(position);
                listener.onImageSelected(selectedImage);
            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(IMAGE_LIST_KEY, imageList);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            imageList = savedInstanceState.getStringArrayList(IMAGE_LIST_KEY);
            listAdapter.notifyDataSetChanged();
        }
    }

    // adding a new image to the list
    public void addImage(String imagePath) {
        Uri imageUri = Uri.parse(imagePath);
        imageList.add(imageUri.toString());
        listAdapter.notifyDataSetChanged();
    }
}
