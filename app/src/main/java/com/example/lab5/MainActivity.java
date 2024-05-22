package com.example.lab5;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements FragmentList.OnImageSelectedListener, DrawingCanvas.OnImageSavedListener {

    private static final int REQUEST_CODE = 100;
    private static final int READ_CODE = 100;
    private static final int WRITE_CODE = 101;

    private DrawingCanvas drawingCanvas;
    private Button colorButton1, colorButton2, colorButton3, colorButton4, clearButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }

        drawingCanvas = findViewById(R.id.drawingCanvas);
        colorButton1 = findViewById(R.id.colorButton1);
        colorButton2 = findViewById(R.id.colorButton2);
        colorButton3 = findViewById(R.id.colorButton3);
        colorButton4 = findViewById(R.id.colorButton4);
        clearButton = findViewById(R.id.clearButton);
        saveButton = findViewById(R.id.saveButton);

        drawingCanvas.setOnImageSavedListener(this);

        // restore state
        if (savedInstanceState != null) {
            drawingCanvas.onRestoreInstanceState(savedInstanceState.getParcelable(getString(R.string.drawingcanvasstate)));
        }

        // initial color
        //drawingCanvas.setColor(Color.rgb(92, 18, 18));

        // color button listeners
        colorButton1.setOnClickListener(v -> drawingCanvas.setColor(Color.RED));
        colorButton2.setOnClickListener(v -> drawingCanvas.setColor(Color.rgb(230, 184, 23)));
        colorButton3.setOnClickListener(v -> drawingCanvas.setColor(Color.rgb(0,204,245)));
        colorButton4.setOnClickListener(v -> drawingCanvas.setColor(Color.GREEN));

        // clear button listener
        clearButton.setOnClickListener(v -> drawingCanvas.clearCanvas());

        // save button listener
        //saveButton.setOnClickListener(v -> {
           // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
               // if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                 //   drawingCanvas.saveImage();
               // } else {
                //    Toast.makeText(this, getString(R.string.permission_denied_to_save_image), Toast.LENGTH_SHORT).show();
               // }
           // } else {
                //drawingCanvas.saveImage();
          //  }
        //});
        saveButton.setOnClickListener(v -> saveImagePermissions());

    }

    // Method to handle image selection from FragmentList
    @Override
    public void onImageSelected(String imagePath) {
        FragmentDetails fragment = (FragmentDetails) getSupportFragmentManager().findFragmentById(R.id.fragmentDetails);
        if (fragment != null && fragment.isInLayout()) {
            fragment.displayImage(imagePath);
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(getString(R.string.imagepath), imagePath);
            startActivity(intent);
        }
    }

    // Add options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Handle menu item selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    drawingCanvas.saveImage();
                } else {
                    Toast.makeText(this, getString(R.string.permission_denied_to_save_image), Toast.LENGTH_SHORT).show();
                }
            } else {
                drawingCanvas.saveImage();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // handling image save callback
    @Override
    public void onImageSaved(String imagePath) {
        Uri imageUri = Uri.parse(imagePath);
        FragmentList fragmentList = (FragmentList) getSupportFragmentManager().findFragmentById(R.id.fragmentList);
        if (fragmentList != null) {
            fragmentList.addImage(imageUri.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getString(R.string.drawingcanvasstate), drawingCanvas.onSaveInstanceState());
    }

    private void saveImagePermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawingCanvas.saveImage();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.permission_granted_to_save_image), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.permission_denied_to_save_image), Toast.LENGTH_SHORT).show();
            }
        }
    }



}
