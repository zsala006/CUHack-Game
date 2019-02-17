package com.coderzgonwild.admin.linguisto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import android.widget.Toast;


import com.google.api.client.http.javanet.NetHttpTransport; //added Net to the HttpTransport

import com.google.api.client.json.JsonFactory; //added Andriod
//import com.google.api.client.json.gson.GsonFactory;

import android.net.Uri;
import android.os.AsyncTask;
import java.util.Arrays;

public class Translate extends AppCompatActivity {


    private ImageView imageView;

    private String photoFilename = "item.png";
    private File photoFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        Button btnCamera = (Button)findViewById(R.id.btnCamera);
        imageView = (ImageView)findViewById(R.id.imageView);

        File folder = new File("/sdcard/linguistopics/");
        folder.mkdirs();

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
//                imagesFolder.mkdirs();
//                File image = new File(imagesFolder, "item.jpg");
//                Uri uriSavedImage = Uri.fromFile(image);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

                final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
                root.mkdirs();
                final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
                final File sdImageMainDirectory = new File(root, fname);
                Uri mImageUri = Uri.fromFile(sdImageMainDirectory);

                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(intent, 0);


                setUpVision(mImageUri);

            }



        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);


    }


    protected void setUpVision(Uri uri){
        //sets up the cloud api builder
        final Uri uriNew = uri;

        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new AndroidJsonFactory();

        Vision.Builder visionBuilder = new Vision.Builder(
                httpTransport,
                jsonFactory,
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("AIzaSyCS_TW8v8UifqjE6bhFnQyRS1FO2sRPb9o"));

        //builds the vision class
        final Vision vision = visionBuilder.build();



    //}

   // private void processImage(Uri uri){


        AsyncTask.execute(new Runnable(){
            public void run() {

                try {

                    InputStream inputStream = getContentResolver().openInputStream(uriNew);
                   // InputStream inputStream = getResources().openRawResource(R.raw.pen);
                    byte[] photoData = IOUtils.toByteArray(inputStream);
                    inputStream.close();

                    Image inputImage = new Image();
                    inputImage.encodeContent(photoData);

                    Feature desiredFeature = new Feature();
                    desiredFeature.setType("LABEL_DETECTION");

                    List<Feature> features = new ArrayList<>();
                    features.add(desiredFeature);

                    List<AnnotateImageRequest> imageList = new ArrayList<>();
                    AnnotateImageRequest request = new AnnotateImageRequest();
                    request.setImage(inputImage);
                    request.setFeatures(features);

                    imageList.add(request);

                    BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
                    batchRequest.setRequests(imageList);

                    Vision.Images.Annotate annotateRequest;
                    annotateRequest = vision.images().annotate(batchRequest);
                    BatchAnnotateImagesResponse batchResponse = annotateRequest.execute();


                    List<EntityAnnotation> labels = batchResponse.getResponses().get(0).getLabelAnnotations();
                   // return batchResponse;

                    int numberOfLabels = labels.size();
                    String labelsObtained = "";
                   // for(int i = 0; i < numberOfLabels; i++){
                        labelsObtained += "\n " + labels.get(0) + " ";
                  //  }

                    final String message =  new String(labelsObtained);

                    runOnUiThread(new Runnable(){
                        public void run() {
                          Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                        }
                    });


                }catch(IOException e){

                }

               // return null;

            }
        });
    }

   // private void

    /*
    public static void main( String args[]){


        setUpVision();
    }
    */
    /*
    private void callCloudVision(final Bitmap bitmap){


        try{
            AsyncTask<Object,Void, String> labelDetectionTask = new Lable
        }


    }
    */

}
