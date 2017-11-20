package com.chan.kingdom3;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
    public static int FROM_NEWITEM = 2;
    public static int FROM_DETAIL = 3;
    List<Map<String, Object>> CharacterList = new ArrayList<>();
    List<Map<String, Object>> searchList = new ArrayList<>();
    SimpleAdapter characterAdapter;
    SimpleAdapter searchAdapter;
    ListView characters;
    ListView searchListView;
    Bitmap[] image_list = new Bitmap[10];
    Button searchBtn;
    EditText searchEdit;
    FloatingActionButton addButton;
    Bitmap WeiBM;
    Bitmap ShuBM;
    Bitmap WuBM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WeiBM = BitmapFactory.decodeResource(getResources(), R.drawable.wei);
        ShuBM = BitmapFactory.decodeResource(getResources(), R.drawable.shu);
        WuBM = BitmapFactory.decodeResource(getResources(), R.drawable.wu);
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
        characterAdapter = new SimpleAdapter(this, CharacterList,R.layout.characterlist_layout,new String[]{"image","name", "KingdomBM", "BG"},new int[]{R.id.character_image,R.id.name,R.id.kingdom_image,R.id.char_layout});
        characterAdapter.setViewBinder(new ImageView_Bitmap_Binder());
//        characterAdapter.setViewBinder(new color_bg_Binder());
        characters.setAdapter(characterAdapter);
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
                            characterAdapter.notifyDataSetChanged();
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
                startActivityForResult(intent, FROM_DETAIL);
            }
        });

        //添加新的词条
        addButton = (FloatingActionButton) findViewById(R.id.addFAB);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: activate a new activity to add new item
                character newChar = new character();
                newChar.setImage(bmTObyte(BitmapFactory.decodeResource(getResources(), R.drawable.default_image)));
                newChar.save();
                Intent intent = new Intent(MainActivity.this, newItem.class);
                intent.putExtra("ID", newChar.getId());
                startActivityForResult(intent, FROM_NEWITEM);
            }
        });

        //搜索页面使用ListView和SimpleAdapter
        searchListView = findViewById(R.id.searchlist);
        searchAdapter = new SimpleAdapter(this, searchList,R.layout.characterlist_layout,new String[]{"image","name", "KingdomBM", "BG"},new int[]{R.id.character_image,R.id.name,R.id.kingdom_image, R.id.char_layout});
        searchAdapter.setViewBinder(new ImageView_Bitmap_Binder());
