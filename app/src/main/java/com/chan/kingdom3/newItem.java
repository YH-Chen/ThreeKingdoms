package com.chan.kingdom3;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
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
import android.widget.Spinner;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class newItem extends AppCompatActivity {
    character curr_character;
    int item_id = -1;
    public static final int TAKE_PHOTO = 2;
    public static final int CHOOSE_PHOTO = 3;
    private Uri imageUri;
    Bitmap imageInput;
    private Typeface type;
    private AlertDialog.Builder builder;

    private TextView nameText;
    private TextView nickText;
    private TextView genderText;
    private TextView kingdomText;
    private TextView placeText;
    private TextView birdeaText;
    private TextView detailText;
    private EditText nickEdit;
    private EditText nameEdit;
    private EditText detailEdit;
    private EditText birthEdit;
    private EditText deathEdit;
    private EditText placeEdit;
    private Spinner kingdomSpin;
    private Spinner genderSpin;
    private Button yes;
    private Button back;
    private ImageView headPhoto;

    private void initial(){
        nameText = (TextView) findViewById(R.id.name_text);
        nickText = (TextView)findViewById(R.id.nick_text);
        genderText = (TextView)findViewById(R.id.gender_text);
        kingdomText = (TextView)findViewById(R.id.kingdom_text);
        placeText = (TextView)findViewById(R.id.place_text);
        birdeaText = (TextView)findViewById(R.id.birdea_text);
        detailText = (TextView)findViewById(R.id.detail_text);
        nickEdit = (EditText)findViewById(R.id.nick_edit);
        nameEdit = (EditText)findViewById(R.id.name_edit);
        detailEdit = (EditText)findViewById(R.id.detail_edit);
        birthEdit = (EditText)findViewById(R.id.birth_edit);
        deathEdit = (EditText)findViewById(R.id.death_edit);
        placeEdit = (EditText)findViewById(R.id.place_edit);
        kingdomSpin = (Spinner)findViewById(R.id.kingdom_sel);
        genderSpin = (Spinner)findViewById(R.id.gender_sel);
        yes = (Button)findViewById(R.id.change_yes);
        back = (Button)findViewById(R.id.change_cancel);;
        headPhoto = (ImageView)findViewById(R.id.change_head);

        type = Typeface.createFromAsset(getAssets(), "FZLBJW.TTF");
        nameText.setTypeface(type);
        nickText.setTypeface(type);
        genderText.setTypeface(type);
        kingdomText.setTypeface(type);
        placeText.setTypeface(type);
        birdeaText.setTypeface(type);
        detailText.setTypeface(type);

        builder = new AlertDialog.Builder(this);

        nickEdit.setText(curr_character.getNickname());
        nameEdit.setText(curr_character.getName());
        detailEdit.setText(curr_character.getProfile());
        birthEdit.setText(curr_character.getBirth());
        deathEdit.setText(curr_character.getDeath());
        placeEdit.setText(curr_character.getNative_place());
        imageInput = BitmapFactory.decodeByteArray(curr_character.getImage(),0, curr_character.getImage().length);

        String [] kingdomArr = getResources().getStringArray(R.array.kingdom_spin);
        for(int i = 0; i <= 3; i++)
            if(kingdomArr[i].equals(curr_character.getKingdom())) kingdomSpin.setSelection(i);
        if("男".equals(curr_character.getGender())) genderSpin.setSelection(0);
        else genderSpin.setSelection(1);

        byte[] bsTemp =  curr_character.getImage();//数据库存的是字节流
        Bitmap bmTemp = BitmapFactory.decodeByteArray(bsTemp, 0, bsTemp.length);//解码字节流得到图片
        headPhoto.setImageBitmap(bmTemp);
    }

    private void setListener(){
        //确定并退出NewItem
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newItem.this.builder.setMessage("确认修改武将信息？修改后将无法撤回。");
                newItem.this.builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});
                newItem.this.builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        curr_character.setBirth(birthEdit.getText().toString());
                        curr_character.setDeath(deathEdit.getText().toString());
                        curr_character.setName(nameEdit.getText().toString());
                        curr_character.setNickname(nickEdit.getText().toString());
                        curr_character.setNative_place(placeEdit.getText().toString());
                        curr_character.setProfile(detailEdit.getText().toString());
                        curr_character.setKingdom(kingdomSpin.getSelectedItem().toString());
                        curr_character.setGender(genderSpin.getSelectedItem().toString());
                        curr_character.setImage(bmTObyte(imageInput));
                        curr_character.save();
                        Intent intent = new Intent();
                        intent.putExtra("ID", curr_character.getId());
                        newItem.this.setResult(RESULT_OK, intent);
                        newItem.this.finish();
                    }
                }).create().show();
            }
        });

        //取消并退出NewItem
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newItem.this.builder.setMessage("确认返回？修改的数据将不被保存。");
                newItem.this.builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});
                    newItem.this.builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newItem.this.finish();
                    }
                }).create().show();
            }
        });

        final String[] opItem = {"拍照", "从相册里选择"};
        final AlertDialog.Builder pic_alertdialog = new AlertDialog.Builder(this);
        pic_alertdialog.setTitle("选择头像");
        headPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                pic_alertdialog.setItems(opItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            //拍照
                            take_photo();
                        }
                        else{
                            //从相册里选择
                            choose_photo();
                        }
                    }
                }).show();
                return false;
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        Bundle extras = this.getIntent().getExtras();
        item_id = extras.getInt("ID");
        curr_character = DataSupport.find(character.class, item_id);
        initial();
        setListener();

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
                        headPhoto.setImageBitmap(bm);
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
                    headPhoto.setImageBitmap(bm);
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

    //返回按钮
    @Override
    public void onBackPressed(){
        newItem.this.builder.setMessage("确认返回？修改的数据将不被保存。");
        newItem.this.builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}});
        newItem.this.builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newItem.this.finish();
            }
        }).create().show();
    }
}
