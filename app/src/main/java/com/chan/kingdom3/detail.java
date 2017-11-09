package com.chan.kingdom3;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initInformation();

        Bundle extras = this.getIntent().getExtras();
        if(extras != null)
        {
            data = extras.getString("goodsName");
            for(int i = 0; i < Informations.size(); i++)
            {
                if(Informations.get(i).get("goodName").toString().equals(data))
                {
                    item_NO = i;
                }
            }
        }

        final int[] imageID = {R.mipmap.enchatedforest, R.mipmap.arla, R.mipmap.devondale, R.mipmap.kindle,
                R.mipmap.waitrose, R.mipmap.mcvitie, R.mipmap.ferrero, R.mipmap.maltesers, R.mipmap.lindt,
                R.mipmap.borggreve};
        ImageView goodsimage = (ImageView)findViewById(R.id.image_detail);
        int resId = imageID[item_NO];
        goodsimage.setImageResource(resId);

        TextView PriceView = (TextView)findViewById(R.id.price_in_detail);
        PriceView.setText(Informations.get(item_NO).get("Price").toString());

        final TextView InfoView = (TextView)findViewById(R.id.info);
        InfoView.setText(Informations.get(item_NO).get("infoType").toString()+" "+Informations.get(item_NO).get("info").toString());

        ImageButton BackButton = (ImageButton)findViewById(R.id.back_button);
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView goods_name = (TextView)findViewById(R.id.goods_name);
        goods_name.setText(data);

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

        ImageButton shoppingCart = (ImageButton)findViewById(R.id.shopping);
        shoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choiceName = Informations.get(item_NO).get("goodName").toString();
                choicePrice = Informations.get(item_NO).get("Price").toString();
                count++;

                Toast.makeText(detail.this,"商品已添加到购物车",Toast.LENGTH_SHORT).show();

                Bundle add_goods_bundle = new Bundle();
                add_goods_bundle.putString("name", choiceName);
                add_goods_bundle.putInt("icon", imageID[item_NO]);
                Intent intentBroadcast = new Intent(DYNAMICACTION);
                intentBroadcast.putExtras(add_goods_bundle);
                sendBroadcast(intentBroadcast);

//                EventBus.getDefault().post(new MessageEvent(choiceName, choicePrice));
            }
        });

        List<Map<String,Object>> MoreList = new ArrayList<>();
        String[] Message = {"一键下单","分享商品","不感兴趣","查看更多商品促销信息"};
        for(int i = 0; i < 4; i++)
        {
            Map<String,Object> temp = new LinkedHashMap<>();
            temp.put("message", Message[i]);
            MoreList.add(temp);
        }
        ListView MoreListView = (ListView)findViewById(R.id.more);
        SimpleAdapter moreListAdapter = new SimpleAdapter(this, MoreList,R.layout.more_list_layout,new String[]{"message"},new int[]{R.id.more_message});
        MoreListView.setAdapter(moreListAdapter);
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    void initInformation()
    {
        String[] goodName = new String[]{"Enchated Forest", "Arla Milk", "Devondale Milk", "Kindle Oasis", "waitrose 早餐麦片",
                "Mcvitie's 饼干", "Ferrero Rocher","Maltesers","Lindt","Borggreve"};
        String[] Price = new String[]{"¥ 5.00", "¥ 59.00", "¥ 79.00", "¥ 2399.00", "¥ 179.00",
                "¥ 14.90", "¥ 132.59","¥ 141.43","¥ 139.43","¥ 28.90"};
        String[] infoType = new String[]{"作者","产地","产地","版本","重量","产地","重量","重量","重量","重量"};
        String[] info = new String[]{"Johanna Basford","德国","澳大利亚","8GB","2Kg","英国",
                "300g","118g","249g","640g"};

        for(int i = 0; i < 10; i++)
        {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("goodName", goodName[i]);
            temp.put("Price", Price[i]);
            temp.put("infoType",infoType[i]);
            temp.put("info", info[i]);
            Informations.add(temp);
        }
    }
}
