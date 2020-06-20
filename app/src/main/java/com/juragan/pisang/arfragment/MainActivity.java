package com.juragan.pisang.arfragment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    ArFragment arFragment;
    private ModelRenderable bearRenderable,
            catRenderable,
            cowRenderable,
            dogRenderable,
            elephantRenderable,
            ferretRenderable,
            hippoRenderable,
            horseRenderable,
            koalaRenderable,
            lionRenderable,
            reindeerRenderable,
            wolverineRenderable;

    ImageView bear, cat, cow, dog, elephant, ferret, hippo, horse, koala, lion, reindeer, wolverine;

    View arrayView[];

    int selected = 1;


    //LIGHT
    SensorManager sensorManager;
    Sensor sensor;

    private CameraManager cameraManager;
    private String cameraId;
    private Boolean isTorchOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_ux_fragment);

        //view
        bear = findViewById(R.id.bear);
        cat = findViewById(R.id.cat);
        cow = findViewById(R.id.cow);
        dog = findViewById(R.id.dog);
        elephant = findViewById(R.id.elephant);
        ferret = findViewById(R.id.ferret);
        hippo = findViewById(R.id.hippo);
        horse = findViewById(R.id.horse);
        koala = findViewById(R.id.koala);
        lion = findViewById(R.id.lion);
        reindeer = findViewById(R.id.reindeer);
        wolverine = findViewById(R.id.wolverine);

        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        isTorchOn = false;
        Boolean isFlashAvailable = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

