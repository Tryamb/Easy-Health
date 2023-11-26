package com.tryamb.healthcare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.soundcloud.android.crop.Crop;
import com.theartofdev.edmodo.cropper.CropImage;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

public class SkinScanActivity extends AppCompatActivity{
    ImageView select_btn, analyze_btn;
    ImageView imageView, iv;
    TextView select_text;
    TextView textView;
    ImageView an_img;
    TextView an_text;
    FrameLayout frameLayout;
    View view;
    int counter;
    static ArrayList<RectangleRange> ranges = new ArrayList<>();

    private final int GET_GALLERY_IMAGE = 200;
    private final int GET_CAMERA_IMAGE = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 672;

    Preprocessor preprocessor;
    static float[][][] bmpOutputs;
    float[][][][] input1;
    float[][][][] input2;
    float[][][][] input3;

    Uri imageUri;
    Uri cropUri;
    Uri cameraUri;
    String cameraFilePath;
    private MediaScanner mMediaScanner;

    File file;

    Bitmap mask_bitmap;
    Bitmap resizedBitmap;

    Bitmap croppedBitmap1 = null, croppedBitmap2 = null, croppedBitmap3 = null;
    File croppedFile;

    static ArrayList<Bitmap> croppedBitmaps = new ArrayList<>();
    SkinScanActivity mainActivity;

    FileManager fileManager;
    DeviceInfo deviceInfo;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();
    OptionsInput options_input;
    // Create a child reference
    // imagesRef now points to "images"
    StorageReference imagesRef = storageRef.child("images");
    private DatabaseReference mDatabase;
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    private static final String TAG = "PhoneState";

    Boolean isAnalyzed = false;
    Boolean fromGall = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_menu:
                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_per:
                Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainActivity = this;
        fileManager = new FileManager(getApplication());

        // 화면 켜진 상태를 유지합니다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mMediaScanner = MediaScanner.getInstance(getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        setContentView(R.layout.activity_main);

        final FirebaseCustomRemoteModel remoteModel_unet =
                new FirebaseCustomRemoteModel.Builder("unet_basic_withopt66.tflite").build();

        final FirebaseCustomLocalModel localModel_unet = new FirebaseCustomLocalModel.Builder()
                .setAssetFilePath("unet_basic_withopt66.tflite")
                .build();

        final FirebaseCustomRemoteModel remoteModel_effnet =
                new FirebaseCustomRemoteModel.Builder("eff_net_basic.tflite").build();

        final FirebaseCustomLocalModel localModel_effnet = new FirebaseCustomLocalModel.Builder()
                .setAssetFilePath("eff_net_basic.tflite")
                .build();

        /*FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();

        FirebaseModelManager.getInstance().download(remoteModel_unet, conditions)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("asdf", "unet download success");
                    }
                });

        FirebaseModelManager.getInstance().download(remoteModel_effnet, conditions)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("asdf", "effnet download success");
                    }
                });*/


        select_text = findViewById(R.id.text_select);
        imageView = findViewById(R.id.image_view);
        select_btn = findViewById(R.id.imageView);
        analyze_btn = findViewById(R.id.imageView2);
        textView = findViewById(R.id.textView);
        an_img = findViewById(R.id.analyzing_view_image);
        an_text = findViewById(R.id.analyzing_view_text);
        frameLayout = findViewById(R.id.analyzing_view);
        view = findViewById(R.id.view);

