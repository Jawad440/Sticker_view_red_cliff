package com.example.sticker_view1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StickerActivity extends AppCompatActivity {
    ImageView imageViewSticker, imageViewRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        imageViewSticker = findViewById(R.id.sticker_activity_imageView);
        imageViewRefresh = findViewById(R.id.imageView_refresh);

        imageViewRefresh.setOnClickListener(v -> {
            imageViewSticker.setImageBitmap(null);
        });

        // Retrieve the byte array passed from MainActivity
        byte[] byteArray = getIntent().getByteArrayExtra("imageBitmap");
        if (byteArray != null) {
            // Convert byte array back to Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            loadImage(bitmap);
        } else {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImage(Bitmap bitmap) {
        try {
            imageViewSticker.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }
}
