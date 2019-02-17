package com.coderzgonwild.admin.linguisto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    Vision vision;
    private ImageView imageView;
  //  private Vision vision;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        Button btnCamera = (Button)findViewById(R.id.btnCamera);
        imageView = (ImageView)findViewById(R.id.imageView);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        setUpVision();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);
    }

    protected void setUpVision(){
        //sets up the cloud api builder


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
                    InputStream inputStream = getResources().openRawResource(R.raw.pen);
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
                    for(int i = 0; i < numberOfLabels; i++){
                        labelsObtained += "\n " + labels.get(i) + " ";
                    }

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
