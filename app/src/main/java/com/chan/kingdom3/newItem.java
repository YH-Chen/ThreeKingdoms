package com.chan.kingdom3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class newItem extends AppCompatActivity {

    public static final int TAKE_PHOTO = 2;
    ImageButton imageBtn;
    private Uri imageUri;

    TextInputLayout nameTextInput;
    TextInputLayout genderTextInput;
    TextInputLayout kingdomTextInput;
    TextInputLayout birthTextInput;
    TextInputLayout deathTextInput;
    TextInputLayout native_placeTextInput;
    byte[] imageInput;
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
        imageInput = bmTObyte(BitmapFactory.decodeResource(getResources(), R.drawable.default_image));

        imageBtn = (ImageButton)findViewById(R.id.character_image_button);
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

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
                newCharacter.setImage(imageInput);
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

    //接受相机activity的返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try{
                        //将拍摄的照片显示
//                        Bitmap photoBM = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//                        imageInput = ;
//                        imageBtn.setImageBitmap(photoBM);
                        imageInput = readStream(getContentResolver().openInputStream(imageUri));
                        imageBtn.setImageBitmap(BitmapFactory.decodeByteArray(imageInput, 0, imageInput.length));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    //得到图片字节流数组大小
    public static byte [] readStream(InputStream inStream)  throws  Exception{
        ByteArrayOutputStream outStream = new  ByteArrayOutputStream();
        byte [] buffer =  new   byte [ 1024 ];
        int  len =  0 ;
        while ( (len=inStream.read(buffer)) != - 1 ){
            outStream.write(buffer, 0 , len);
        }
        outStream.close();
        inStream.close();
        return  outStream.toByteArray();
    }
}
