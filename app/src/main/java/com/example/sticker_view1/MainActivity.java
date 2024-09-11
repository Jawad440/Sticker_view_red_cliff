package com.example.sticker_view1;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.stickerview.DrawableSticker;
import com.example.stickerview.Sticker;
import com.example.stickerview.StickerView;
import com.example.stickerview.TextSticker;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = MainActivity.class.getSimpleName();
  public static final int PERM_RQST_CODE = 110;
  private static final int STORAGE_PERMISSION_CODE = 101;
  private StickerView stickerView;
  private Uri saveImage;
  private TextSticker sticker;
  ActivityResultLauncher<Intent> resultLauncher;
//  PhotoView photoView;
  ImageView imageView1, getImageView_save;

  ImageView pickImageBtn, picStickerBtn;

  private StickerManager stickerManager;
Toolbar toolbar;
Bitmap bitmap;
BitmapDrawable bitmapDrawable;
  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }

    //assign id to objects
    stickerView = (StickerView) findViewById(R.id.sticker_view);
    imageView1 = findViewById(R.id.imageview1);
    pickImageBtn = findViewById(R.id.pickImageBtn);
    picStickerBtn = findViewById(R.id.pickStickerBtn);
    toolbar = findViewById(R.id.app_bar);
    getImageView_save = findViewById(R.id.imageView_save);


    //OnclickListner
    getImageView_save.setOnClickListener(v -> {
        saveImageToGallery();
    });

    pickImageBtn.setOnClickListener(v -> {
      if (checkPermission()) {
        // If permission is already granted, pick the image
        pickImage();
      } else {
        // Request storage permission
        requestStoragePermission();
      }
    });

    picStickerBtn.setOnClickListener(v -> {
      showStickerPopup();
    });



    resultRegister();

    //Call sticker manager Activity
    stickerManager = new StickerManager(stickerView);

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
              new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERM_RQST_CODE);
    } else {
      requestStoragePermission();
    }
  }
  private boolean checkPermission() {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
  }
  private void requestStoragePermission() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
      Toast.makeText(this, "Storage permission is required to pick an image.", Toast.LENGTH_LONG).show();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
      {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_IMAGES }, STORAGE_PERMISSION_CODE);
      }else {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
      }
    }

  }

  //save Image to Galery
  private void saveImageToGallery() {
    // Create a Bitmap with the dimensions of the StickerView
    Bitmap resultBitmap = Bitmap.createBitmap(stickerView.getWidth(), stickerView.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(resultBitmap);

    stickerView.draw(canvas);
    // Save the bitmap to storage
    saveBitmap(resultBitmap);
    launchStickerActivity(resultBitmap);
  }

  //Save Bitmap
  private void saveBitmap(Bitmap bitmap) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      // For Android 10 and above
      ContentResolver resolver = getContentResolver();
      ContentValues contentValues = new ContentValues();
      contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "StickerImage_" + System.currentTimeMillis() + ".jpg");
      contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
      contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

      Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

      try {
        OutputStream outputStream = resolver.openOutputStream(imageUri);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.close();
        Toast.makeText(this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();
      } catch (IOException e) {
        e.printStackTrace();
        Toast.makeText(this, "Failed to Save Image", Toast.LENGTH_SHORT).show();
      }
    } else {
      // For devices below Android 10
      String imagePath = Environment.getExternalStorageDirectory().toString();
      File imageFile = new File(imagePath, "StickerImage_" + System.currentTimeMillis() + ".jpg");

      try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        Toast.makeText(this, "Image Saved to Gallery", Toast.LENGTH_SHORT).show();
      } catch (IOException e) {
        e.printStackTrace();
        Toast.makeText(this, "Failed to Save Image", Toast.LENGTH_SHORT).show();
      }

      // Notify the gallery app to scan the new file
      Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
      Uri contentUri = Uri.fromFile(imageFile);
      this.sendBroadcast(mediaScanIntent);

    }

    // Clear stickers and reset image view
    stickerView.removeAllStickers();
    imageView1.setImageURI(null);
  }

  private void showStickerPopup() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    LayoutInflater inflater = getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.sticker_popup_layout, null);
    builder.setView(dialogView);

    GridView stickerGrid = dialogView.findViewById(R.id.sticker_grid);
    //drawable array
    Integer[] stickerImages =
            {       R.drawable.a01,
                    R.drawable.a01,
                    R.drawable.a02,
                    R.drawable.a03,
                    R.drawable.a04,
                    R.drawable.a05,
                    R.drawable.a06,
                    R.drawable.a07,
                    R.drawable.a08,
                    R.drawable.a09,
                    R.drawable.a11,
                    R.drawable.a12,
                    R.drawable.a13,
                    R.drawable.a15,
                    R.drawable.a16,
                    R.drawable.a17,
                    R.drawable.a18,
                    R.drawable.a19,
                    R.drawable.a20,
                    R.drawable.a21,
                    R.drawable.a22,
             };
    StickerAdapter stickerAdapter = new StickerAdapter(this, stickerImages);
    stickerGrid.setBackgroundColor(Color.TRANSPARENT);

    stickerGrid.setAdapter(stickerAdapter);
    AlertDialog dialog = builder.create();
 // sticker add onclicklistner
    stickerGrid.setOnItemClickListener((parent, view, position, id) -> {
      Drawable stickerDrawable = getResources().getDrawable(stickerImages[position], null);

      if (stickerDrawable != null) {
       //DrawableSticker drawableSticker = new DrawableSticker(stickerDrawable);
        stickerView.addSticker(new DrawableSticker(stickerDrawable), Sticker.Position.CENTER);
      }
      else {
        Toast.makeText(this, "Sticker Not loaded Successfully", Toast.LENGTH_SHORT).show();
      }
      // Dismiss the dialog after sticker selection
      dialog.dismiss();
    });

    dialog.show();
  }

  //  private void loadSticker() {
