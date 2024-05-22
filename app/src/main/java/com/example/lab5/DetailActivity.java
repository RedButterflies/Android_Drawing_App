package com.example.lab5;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String imagePath = getIntent().getStringExtra(getString(R.string.imagepath));

        FragmentDetails fragment = new FragmentDetails();
        Bundle args = new Bundle();
        args.putString(getString(R.string.imagepath), imagePath);
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detailFragmentContainer, fragment)
                .commit();
    }

}