        deviceInfo = new DeviceInfo(Build.BOARD, Build.BRAND, Build.CPU_ABI, Build.DEVICE, Build.DISPLAY,
                Build.FINGERPRINT, Build.HOST, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), Build.MANUFACTURER, Build.MODEL, Build.PRODUCT,
                Build.TAGS, Build.TYPE, Build.USER, Build.VERSION.RELEASE);


        deviceInfo.logging();

        select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("");
                imageView.setImageDrawable(null);
                Intent intent = new Intent(mainActivity, AddPhotoPopup.class);
                startActivityForResult(intent, 2);
            }
        });

        analyze_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAnalyzed){
                    Toast.makeText(getApplication(), "has already been executed.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(croppedBitmap1 != null){
                    isAnalyzed = true;
                    int bithw = 304;
                    resizedBitmap = Bitmap.createScaledBitmap(croppedBitmap1, 304, 304, true);

                    preprocessor = new Preprocessor();
                    Bitmap[] combs = preprocessor.decompose(resizedBitmap);
                    croppedBitmap2 = combs[0];
                    croppedBitmap3 = combs[1];

                    float [][][][][] combs_input = preprocessor.make_inputs_unet(bithw, resizedBitmap, croppedBitmap2, croppedBitmap3);
                    input1 = combs_input[0];
                    input2 = combs_input[1];
                    input3 = combs_input[2];

                    frameLayout.bringToFront();
                    view.setVisibility(View.VISIBLE);
                    an_img.setVisibility(View.VISIBLE);
                    an_text.setVisibility(View.VISIBLE);



                    ModelInference segment_classify_model = new ModelInference(SkinScanActivity.this, resizedBitmap, imageUri, cropUri, 0);
                    FirebaseModelInterpreterOptions options2 = segment_classify_model.initializeUnet(localModel_unet);
                    segment_classify_model.segment_and_classify(input1, input2, input3, options2, localModel_effnet, () -> {
                        if (textView.getText().toString().trim().isEmpty()) {
                            view.setVisibility(View.INVISIBLE);
                            an_img.setVisibility(View.INVISIBLE);
                            an_text.setVisibility(View.INVISIBLE);
                            textView.setText("No disease is found.");
                        }
                    });
                }
            }
        });
    }

    String res_all = "";
    Mat orig_changed;

    List<MatOfPoint> contours;
    Bitmap[] bmps;

    Bitmap new_bit;


    private void update_firebase(){
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a");
        String time = sdf.format(dt).toString();
        OriginalInfo originalInfo = new OriginalInfo();
        //mDatabase.updateChildren(originalInfo.postInfo(deviceInfo.getId(), time));
        //fileManager.uploadImageOrigin(croppedBitmap1, "original", deviceInfo.getId(), time);
        for(int i=0;i<croppedBitmaps.size();i++){
            double area = croppedBitmaps.get(i).getWidth() * croppedBitmaps.get(i).getHeight();
            int[] pixels = new int[(int)area];
            croppedBitmaps.get(i).getPixels(pixels, 0, croppedBitmaps.get(i).getWidth(), 0, 0, croppedBitmaps.get(i).getWidth(), croppedBitmaps.get(i).getHeight());
            double R = 0;
            double G = 0;
            double B = 0;
            for(int j=0;j<(int)area;j++){
                R += Color.red(pixels[j]);
                G += Color.green(pixels[j]);
                B += Color.blue(pixels[j]);
            }
            R /= area;
            G /= area;
            B /= area;
            CropInfo cropInfo = new CropInfo(area, R, G, B);
            String parameter = (char)('A'+i)+"";
//            mDatabase.updateChildren(cropInfo.postInfo(deviceInfo.getId(), time, parameter));
//            fileManager.uploadImageOrigin(croppedBitmaps.get(i), parameter, deviceInfo.getId(), time);
        }
       // mDatabase.updateChildren(deviceInfo.postInfo());

    }
    private void cropImage(Uri photoUri) {
        /**
         *  If you select from the gallery, there is no tempFile, so a new one is created.
         */
        try {
            croppedFile = fileManager.createImageFile("croppedFile");
        } catch (IOException e) {
            Toast.makeText(this, "Image processing error! please try again.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        //크롭 후 저장할 Uri
        cropUri = Uri.fromFile(croppedFile);
        Crop.of(photoUri, cropUri).withMaxSize(304, 304).start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            int result = data.getIntExtra("result", 1);
            switch (result) {
                case 2:
                    camera();
                    break;
                case 3:
                    //Clear previous inputs
                    counter = 0;
//                    imageView.setImageDrawable(null);
//                    textView.setText("");
                    res_all = "";
                    //~Clear previous inputs

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, GET_GALLERY_IMAGE);
                    break;
            }
        }
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            select_text.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            fromGall = true;
            cropImage(imageUri);
        }
        if (requestCode == GET_CAMERA_IMAGE && resultCode == RESULT_OK) {
            imageView.setImageURI(cameraUri);
            select_text.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            cropImage(cameraUri);
        }
        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            imageView.setImageURI(cropUri);
            Log.d("imageString", cropUri.toString());
            isAnalyzed = false;
            try {
                croppedBitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), cropUri);
                if (fromGall) {
                    ExifInterface exif = null;

                    try {
                        exif = new ExifInterface(cropUri.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int exifOrientation;
                    int exifDegree;

                    if (exif != null) {
                        exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        exifDegree = exifOrientationToDegrees(exifOrientation);
                    } else {
                        exifDegree = 0;
                    }
                    croppedBitmap1 = rotate(croppedBitmap1, exifDegree);
                    imageView.setImageBitmap(croppedBitmap1);
                    Log.d("crop hua hai ya nhi", "ho gya");
                }


                //fileManager.uploadImage(croppedBitmap1, "img1");
            } catch (Exception e) {
                Log.e("except", String.valueOf(e));
            }
        }
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                int result = data.getIntExtra("result", 1);
                if (result != 1) {
                    String data1 = data.getStringExtra("data");
                    String numb = data.getStringExtra("numb");
                    Bitmap bmp = data.getParcelableExtra("image");

                    Intent intent = new Intent(this, InfoActivity.class);
                    intent.putExtra("image", bmp);
                    intent.putExtra("numb", numb);
                    intent.putExtra("data", data1);
                    startActivity(intent);
                }
            }
        }

    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    public void camera(){
        fromGall = false;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            File cameraFile = null;
            try{
                cameraFile = fileManager.createImageFile2();
                cameraFilePath = cameraFile.getAbsolutePath();
            } catch(IOException e){
                Log.e("camera related error", String.valueOf(e));
            }
            if(cameraFile != null){
               // cameraUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), cameraFile);
                cameraUri = FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID+ ".fileprovider",
                        cameraFile);
                Log.d("camera wala uri:",cameraUri.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                startActivityForResult(intent, GET_CAMERA_IMAGE);
            }
        }
    }
}