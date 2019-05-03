package com.example.instagramclone.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagramclone.Data.APIClient;
import com.example.instagramclone.Data.ApiInterface;
import com.example.instagramclone.R;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditFeedActivity extends AppCompatActivity {
    @BindView(R.id.edit_feed)
    EditText edit_feed;
    @BindView(R.id.text_edit)
    TextView text_edit;
    @BindView(R.id.edit_photo)
    ImageView edit_photo;

    private static final int MY_PERMISSION_CAMERA = 1111;
    private static final int REQUEST_TAKE_PHOTO = 2222;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    private static final int REQUEST_IMAGE_CROP = 4444;

    Uri imageUri, photoURI, albumURI;
    String mCurrentPhotoPath;
    String feed;
    ApiInterface apiInterface;

    SharedPreferences sharedPreferences;
    String token, realphotouri;
    MultipartBody.Part imageBody;
    RequestBody tokenBody, feedbody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_feed);

        ButterKnife.bind(this);

        //사진 찍고 회전 막아줌.
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new RoundedCorners(15));

        sharedPreferences = getApplicationContext().getSharedPreferences("Token", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        tokenBody = RequestBody.create(MediaType.parse("multipart/form-data"), token);

    }

    @OnClick(R.id.btn_feed)
    void Btn_feed() {

        feed = edit_feed.getText().toString();
        feedbody = RequestBody.create(MediaType.parse("multipart/form-data"), feed);

        if (edit_photo.getDrawable() == null || feed.equals("")) {
            Toast.makeText(this, "사진 또는 피드글을 작성해주세요:)", Toast.LENGTH_SHORT).show();
        } else {

            apiInterface = APIClient.getClient().create(ApiInterface.class);
            Call<JsonObject> call = apiInterface.FeedInsert(tokenBody, imageBody, feedbody);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(EditFeedActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });

        }

    }


    public void iv_view(View view) {
        checkPermission();
        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                captureCamera();
            }
        };
        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getAlbum();
            }
        };
        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
        DialogInterface.OnClickListener basic = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "기본 이미지", Toast.LENGTH_SHORT).show();
            }
        };
        new AlertDialog.Builder(this)
                .setTitle("업로드할 이미지 선택")
                .setPositiveButton("사진 촬영", cameraListener)
                .setNegativeButton("앨범 선택", albumListener)
                .setNeutralButton("기본 이미d지", basic)
                .setNeutralButton("취소", cancelListener)
                .show();
    }

    private void captureCamera() {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = creatImageFile();
                } catch (IOException ex) {
                }
                if (photoFile != null) {
                    Uri providerURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName(), photoFile);
                    imageUri = providerURI;

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(this, "저장공간이 접근 불가능한 기기입니다.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public File creatImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "gyeom");

        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;

        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void getAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    // 갤러리 이미지 crop
    private void galleryAddpic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        //갤러리에 사진 저장
//        mediaScanIntent.setData(contentUri);
//        sendBroadcast(mediaScanIntent);
//        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    // 카메라 이미지 crop
    public void cropSingleImage(Uri photoUriPath) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        cropIntent.setDataAndType(photoUriPath, "image/*");
        cropIntent.putExtra("aspectX", true);
        cropIntent.putExtra("aspectY", true);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", photoUriPath);

        List list = getPackageManager().queryIntentActivities(cropIntent, 0);

        Intent i = new Intent(cropIntent);
        ResolveInfo res = (ResolveInfo) list.get(0);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        grantUriPermission(res.activityInfo.packageName, photoUriPath,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

        startActivityForResult(i, REQUEST_IMAGE_CROP);

    }

    public void cropImage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        cropIntent.putExtra("aspectX", true);
        cropIntent.putExtra("aspectY", true);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", albumURI);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(15));
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        // cropSingleImage(imageUri);  갤러리 crop 이미지 저장
                        Glide.with(this).load(imageUri).apply(requestOptions).into(edit_photo);
                        realphotouri = getRealPathFromURI(imageUri);
                        File file = new File(realphotouri);
                        imageBody = MultipartBody.Part.createFormData("file",
                                file.getName(), RequestBody.create(MediaType.parse("userfile"), file));

                    } catch (Exception e) {

                    }
                } else {
                    Toast.makeText(this, "사진찍기를 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_TAKE_ALBUM:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getData() != null) {
                        try {
                            File albumFile = null;
                            albumFile = creatImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                            cropImage();
                        } catch (IOException e) {

                        }
                    }
                }
                break;
            case REQUEST_IMAGE_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    // galleryAddpic(); 갤러리 crop 이미지 저장
                    Glide.with(this).load(albumURI).apply(requestOptions).into(edit_photo);
                    realphotouri = getRealPathFromURI(albumURI);
                    File file = new File(realphotouri);
                    imageBody = MultipartBody.Part.createFormData("file",
                            file.getName(), RequestBody.create(MediaType.parse("userfile"), file));
                }
                break;
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();

        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        return result;
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 처음 호출시엔 if 안의 부분은 false로 리턴, else 의 요청으로 넘어감
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA))) {
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CAMERA:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] < 0) {
                        Toast.makeText(this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                break;
        }
    }
}
