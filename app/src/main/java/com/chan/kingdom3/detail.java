package com.chan.kingdom3;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.litepal.crud.DataSupport;

public class detail extends AppCompatActivity {
    public static int FROM_NEWITEM = 2;
    public static int FROM_DETAIL = 3;
    character curr_character;
    int item_id = -1;

    private ConstraintLayout BG;
    private TextView nickname;
    private TextView name;
    private TextView detail;
    private TextView jianjie;
    private FloatingActionButton back;
    private FloatingActionButton toEdit;
    private FloatingActionButton delete;
    private AlertDialog.Builder builder;
    private Typeface nickType, nameType, detailType;
    private ImageView image;
    int wei, shu, wu, qun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = this.getIntent().getExtras();
        if(extras != null)
        {
            item_id = extras.getInt("ID");
            curr_character = DataSupport.find(character.class, item_id);
        }

        initial();
        set(curr_character);
        setListener();
    }

    private void initial(){
        BG = (ConstraintLayout)findViewById(R.id.detailBG);
        nickname = (TextView)findViewById(R.id.detail_nick);
        name = (TextView)findViewById(R.id.detail_name);
        detail = (TextView)findViewById(R.id.detail_detail);
        jianjie = (TextView)findViewById(R.id.detail_jianjie);
        back = (FloatingActionButton)findViewById(R.id.detail_back);
        toEdit = (FloatingActionButton)findViewById(R.id.detail_to_edit);
        delete = (FloatingActionButton)findViewById(R.id.detail_delete);
        image = (ImageView)findViewById(R.id.detail_image);

        nickType = Typeface.createFromAsset(getAssets(), "nickname.TTF");
        nameType = Typeface.createFromAsset(getAssets(), "name.TTF");
        detailType = Typeface.createFromAsset(getAssets(), "FZLBJW.TTF");
        builder = new AlertDialog.Builder(this);

        nickname.setTypeface(nickType);
        name.setTypeface(nameType);
        jianjie.setTypeface(detailType);

        wei = R.mipmap.wei;
        shu = R.mipmap.shu;
        wu = R.mipmap.wu;
        qun = R.mipmap.qun;
    }

    private void set(character new_character){
        //设置背景
        if(new_character.getKingdom().equals("魏")) BG.setBackgroundResource(wei);
        if(new_character.getKingdom().equals("蜀")) BG.setBackgroundResource(shu);
        if(new_character.getKingdom().equals("吴")) BG.setBackgroundResource(wu);
        if(new_character.getKingdom().equals("群")) BG.setBackgroundResource(qun);

        //设置图片
        byte[] bsTemp =  new_character.getImage();//数据库存的是字节流
        Bitmap bmTemp = BitmapFactory.decodeByteArray(bsTemp, 0, bsTemp.length);//解码字节流得到图片
        image.setImageBitmap(bmTemp);

        //人物信息
        name.setText(new_character.getName());
        nickname.setText(new_character.getNickname());
        detail.setText("    " + new_character.getName() + "(" + new_character.getBirth() + " - " + new_character.getDeath() + "),"
                + new_character.getGender() + ",属" + new_character.getKingdom() + "势力,"
                + new_character.getNative_place() + "人。\n" + new_character.getProfile());
    }

    private void setListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(detail.this, MainActivity.class);
                intent.putExtra("ID", item_id);
                intent.putExtra("Flag", true);
                detail.this.setResult(RESULT_OK, intent);
                detail.this.finish();
            }
        });

        toEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(detail.this, newItem.class);
                Bundle bundle = new Bundle();
                bundle.putInt("ID", item_id);
                bundle.putInt("which", 2);
                intent.putExtras(bundle);
                detail.this.startActivityForResult(intent, FROM_NEWITEM);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail.this.builder.setMessage("确定要删除该武将吗？该操作不可撤回。");
                detail.this.builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});
                detail.this.builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataSupport.delete(character.class, item_id);
                        Intent intent = new Intent(detail.this, MainActivity.class);
                        intent.putExtra("Flag", false);
                        intent.putExtra("ID", item_id);
                        detail.this.setResult(RESULT_OK, intent);
                        detail.this.finish();
                    }
                }).create().show();
            }
        });
    }

    //接受回传的信息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            if(requestCode == FROM_NEWITEM){
                character new_character = DataSupport.find(character.class, data.getIntExtra("ID", -1));
                set(new_character);
            }
        }
    }
}
