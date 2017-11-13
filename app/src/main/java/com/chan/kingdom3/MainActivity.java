package com.chan.kingdom3;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    List<Map<String, Object>> CharacterList = new ArrayList<>();
    SimpleAdapter simpleAdapter;
    ListView characters;
    Bitmap[] image_list = new Bitmap[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //把Resource转为bitmap的list
        fill_image_list();

        //创建数据库
        Connector.getDatabase();
        fill_database();

        //初始化商品列表
        initCharacterList();

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
        simpleAdapter.setViewBinder(new ImageView_Bitmap_Binder());
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
                int chose_id = (int) CharacterList.get(i).get("ID");
                Intent intent = new Intent(MainActivity.this, detail.class);
                intent.putExtra("ID", chose_id);
                startActivity(intent);
            }
        });
    }

    //把Resources都换成bitmap
    void fill_image_list(){
        int[] ImageID = {R.drawable.liubei,R.drawable.guanyu, R.drawable.zhangfei, R.drawable.zhugeliang, R.drawable.zhaoyun,
                R.drawable.caochao, R.drawable.sunquan, R.drawable.simayi, R.drawable.wanglang, R.drawable.huangai};
        for(int i = 0; i < 10; i++){
            Bitmap tmp_mp = BitmapFactory.decodeResource(getResources(), ImageID[i]);
            image_list[i] = tmp_mp;
        }
    }
    //bitmap转为字节流
    private byte[] bmTObyte(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    //填充数据库的初始值
    void fill_database(){
        String[] Name = getResources().getStringArray(R.array.character_names);
        String[] Kingdom = getResources().getStringArray(R.array.Kingdoms);
        String[] gender = getResources().getStringArray(R.array.gender);
        String[] birth = getResources().getStringArray(R.array.birth);
        String[] death = getResources().getStringArray(R.array.death);
        String[] native_place = getResources().getStringArray(R.array.native_place);

        for(int i = 0; i < 10; i++){
            character temp_c = new character();
            temp_c.setName(Name[i]);
            temp_c.setKingdom(Kingdom[i]);
            temp_c.setGender(gender[i]);
            temp_c.setBirth(birth[i]);
            temp_c.setDeath(death[i]);
            temp_c.setNative_place(native_place[i]);
            temp_c.setImage(bmTObyte(image_list[i]));
            temp_c.save();
        }
    }
    //商品列表在此初始化
    private void initCharacterList()
    {
        List<character> Chs = DataSupport.findAll(character.class);
        for(int i = 0; i < 10; i++)
        {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("ID", Chs.get(i).getId());
            temp.put("image", BitmapFactory.decodeByteArray(Chs.get(i).getImage(), 0, Chs.get(i).getImage().length));
            temp.put("name", Chs.get(i).getName());
            temp.put("Kingdoms", Chs.get(i).getKingdom());
            CharacterList.add(temp);
        }
    }
}