//        if (!isFlashAvailable) {
//            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
//                    .create();
//            alert.setTitle("Error !!");
//            alert.setMessage("Device anda tidak mendukung flash light!");
//            alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    // closing the application
////                    finish();
////                    System.exit(0);
//                }
//            });
//            alert.show();
//            return;
//        }

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                cameraId = cameraManager.getCameraIdList()[0];

            }catch(Exception e){
                e.printStackTrace();
            }
        }

        setArrayView();
        setOnclickListener();

        setupModel();

        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                //when user tap on plane, we will add model

                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());

                createModel(anchorNode, selected);

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if(isTorchOn){
            turnOffFlashLight();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(isTorchOn){
            turnOffFlashLight();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        if(isTorchOn){
            turnOnFlashLight();
        }
    }

    public void turnOnFlashLight() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turnOffFlashLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT){
            if(sensorEvent.values[0] < 100) {
                turnOnFlashLight();
                isTorchOn = true;
            }else{
                turnOffFlashLight();
                isTorchOn = false;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    //INI AR
    private void setupModel() {
        ModelRenderable.builder()
                .setSource(this, R.raw.bear)
                .build().thenAccept(renderable -> bearRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Tidak dapat memproses model Beruang", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        }
                );

        ModelRenderable.builder()
                .setSource(this, R.raw.cat)
                .build().thenAccept(renderable -> catRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Tidak dapat memproses model Kucing", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        }
                );


        ModelRenderable.builder()
                .setSource(this, R.raw.cat)
                .build().thenAccept(renderable -> catRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Tidak dapat memproses model Kucing", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        }
                );


        ModelRenderable.builder()
                .setSource(this, R.raw.cow)
                .build().thenAccept(renderable -> cowRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Tidak dapat memproses model Sapi", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        }
                );


        ModelRenderable.builder()
                .setSource(this, R.raw.dog)
                .build().thenAccept(renderable -> dogRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Tidak dapat memproses model Anjing", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        }
                );

        ModelRenderable.builder()
                .setSource(this, R.raw.elephant)
                .build().thenAccept(renderable -> elephantRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Tidak dapat memproses model Gajah", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        }
                );


        ModelRenderable.builder()
                .setSource(this, R.raw.ferret)
                .build().thenAccept(renderable -> ferretRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Tidak dapat memproses model Berang-Berang", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        }
                );


        ModelRenderable.builder()
                .setSource(this, R.raw.hippopotamus)
                .build().thenAccept(renderable -> hippoRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Tidak dapat memproses model Kudanil", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        }
                );

        ModelRenderable.builder()
                .setSource(this, R.raw.horse)
                .build().thenAccept(renderable -> horseRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Tidak dapat memproses model Kuda", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        }
                );

        ModelRenderable.builder()
                .setSource(this, R.raw.koala_bear)
                .build().thenAccept(renderable -> koalaRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Tidak dapat memproses model Koala", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        }
                );


        ModelRenderable.builder()
                .setSource(this, R.raw.lion)
                .build().thenAccept(renderable -> lionRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Tidak dapat memproses model Singa", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        }
                );

        ModelRenderable.builder()
                .setSource(this, R.raw.reindeer)
                .build().thenAccept(renderable -> reindeerRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Tidak dapat memproses model Rusa", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        }
                );

        ModelRenderable.builder()
                .setSource(this, R.raw.wolverine)
                .build().thenAccept(renderable -> wolverineRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Tidak dapat memproses model Wolverine", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        }
                );
    }

    private void createModel(AnchorNode anchorNode, int selected) {
        if (selected == 1) {
            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
            bear.setParent(anchorNode);
            bear.setRenderable(bearRenderable);
            bear.select();

            addName(anchorNode, bear, "Beruang");
        }
        if (selected == 2) {
            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
            bear.setParent(anchorNode);
            bear.setRenderable(catRenderable);
            bear.select();

            addName(anchorNode, bear, "Kucing");
        }
        if (selected == 3) {
            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
            bear.setParent(anchorNode);
            bear.setRenderable(cowRenderable);
            bear.select();

            addName(anchorNode, bear, "Sapi");
        }
        if (selected == 4) {
            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
            bear.setParent(anchorNode);
            bear.setRenderable(dogRenderable);
            bear.select();

            addName(anchorNode, bear, "Anjing");
        }
        if (selected == 5) {
            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
            bear.setParent(anchorNode);
            bear.setRenderable(elephantRenderable);
            bear.select();

            addName(anchorNode, bear, "Gajah");
        }
        if (selected == 6) {
            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
            bear.setParent(anchorNode);
            bear.setRenderable(ferretRenderable);
            bear.select();

            addName(anchorNode, bear, "Berang-Berang");
        }
        if (selected == 7) {
            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
            bear.setParent(anchorNode);
            bear.setRenderable(hippoRenderable);
            bear.select();

            addName(anchorNode, bear, "Kudanil");
        }
        if (selected == 8) {
            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
            bear.setParent(anchorNode);
            bear.setRenderable(horseRenderable);
            bear.select();

            addName(anchorNode, bear, "Kuda");
        }
        if (selected == 9) {
            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
            bear.setParent(anchorNode);
            bear.setRenderable(koalaRenderable);
            bear.select();

            addName(anchorNode, bear, "Koala");
        }
        if (selected == 10) {
            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
            bear.setParent(anchorNode);
            bear.setRenderable(lionRenderable);
            bear.select();

            addName(anchorNode, bear, "Singa");
        }
        if (selected == 11) {
            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
            bear.setParent(anchorNode);
            bear.setRenderable(reindeerRenderable);
            bear.select();

            addName(anchorNode, bear, "Rusa");
        }
        if (selected == 12) {
            TransformableNode bear = new TransformableNode(arFragment.getTransformationSystem());
            bear.setParent(anchorNode);
            bear.setRenderable(wolverineRenderable);
            bear.select();

            addName(anchorNode, bear, "Wolverine");
        }
    }

    private void addName(AnchorNode anchorNode, TransformableNode model, String name) {

        ViewRenderable.builder()
                .setView(this, R.layout.name_animal)
                .build()
                .thenAccept(viewRenderable -> {
                    TransformableNode nameView = new TransformableNode(arFragment.getTransformationSystem());
                    nameView.setLocalPosition(new Vector3(0f, model.getLocalPosition().y + 0.5f, 0));
                    nameView.setParent(anchorNode);
                    nameView.setRenderable(viewRenderable);
                    nameView.select();

                    //set text
                    TextView txt_name = (TextView) viewRenderable.getView();
                    txt_name.setText(name);

                    //click to text view to remove animal
                    txt_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            anchorNode.setParent(null);
                        }
                    });
                });


    }

    private void setArrayView() {
        arrayView = new View[]{
                bear, cat, cow, dog, elephant, ferret, hippo, horse, koala, lion, reindeer, wolverine
        };
    }

    private void setOnclickListener() {
        for (int i = 0; i < arrayView.length; i++) {
            arrayView[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bear) {
            selected = 1;
            setBackground(v.getId());
        } else if (v.getId() == R.id.cat) {
            selected = 2;
            setBackground(v.getId());
        } else if (v.getId() == R.id.cow) {
            selected = 3;
            setBackground(v.getId());
        } else if (v.getId() == R.id.dog) {
            selected = 4;
            setBackground(v.getId());
        } else if (v.getId() == R.id.elephant) {
            selected = 5;
            setBackground(v.getId());
        } else if (v.getId() == R.id.ferret) {
            selected = 6;
            setBackground(v.getId());
        } else if (v.getId() == R.id.hippo) {
            selected = 7;
            setBackground(v.getId());
        } else if (v.getId() == R.id.horse) {
            selected = 8;
            setBackground(v.getId());
        } else if (v.getId() == R.id.koala) {
            selected = 9;
            setBackground(v.getId());
        } else if (v.getId() == R.id.lion) {
            selected = 10;
            setBackground(v.getId());
        } else if (v.getId() == R.id.reindeer) {
            selected = 11;
            setBackground(v.getId());
        } else if (v.getId() == R.id.wolverine) {
            selected = 12;
            setBackground(v.getId());
        }
    }

    private void setBackground(int id) {
        for (int i = 0; i < arrayView.length; i++) {
            if (arrayView[i].getId() == id) {
                arrayView[i].setBackgroundColor(Color.parseColor("#80333639"));
            } else {
                arrayView[i].setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }
}
