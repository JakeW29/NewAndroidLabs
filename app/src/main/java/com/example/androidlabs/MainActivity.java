package com.example.androidlabs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

        new DogImages().execute();
    }

    private class DogImages extends AsyncTask<Void, Integer, Bitmap> {
        private Bitmap dogImage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                while (true) {
                    URL url = new URL("https://dog.ceo/api/breeds/image/random");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    StringBuilder json = new StringBuilder();
                    int byteCharacter;
                    while ((byteCharacter = inputStream.read()) != -1) {
                        json.append((char) byteCharacter);
                    }

                    JSONObject jsonObject = new JSONObject(json.toString());
                    String imageUrl = jsonObject.getString("message");

                    // Generate unique file name
                    String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

                    // Check if image exists
                    File file = new File(getCacheDir(), fileName);
                    if (file.exists()) {
                        dogImage = BitmapFactory.decodeFile(file.getPath());
                    } else {

                        // Download the image
                        URL imageDownloadUrl = new URL(imageUrl);
                        HttpURLConnection imageConnection = (HttpURLConnection) imageDownloadUrl.openConnection();
                        InputStream imageInputStream = imageConnection.getInputStream();
                        dogImage = BitmapFactory.decodeStream(imageInputStream);

                        // Save the downloaded image
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        dogImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        fileOutputStream.close();
                    }

                    publishProgress(0);
                    for (int i = 0; i < 100; i++) {
                        publishProgress(i);
                        Thread.sleep(30);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error downloading dog image", e);
            }
            return dogImage;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d(TAG, "Progress: " + values[0]);
            // If progress is at the start, update the ImageView with the new image
            if (values[0] == 0) {
                runOnUiThread(() -> {
                    imageView.setImageBitmap(dogImage);
                });
            }
            // Update the ProgressBar with current progress
            runOnUiThread(() -> progressBar.setProgress(values[0]));
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                Log.e(TAG, "Failed to download dog image");
            }
            runOnUiThread(() -> progressBar.setVisibility(View.INVISIBLE));
        }
    }
}
