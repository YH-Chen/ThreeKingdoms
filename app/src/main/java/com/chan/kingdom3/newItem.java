package com.chan.kingdom3;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class newItem extends AppCompatActivity {

    public static final int TAKE_PHOTO = 2;
    public static final int CHOOSE_PHOTO = 3;
    ImageButton imageBtn;
    private Uri imageUri;

    TextInputLayout nameTextInput;
    TextInputLayout genderTextInput;
    TextInputLayout kingdomTextInput;
    TextInputLayout birthTextInput;
    TextInputLayout deathTextInput;
    TextInputLayout native_placeTextInput;
    Bitmap imageInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        nameTextInput = (TextInputLayout)findViewById(R.id.character_name_textinput);
        genderTextInput = (TextInputLayout)findViewById(R.id.character_gender_textinput);
        kingdomTextInput = (TextInputLayout)findViewById(R.id.character_kingdom_textinput);
        birthTextInput = (TextInputLayout)findViewById(R.id.character_birth_textinput);
        deathTextInput = (TextInputLayout)findViewById(R.id.character_death_textinput);
        native_placeTextInput = (TextInputLayout)findViewById(R.id.character_native_place_textinput);
        imageInput = BitmapFactory.decodeResource(getResources(), R.drawable.default_image);

        //头像的对话框
        final String[] opItem = {"拍照", "从相册里选择"};
        final AlertDialog.Builder pic_alertdialog = new AlertDialog.Builder(this);
        pic_alertdialog.setTitle("选择头像");
        //头像按钮
        imageBtn = (ImageButton)findViewById(R.id.character_image_button);
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pic_alertdialog.setItems(opItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            //拍照
                            take_photo();
                        }
                        else{
                            //从相册里选择
//                            Toast.makeText(getApplication(), "还无法打开相册", Toast.LENGTH_LONG).show();
                            choose_photo();
                        }
                    }
                }).show();
            }
        });

        //确定按钮
        Button confirmBtn = (Button)findViewById(R.id.confirm);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                character newCharacter = new character();
                newCharacter.setName(nameTextInput.getEditText().getText().toString());
                newCharacter.setGender(genderTextInput.getEditText().getText().toString());
                newCharacter.setKingdom(kingdomTextInput.getEditText().getText().toString());
                newCharacter.setBirth(birthTextInput.getEditText().getText().toString());
                newCharacter.setDeath(deathTextInput.getEditText().getText().toString());
                newCharacter.setNative_place(native_placeTextInput.getEditText().getText().toString());
                newCharacter.setImage(bmTObyte(imageInput));
                newCharacter.save();

                Intent intent = new Intent();
                intent.putExtra("ID", newCharacter.getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });//end confirmBtn Listener

        Button cancelBtn = (Button)findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("ID", -1);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }//end Create

    //bitmap转为字节流
    private byte[] bmTObyte(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private int calSampeSize(int height, int width){
        int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
        int minLen = Math.min(height, width); // 原图的最小边长
        if(minLen > 300) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
            float ratio = (float)minLen / 300.0f; // 计算像素压缩比例
            inSampleSize = (int)ratio;
        }
        return inSampleSize;
    }

    //接受相机activity的返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try{
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
                        BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);
                        int inSampleSize = calSampeSize(options.outHeight, options.outWidth); // 计算压缩比例
                        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
                        options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
                        Bitmap bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);
                        imageInput = bm;
                        imageBtn.setImageBitmap(bm);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
//                    handleImage(data);
                    //获取图片地址 from: http://blog.csdn.net/w18756901575/article/details/52085157
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
                    String imagePath = cursor.getString(columnIndex);
                    //修改图片大小 from: http://blog.csdn.net/adam_ling/article/details/52346741
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
                    BitmapFactory.decodeFile(imagePath, options); // 解码出图片边长
                    int inSampleSize = calSampeSize(options.outHeight, options.outWidth); // 计算压缩比例
                    options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
                    options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
                    Bitmap bm = BitmapFactory.decodeFile(imagePath, options); // 解码文件
                    Log.w("TAG", "size: " + bm.getByteCount() + " width: " + bm.getWidth() + " heigth:" + bm.getHeight()); // 输出图像数据
                    imageBtn.setImageBitmap(bm);
                    imageInput = bm;
                    cursor.close();
                }
            default:
                break;
        }
    }

    private void take_photo(){
        //创建File对象，用于存储拍照后的图片
        File outputImage = new File(getExternalCacheDir(), "output_image.png");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(newItem.this, "com.chan.fileprovider", outputImage);
        }else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intentTOphoto = new Intent("android.media.action.IMAGE_CAPTURE");
        intentTOphoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentTOphoto, TAKE_PHOTO);
    }

    private void choose_photo(){
        //启动相册
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CHOOSE_PHOTO);
    }
}