//        searchAdapter.setViewBinder(new color_bg_Binder());
        searchListView.setAdapter(searchAdapter);
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int chose_id = (int) searchList.get(i).get("ID");
                Intent intent = new Intent(MainActivity.this, detail.class);
                intent.putExtra("ID", chose_id);
                startActivityForResult(intent, FROM_DETAIL);
            }
        });
        //搜索按钮
        searchBtn = findViewById(R.id.searchBtn);
        searchEdit = findViewById(R.id.search_edittext);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchListView.getVisibility() == View.GONE){
                    List<character> guess = DataSupport.where("Name like ? or Kingdom like ?", "%"+searchEdit.getText().toString()+"%", "%"+searchEdit.getText().toString()+"%").find(character.class);
                    fillSearchList(guess);
                    searchAdapter.notifyDataSetChanged();
                    searchListView.setVisibility(View.VISIBLE);
                    characters.setVisibility(View.GONE);
                    searchBtn.setText("取消");
                }
                else{
                    searchListView.setVisibility(View.GONE);
                    characters.setVisibility(View.VISIBLE);
                    searchBtn.setText("搜索");
                }
            }
        });
    }//end onCreate

    //接受回传的信息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            if(requestCode == FROM_NEWITEM){
                int newID = data.getIntExtra("ID", -1);//没有收到ID的intent就返回-1
                character newChar = DataSupport.find(character.class, newID);
                Map<String, Object> temp = new LinkedHashMap<>();
                String Kingdom = newChar.getKingdom();
                Bitmap KingdomBM = WeiBM;
                int color = 0;
                if(Kingdom.equals("魏")){ KingdomBM = WeiBM; color = Color.parseColor("#FF7F00");}
                else if(Kingdom.equals("蜀")){ KingdomBM = ShuBM; color = Color.parseColor("#FFD700");}
                else if(Kingdom.equals("吴")){ KingdomBM = WuBM; color = Color.parseColor("#B3EE3A");}
                temp.put("ID", newID);
                temp.put("image", BitmapFactory.decodeByteArray(newChar.getImage(), 0, newChar.getImage().length));
                temp.put("name", newChar.getName());
                temp.put("Kingdoms", newChar.getKingdom());
                temp.put("KingdomBM", KingdomBM);
                temp.put("BG", color);
                CharacterList.add(temp);
                characterAdapter.notifyDataSetChanged();
            }
            else if(requestCode == FROM_DETAIL){
                int changeID = data.getIntExtra("ID", -1);
                character changeChar = DataSupport.find(character.class, changeID);
                int changeIndex = -1;
                for(int i = 0; i < CharacterList.size(); i++)
                {
                    if((int)CharacterList.get(i).get("ID") == changeID){
                        changeIndex = i;
                        break;
                    }
                }
                //Flag == true 修改, Flag == false 删除
                if(data.getBooleanExtra("Flag", true)){
                    Map<String, Object> temp = CharacterList.get(changeIndex);
                    String Kingdom = changeChar.getKingdom();
                    Bitmap KingdomBM = WeiBM;
                    int color = 0;
                    if(Kingdom.equals("魏")){ KingdomBM = WeiBM; color = Color.parseColor("#FF7F00");}
                    else if(Kingdom.equals("蜀")){ KingdomBM = ShuBM; color = Color.parseColor("#FFD700");}
                    else if(Kingdom.equals("吴")){ KingdomBM = WuBM; color = Color.parseColor("#B3EE3A");}
                    temp.put("ID", changeID);
                    temp.put("image", BitmapFactory.decodeByteArray(changeChar.getImage(), 0, changeChar.getImage().length));
                    temp.put("name", changeChar.getName());
                    temp.put("Kingdoms", changeChar.getKingdom());
                    temp.put("KingdomBM", KingdomBM);
                    temp.put("BG", color);
                    characterAdapter.notifyDataSetChanged();
                }
                else{
                    CharacterList.remove(changeIndex);
                    characterAdapter.notifyDataSetChanged();
                }
            }
        }
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

    //把Resources都换成bitmap
    void fill_image_list(){
        int[] ImageID = {R.drawable.liubei,R.drawable.guanyu, R.drawable.zhangfei, R.drawable.zhugeliang, R.drawable.zhaoyun,
                R.drawable.caochao, R.drawable.sunquan, R.drawable.simayi, R.drawable.wanglang, R.drawable.huangai};
        for(int i = 0; i < ImageID.length; i++){
            //修改图片大小 from: http://blog.csdn.net/adam_ling/article/details/52346741
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
            BitmapFactory.decodeResource(getResources(), ImageID[i], options); // 解码出图片边长
            int inSampleSize = calSampeSize(options.outHeight, options.outWidth); // 计算压缩比例
            options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
            options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
            Bitmap tmp_mp = BitmapFactory.decodeResource(getResources(), ImageID[i], options); // 解码文件
            image_list[i] = tmp_mp;
        }
    }
    //bitmap转为字节流
    private byte[] bmTObyte(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
    //武将列表在此初始化
    private void initCharacterList()
    {
        int k = DataSupport.count("character");
        List<character> Chs = DataSupport.findAll(character.class);
        for(int i = 0; i < k; i++)
        {
            Map<String, Object> temp = new LinkedHashMap<>();
            String Kingdom = Chs.get(i).getKingdom();
            Bitmap KingdomBM = WeiBM;
            int color = 0;
            if(Kingdom.equals("魏")){ KingdomBM = WeiBM; color = Color.parseColor("#FF7F00");}
            else if(Kingdom.equals("蜀")){ KingdomBM = ShuBM; color = Color.parseColor("#FFD700");}
            else if(Kingdom.equals("吴")){ KingdomBM = WuBM; color = Color.parseColor("#B3EE3A");}
            temp.put("ID", Chs.get(i).getId());
            temp.put("image", BitmapFactory.decodeByteArray(Chs.get(i).getImage(), 0, Chs.get(i).getImage().length));
            temp.put("name", Chs.get(i).getName());
            temp.put("Kingdoms", Chs.get(i).getKingdom());
            temp.put("KingdomBM", KingdomBM);
            temp.put("BG", color);
            CharacterList.add(temp);
        }
    }
    //搜索预测列表填充
    private void fillSearchList(List<character> guess){
        searchList.clear();
        for(int i = 0; i < guess.size(); i++){
            Map<String, Object> temp = new LinkedHashMap<>();
            String Kingdom = guess.get(i).getKingdom();
            Bitmap KingdomBM = WeiBM;
            int color = 0;
            if(Kingdom.equals("魏")){ KingdomBM = WeiBM; color = Color.parseColor("#FF7F00");}
            else if(Kingdom.equals("蜀")){ KingdomBM = ShuBM; color = Color.parseColor("#FFD700");}
            else if(Kingdom.equals("吴")){ KingdomBM = WuBM; color = Color.parseColor("#B3EE3A");}
            temp.put("ID", guess.get(i).getId());
            temp.put("image", BitmapFactory.decodeByteArray(guess.get(i).getImage(), 0, guess.get(i).getImage().length));
            temp.put("name", guess.get(i).getName());
            temp.put("Kingdoms", guess.get(i).getKingdom());
            temp.put("KingdomBM", KingdomBM);
            temp.put("BG", color);
            searchList.add(temp);
        }
    }
}
