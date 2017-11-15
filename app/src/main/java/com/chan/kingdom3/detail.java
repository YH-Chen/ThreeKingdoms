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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class detail extends AppCompatActivity {
    character character;
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
            character = DataSupport.find(character.class, item_id);
        }

        initial();
        set();
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

    private void set(){
        //设置背景
        if(character.getKingdom().equals("魏")) BG.setBackgroundResource(wei);
        if(character.getKingdom().equals("蜀")) BG.setBackgroundResource(shu);
        if(character.getKingdom().equals("吴")) BG.setBackgroundResource(wu);
        if(character.getKingdom().equals("群")) BG.setBackgroundResource(qun);

        //设置图片
        byte[] bsTemp =  character.getImage();//数据库存的是字节流
        Bitmap bmTemp = BitmapFactory.decodeByteArray(bsTemp, 0, bsTemp.length);//解码字节流得到图片
        image.setImageBitmap(bmTemp);

        //人物信息
        name.setText(character.getName());
        nickname.setText(character.getNickname());
        detail.setText("    " + character.getName() + "(" + character.getBirth() + " - " + character.getDeath() + "),"
                + character.getGender() + ",属" + character.getKingdom() + "势力,"
                + character.getNative_place() + "人。\n" + character.getProfile());
    }

    private void setListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(detail.this, MainActivity.class);
                intent.putExtra("ID", item_id);
                detail.this.setResult(3, intent);
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
                detail.this.startActivity(intent);
                set();
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
                        Intent intent = new Intent(detail.this, MainActivity.class);
                        intent.putExtra("ID", item_id);
                        detail.this.setResult(1, intent);
                        detail.this.finish();
                    }
                }).create().show();
            }
        });
    }

}
