package com.chan.kingdom3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class detail extends AppCompatActivity {
    character chose_character;
    String DYNAMICACTION = "dynamic_action";
    int item_id = -1;
    int count = 0;
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

        //星标按钮
        final ImageButton Star = (ImageButton)findViewById(R.id.Star);
        Star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Star.getTag().toString().equals("0"))
                {
                    Star.setTag("1");
                    Star.setImageResource(R.drawable.full_star);
                }
                else if(Star.getTag().toString().equals("1"))
                {
                    Star.setTag("0");
                    Star.setImageResource(R.drawable.empty_star);
                }
            }
        });

//        ImageButton shoppingCart = (ImageButton)findViewById(R.id.shopping);
//        shoppingCart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                choiceName = Informations.get(item_NO).get("goodName").toString();
//                choicePrice = Informations.get(item_NO).get("Price").toString();
//                count++;
//
//                Toast.makeText(detail.this,"商品已添加到购物车",Toast.LENGTH_SHORT).show();
//
//                Bundle add_goods_bundle = new Bundle();
//                add_goods_bundle.putString("name", choiceName);
//                add_goods_bundle.putInt("icon", imageID[item_NO]);
//                Intent intentBroadcast = new Intent(DYNAMICACTION);
//                intentBroadcast.putExtras(add_goods_bundle);
//                sendBroadcast(intentBroadcast);
//
////                EventBus.getDefault().post(new MessageEvent(choiceName, choicePrice));
//            }
//        });

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
}