//    Drawable drawable =
//            ContextCompat.getDrawable(this, R.drawable.haizewang_215);
//    Drawable drawable1 =
//            ContextCompat.getDrawable(this, R.drawable.haizewang_23);
//    stickerView.addSticker(new DrawableSticker(drawable));
//    stickerView.addSticker(new DrawableSticker(drawable1), Sticker.Position.BOTTOM | Sticker.Position.RIGHT);
//
//    Drawable bubble = ContextCompat.getDrawable(this, R.drawable.bubble);
//    stickerView.addSticker(
//            new TextSticker(getApplicationContext())
//                    .setDrawable(bubble)
//                    .setText("Sticker\n")
//                    .setMaxTextSize(14)
//                    .resizeText()
//            , Sticker.Position.TOP);
//  }
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == STORAGE_PERMISSION_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        pickImage();
      } else {
        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
      }
    }
  }

  //pick image from galley
  private void pickImage() {
    CropImage.activity().start(MainActivity.this);
//    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//    resultLauncher.launch(intent);
  }
   //image loaded to image veiew
  private void resultRegister() {
    resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
              @Override
              public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                  Uri imageUri = result.getData().getData();
                  //imageView.setImageURI(imageUri);
                  imageView1.setImageURI(imageUri);
                } else {
                  Toast.makeText(MainActivity.this, "Image not selected", Toast.LENGTH_SHORT).show();
                }
              }
            }
    );
  }

  //launch StickerActivity
  private void launchStickerActivity(Bitmap bitmap) {
    // Convert Bitmap to ByteArray
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    byte[] byteArray = stream.toByteArray();

    // Start StickerActivity and pass the ByteArray
    Intent intent = new Intent(MainActivity.this, StickerActivity.class);
    intent.putExtra("imageBitmap", byteArray);
    startActivity(intent);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
      CropImage.ActivityResult result = CropImage.getActivityResult(data);
      if (resultCode == RESULT_OK) {
        Uri resultUri = result.getUri();
        saveImage = resultUri;
        // Use Picasso to load the cropped image into the ImageView
        Picasso.with(this).load(saveImage).into(imageView1);
      } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
        Exception error = result.getError();
        Toast.makeText(this, "Cropping failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
      }
    }
  }

}
