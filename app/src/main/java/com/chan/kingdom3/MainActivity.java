package com.chan.kingdom3;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
    FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!DataSupport.isExist(character.class)){
            //把Resource转为bitmap的list
            fill_image_list();

            //创建数据库
            Connector.getDatabase();
            fill_database();
        }

        //初始化商品列表
        initCharacterList();

        //词典主界面的对话框
        final String[] opItem = {"删除这个人物", "修改人物信息"};
        final AlertDialog.Builder characterlist_alertdialog = new AlertDialog.Builder(this);
        characterlist_alertdialog.setTitle("操作人物词条");

        //词典主界面用ListView和SimpleAdapter
        characters = (ListView) findViewById(R.id.characterlist);
        simpleAdapter = new SimpleAdapter(this, CharacterList,R.layout.characterlist_layout,new String[]{"image","name", "Kingdoms"},new int[]{R.id.character_image,R.id.name,R.id.Kingdoms});
        simpleAdapter.setViewBinder(new ImageView_Bitmap_Binder());
        characters.setAdapter(simpleAdapter);
        characters.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos= position;
                characterlist_alertdialog.setItems(opItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            Toast.makeText(getApplication(), CharacterList.get(pos).get("name")+"已移除", Toast.LENGTH_LONG).show();
                            DataSupport.delete(character.class, (int)CharacterList.get(pos).get("ID"));
                            CharacterList.remove(pos);
                            simpleAdapter.notifyDataSetChanged();
                        }
                        else{
                            Toast.makeText(getApplication(), "还无法修改人物信息", Toast.LENGTH_LONG).show();
                        }
                    }
                }).show();
                return true;
            }
        });

        characters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int chose_id = (int) CharacterList.get(i).get("ID");
                Intent intent = new Intent(MainActivity.this, detail.class);
                intent.putExtra("ID", chose_id);
                startActivityForResult(intent, 1);
            }
        });

        addButton = (FloatingActionButton) findViewById(R.id.addFAB);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: activate a new activity to add new item
                Intent intent = new Intent(MainActivity.this, newItem.class);
                Bundle bundle = new Bundle();
                character temp_c = new character();
                temp_c.setName("");
                temp_c.setKingdom("");
                temp_c.setGender("");
                temp_c.setBirth("");
                temp_c.setDeath("");
                temp_c.setNative_place("");
                temp_c.setNickname("");
                temp_c.setProfile("");
                temp_c.setImage(bmTObyte(BitmapFactory.decodeResource(getResources(), R.drawable.default_image)));
                bundle.putSerializable("char", temp_c);
                bundle.putInt("which", 1);
                intent.putExtras(bundle);
                MainActivity.this.startActivity(intent);
            }
        });
    }//end onCreate

    //接受回传的信息
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode== 1){
            int newID = data.getIntExtra("ID", -1);//没有收到ID的intent就返回-1
            for(int i = 0; i < CharacterList.size(); i++) {
                if (CharacterList.get(i).get("ID").equals(newID)) {
                    CharacterList.remove(i);
                    DataSupport.delete(character.class, (int)CharacterList.get(i).get("ID"));
                    simpleAdapter.notifyDataSetChanged();
                }
            }
        }
        if(resultCode== 2){
            int newID = data.getIntExtra("ID", -1);//没有收到ID的intent就返回-1
            for(int i = 0; i < CharacterList.size(); i++) {
                if (CharacterList.get(i).get("ID").equals(newID)) {
                    character Chs = (character) DataSupport.findAll(character.class);
                    Map<String, Object> temp = new LinkedHashMap<>();
                    temp.put("ID", Chs.getId());
                    temp.put("image", BitmapFactory.decodeByteArray(Chs.getImage(), 0, Chs.getImage().length));
                    temp.put("name", Chs.getName());
                    temp.put("Kingdoms", Chs.getKingdom());
                    CharacterList.add(i, temp);
                }
            }
        }
    }
    //把Resources都换成bitmap
    void fill_image_list(){
        int[] ImageID = {R.drawable.liubei,R.drawable.guanyu, R.drawable.zhangfei, R.drawable.zhugeliang, R.drawable.zhaoyun,
                R.drawable.caochao, R.drawable.sunquan, R.drawable.simayi, R.drawable.wanglang, R.drawable.huangai};
        for(int i = 0; i < ImageID.length; i++){
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
        String[] nickname = getResources().getStringArray(R.array.nickname);
        String[] profile = getResources().getStringArray(R.array.profile);

        for(int i = 0; i < Name.length; i++){
            character temp_c = new character();
            temp_c.setName(Name[i]);
            temp_c.setKingdom(Kingdom[i]);
            temp_c.setGender(gender[i]);
            temp_c.setBirth(birth[i]);
            temp_c.setDeath(death[i]);
            temp_c.setNative_place(native_place[i]);
            temp_c.setImage(bmTObyte(image_list[i]));
            temp_c.setNickname(nickname[i]);
            temp_c.setProfile(profile[i]);
            temp_c.save();
        }
    }
    //商品列表在此初始化
    private void initCharacterList()
    {
        int k = DataSupport.count("character");
        List<character> Chs = DataSupport.findAll(character.class);
        for(int i = 0; i < k; i++)
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
