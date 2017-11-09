package com.chan.kingdom3;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    List<Map<String, Object>> CharacterList = new ArrayList<>();
    SimpleAdapter simpleAdapter;
    ListView characters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCharacterList();//初始化商品列表

        //发送随机推荐
//        final int[] imageID = {R.mipmap.enchatedforest, R.mipmap.arla, R.mipmap.devondale, R.mipmap.kindle,
//                R.mipmap.waitrose, R.mipmap.mcvitie, R.mipmap.ferrero, R.mipmap.maltesers, R.mipmap.lindt,
//                R.mipmap.borggreve};
//        Random random = new Random();
//        int noti_choice = random.nextInt(GoodsList.size());
//        Bundle random_recommand_bundle = new Bundle();
//        random_recommand_bundle.putString("name", GoodsList.get(noti_choice).get("name").toString());
//        random_recommand_bundle.putString("price", GoodsList.get(noti_choice).get("price").toString());
//        random_recommand_bundle.putInt("icon", imageID[noti_choice]);
//        Intent intentBroadcast = new Intent("static_action");
//        intentBroadcast.putExtras(random_recommand_bundle);
//        sendBroadcast(intentBroadcast);

        //词典主界面的对话框
        final AlertDialog.Builder characterlist_alertdialog = new AlertDialog.Builder(this);
        characterlist_alertdialog.setTitle("移除商品")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        //词典主界面用ListView和SimpleAdapter
        characters = (ListView) findViewById(R.id.characterlist);
        simpleAdapter = new SimpleAdapter(this, CharacterList,R.layout.characterlist_layout,new String[]{"image","name", "Kingdoms"},new int[]{R.id.character_image,R.id.name,R.id.Kingdoms});
        characters.setAdapter(simpleAdapter);
        characters.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos= position;
                characterlist_alertdialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CharacterList.remove(pos);
                        simpleAdapter.notifyDataSetChanged();
                    }
                }).setMessage("从购物车移除"+CharacterList.get(pos).get("name")+"?")
                        .create()
                        .show();
                return true;
            }
        });
        characters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String chose_name = CharacterList.get(i).get("name").toString();
                Intent intent = new Intent(MainActivity.this, detail.class);
                intent.putExtra("goodsName", chose_name);
                startActivity(intent);
            }
        });
    }

    //商品列表在此初始化
    private void initCharacterList()
    {
        int[] ImageID = {R.drawable.liubei, R.drawable.guanyu, R.drawable.zhangfei, R.drawable.zhugeliang, R.drawable.zhaoyun,
                            R.drawable.caochao, R.drawable.sunquan, R.drawable.simayi, R.drawable.wanglang, R.drawable.huangai};
        String[] Names = getResources().getStringArray(R.array.charater_names);
        String[] Kingdoms = getResources().getStringArray(R.array.Kingdoms);
        for(int i = 0; i < 10; i++)
        {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("image", ImageID[i]);
            temp.put("name", Names[i]);
            temp.put("Kingdoms", Kingdoms[i]);
            CharacterList.add(temp);
        }
    }
}
