package com.chan.kingdom3;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
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

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class detail extends AppCompatActivity {
    character chose_character;
    int item_id = -1;
    int count = 0;

    private ConstraintLayout BG;
    private TextView nickname;
    private TextView name;
    private TextView detail;
    private TextView jianjie;
    private FloatingActionsMenu floatingBtn;
    private FloatingActionButton back;
    private FloatingActionButton change;
    private ImageView touxiang;
    private ConstraintLayout changeCons;
    private EditText nick_edit;
    private EditText name_edit;
    private EditText detail_edit;
    private Button change_yes;
    private Button delete;
    private Button toDetail;
    private AlertDialog.Builder bulider;
    private Typeface nickType, nameType, detailType;
    int wei, shu, wu, qun, changeBG;

    List<Map<String,Object>> Informations = new ArrayList<>();
    Bitmap[] image_list = new Bitmap[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = this.getIntent().getExtras();
        if(extras != null)
        {
            item_id = extras.getInt("ID");
            chose_character = DataSupport.find(character.class, item_id);
        }

        //设置图片
        ImageView image = (ImageView)findViewById(R.id.image_detail);
        byte[] bsTemp =  chose_character.getImage();//数据库存的是字节流
        Bitmap bmTemp = BitmapFactory.decodeByteArray(bsTemp, 0, bsTemp.length);//解码字节流得到图片
        image.setImageBitmap(bmTemp);

        //返回按钮
        ImageButton BackButton = (ImageButton)findViewById(R.id.back_button);
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //人物姓名
        TextView character_name = (TextView)findViewById(R.id.character_name);
        character_name.setText(chose_character.getName());


        List<Map<String,Object>> one_info = new ArrayList<>();
        String[] info_str = {chose_character.getGender(),
                              chose_character.getKingdom(),
                              chose_character.getBirth(),
                              chose_character.getDeath(),
                              chose_character.getNative_place()};
        for(int i = 0; i < 5; i++)
        {
            Map<String,Object> temp = new LinkedHashMap<>();
            temp.put("message", info_str[i]);
            one_info.add(temp);
        }
        ListView MoreListView = (ListView)findViewById(R.id.info_list);
        SimpleAdapter moreListAdapter = new SimpleAdapter(this, one_info,R.layout.more_list_layout, new String[]{"message"},new int[]{R.id.more_message});
        MoreListView.setAdapter(moreListAdapter);
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    private void initial(){
        BG = (ConstraintLayout)findViewById(R.id.detailBG);
        nickname = (TextView)findViewById(R.id.nickname);
        name = (TextView)findViewById(R.id.name);
        detail = (TextView)findViewById(R.id.detail);
        jianjie = (TextView)findViewById(R.id.jianjie);
        floatingBtn = (FloatingActionsMenu)findViewById(R.id.floatingBtn);
        back = (FloatingActionButton)findViewById(R.id.back);
        change = (FloatingActionButton)findViewById(R.id.change);
        touxiang = (ImageView)findViewById(R.id.touxiang);
        changeCons = (ConstraintLayout)findViewById(R.id.change_cons);
        nick_edit = (EditText) findViewById(R.id.nick_edit);
        name_edit = (EditText) findViewById(R.id.name_edit);
        detail_edit = (EditText) findViewById(R.id.detail_edit);
        change_yes = (Button)findViewById(R.id.change_yes);
        delete = (Button)findViewById(R.id.delete);
        toDetail = (Button)findViewById(R.id.back_to_detail);
        nickType = Typeface.createFromAsset(getAssets(), "nickname.TTF");
        nameType = Typeface.createFromAsset(getAssets(), "name.TTF");
        detailType = Typeface.createFromAsset(getAssets(), "FZLBJW.TTF");
        bulider = new AlertDialog.Builder(this);

        nickname.setVisibility(View.VISIBLE);
        nickname.setTypeface(nickType);
        name.setVisibility(View.VISIBLE);
        name.setTypeface(nameType);
        detail.setVisibility(View.VISIBLE);
        jianjie.setVisibility(View.VISIBLE);
        jianjie.setTypeface(detailType);
        floatingBtn.setVisibility(View.VISIBLE);
        floatingBtn.setClickable(true);
        changeCons.setVisibility(View.INVISIBLE);
        change_yes.setClickable(false);
        delete.setClickable(false);
        touxiang.setClickable(false);
        nick_edit.setClickable(false);
        name_edit.setClickable(false);
        detail_edit.setClickable(false);
        toDetail.setClickable(false);
    }

    private void setListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail.this.finish();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nick_edit.setText(nickname.getText());
                name_edit.setText(name.getText());
                detail_edit.setText(detail.getText());
                BG.setBackgroundResource(R.mipmap.changebg);
                nickname.setVisibility(View.INVISIBLE);
                name.setVisibility(View.INVISIBLE);
                detail.setVisibility(View.INVISIBLE);
                jianjie.setVisibility(View.INVISIBLE);
                floatingBtn.setVisibility(View.INVISIBLE);
                floatingBtn.setClickable(false);
                changeCons.setVisibility(View.VISIBLE);
                change_yes.setClickable(true);
                delete.setClickable(true);
                touxiang.setClickable(true);
                nick_edit.setClickable(true);
                name_edit.setClickable(true);
                detail_edit.setClickable(true);
                toDetail.setClickable(true);
            }
        });

        touxiang.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //打开新头像并更新

                return false;
            }
        });

        change_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail.this.bulider.setMessage("确定更改武将信息?");
                detail.this.bulider.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});
                detail.this.bulider.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newNick = nick_edit.getText().toString();
                        String newName = name_edit.getText().toString();
                        String newDetail = detail_edit.getText().toString();
                        //得到新头像
                        //newTouxiang = getFromImageView;

                        //更新数据库中的数据
                        //update(newNick, newName, newDetail, newTouxiang)

                        //从数据库中读取数据，设置背景

                        nickname.setText(newNick);
                        name.setText(newName);
                        detail.setText(newDetail);
                        nickname.setVisibility(View.VISIBLE);
                        name.setVisibility(View.VISIBLE);
                        detail.setVisibility(View.VISIBLE);
                        jianjie.setVisibility(View.VISIBLE);
                        floatingBtn.setVisibility(View.VISIBLE);
                        floatingBtn.setClickable(true);
                        changeCons.setVisibility(View.INVISIBLE);
                        change_yes.setClickable(false);
                        delete.setClickable(false);
                        touxiang.setClickable(false);
                        nick_edit.setClickable(false);
                        name_edit.setClickable(false);
                        detail_edit.setClickable(false);
                        Toast.makeText(detail.this, "修改成功！", Toast.LENGTH_SHORT).show();
                    }
                }).create().show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail.this.bulider.setMessage("确定删除该武将信息?\n警告:该操作无法删除");
                detail.this.bulider.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});
                detail.this.bulider.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(detail.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("delete", true);

                        //bundle中放入要删除的武将在数据库中的id

                        intent.putExtras(bundle);
                        detail.this.startActivity(intent);
                    }
                }).create().show();
            }
        });

        toDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail.this.bulider.setMessage("现在返回将放弃所有更改");
                detail.this.bulider.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});
                detail.this.bulider.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //从数据库中读取该武将势力，重新设置背景

                        nickname.setVisibility(View.VISIBLE);
                        name.setVisibility(View.VISIBLE);
                        detail.setVisibility(View.VISIBLE);
                        jianjie.setVisibility(View.VISIBLE);
                        floatingBtn.setVisibility(View.VISIBLE);
                        //floatingBtn.setClickable(true);
                        changeCons.setVisibility(View.INVISIBLE);
                        change_yes.setClickable(false);
                        delete.setClickable(false);
                        touxiang.setClickable(false);
                        nick_edit.setClickable(false);
                        name_edit.setClickable(false);
                        detail_edit.setClickable(false);
                        Toast.makeText(detail.this, "修改未保存", Toast.LENGTH_SHORT).show();
                    }
                }).create().show();
            }
        });
    }

}
