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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class detail extends AppCompatActivity {
    String data = null;
    String choiceName = null;
    String choicePrice = null;
    String DYNAMICACTION = "dynamic_action";
    int item_NO = -1;
    int count = 0;
    List<Map<String,Object>> Informations = new ArrayList<>();
    Bitmap[] image_list = new Bitmap[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        fill_image_list();
        initInformation();

        Bundle extras = this.getIntent().getExtras();
        if(extras != null)
        {
            data = extras.getString("Name");
            for(int i = 0; i < Informations.size(); i++)
            {
                if(Informations.get(i).get("Name").toString().equals(data))
                {
                    item_NO = i;
                }
            }
        }

        //设置图片
        ImageView image = (ImageView)findViewById(R.id.image_detail);
        image.setImageBitmap(image_list[item_NO]);

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
        character_name.setText(data);

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
        String[] info_str = {Informations.get(item_NO).get("gender").toString(),
                                Informations.get(item_NO).get("Kingdom").toString(),
                                Informations.get(item_NO).get("birth").toString(),
                                Informations.get(item_NO).get("death").toString(),
                                Informations.get(item_NO).get("native_place").toString()};
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

    void fill_image_list(){
        int[] ImageID = {R.drawable.liubei,R.drawable.guanyu, R.drawable.zhangfei, R.drawable.zhugeliang, R.drawable.zhaoyun,
                R.drawable.caochao, R.drawable.sunquan, R.drawable.simayi, R.drawable.wanglang, R.drawable.huangai};
        for(int i = 0; i < 10; i++){
            Bitmap tmp_mp = BitmapFactory.decodeResource(getResources(), ImageID[i]);
            image_list[i] = tmp_mp;
        }
    }
    void initInformation()
    {
        String[] Name = getResources().getStringArray(R.array.character_names);
        String[] Kingdom = getResources().getStringArray(R.array.Kingdoms);
        String[] gender = getResources().getStringArray(R.array.gender);
        String[] birth = getResources().getStringArray(R.array.birth);
        String[] death = getResources().getStringArray(R.array.death);
        String[] native_place = getResources().getStringArray(R.array.native_place);

        for(int i = 0; i < 10; i++)
        {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("Name", Name[i]);
            temp.put("Kingdom", Kingdom[i]);
            temp.put("gender",gender[i]);
            temp.put("birth", birth[i]);
            temp.put("death", death[i]);
            temp.put("native_place", native_place[i]);
            temp.put("image", image_list[i]);
            Informations.add(temp);
        }
    }
}
