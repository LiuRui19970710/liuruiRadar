package com.liurui.project.liuruiapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jszx on 2018/12/24.
 */

public class EnemyListActivity extends AppCompatActivity {

    private Button enemies_list_add_btn;
    private Button enemies_list_radar_btn;
    private Button enemies_list_friends_btn;
    private ListView Enemies_list;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enemies_list);
        Enemies_list=(ListView)findViewById(R.id.lvw_enemies_list);
        Enemies_list.setDivider(null);//去掉条目间的分割线
        Enemies_list.setSelector(new ColorDrawable());//设置默认状态选择器为全透明，不传颜色就是没颜色（也就是条目被点击时，背景没颜色）
        adapter=new ListViewAdapter();
        Enemies_list.setAdapter(adapter);
        Enemies_list.setOnItemClickListener(new mItemClick());

        enemies_list_add_btn=(Button)findViewById(R.id.btn_enemies_list_add);
        enemies_list_add_btn.setOnClickListener(new addClick());

        enemies_list_radar_btn=(Button)findViewById(R.id.btn_enemies_list_radar);
        enemies_list_radar_btn.setOnClickListener(new radarClick());

        enemies_list_friends_btn=(Button)findViewById(R.id.btn_enemies_list_friends);
        enemies_list_friends_btn.setOnClickListener(new friendClick());
    }

    public class ListViewAdapter extends BaseAdapter {

        ArrayList<View> itemViews;

        public ListViewAdapter(){
            itemViews=new ArrayList<>();
            for(int i=0;i<MainActivity.enemies.size();i++)
            {
                itemViews.add(makeItemView(i));
            }
        }

        public int getCount(){
            return MainActivity.enemies.size();
        }

        public View getItem(int position){
            return itemViews.get(position);
        }

        public long getItemId(int position){
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return itemViews.get(position);
        }

        public View makeItemView(final int id) {
            LayoutInflater inflater = (LayoutInflater) EnemyListActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 使用View的对象itemView与R.layout.item关联
            View itemView = inflater.inflate(R.layout.enemies_list_item, null);
            TextView friend_name = (TextView) itemView.findViewById(R.id.name_cell);
            friend_name.setText(MainActivity.enemies.get(id).getName());

            Button delete_button = (Button) itemView.findViewById(R.id.delete_button_cell);
            delete_button.setOnClickListener(new View.OnClickListener() {
                                                 TextView enemy_number_txt;
                                                 AlertDialog.Builder customizeDialog;
                                                 AlertDialog dialog;
                                                 Button dialog_ok_btn;
                                                 Button dialog_close_btn;
                                                 public void onClick(View v) {
                                                     customizeDialog =
                                                             new AlertDialog.Builder(EnemyListActivity.this);
                                                     final View dialogView = LayoutInflater.from(EnemyListActivity.this)
                                                             .inflate(R.layout.dialog_delete_enemy, null);
                                                     enemy_number_txt = (TextView) dialogView.findViewById(R.id.txt_enemy_number);
                                                     enemy_number_txt.setText(MainActivity.enemies.get(id).getNumber());

                                                     dialog_ok_btn=(Button)dialogView.findViewById(R.id.btn_dialog_ok);
                                                     dialog_ok_btn.setOnClickListener(new View.OnClickListener() {
                                                         public void onClick(View v) {
                                                             MainActivity.enemies.remove(id);
                                                             itemViews.remove(id);
                                                             EnemyCollectOperation enemyCollectOperation =new EnemyCollectOperation();
                                                             enemyCollectOperation.save(EnemyListActivity.this,MainActivity.enemies);
                                                             adapter.notifyDataSetChanged();
                                                             dialog.dismiss();
                                                         }
                                                     });

                                                     dialog_close_btn=(Button)dialogView.findViewById(R.id.btn_dialog_close);
                                                     dialog_close_btn.setOnClickListener(new View.OnClickListener() {public void onClick(View v) {dialog.dismiss();}});

                                                     customizeDialog.setView(dialogView);
                                                     customizeDialog.create();
                                                     dialog = customizeDialog.show();
                                                 }
                                             }
            );
            return itemView;
        }

        public void addItem(int id){
            itemViews.add(makeItemView(id));
        }

    }

    private class addClick implements View.OnClickListener {
        EditText enemy_name_txt;
        EditText enemy_number_txt;
        Button dialog_ok_btn;
        Button dialog_close_btn;
        AlertDialog.Builder customizeDialog;
        AlertDialog dialog;

        public void onClick(View v){
            customizeDialog =
                    new AlertDialog.Builder(EnemyListActivity.this);
            final View dialogView = LayoutInflater.from(EnemyListActivity.this)
                    .inflate(R.layout.dialog_add_enemy,null);

            enemy_name_txt=(EditText)dialogView.findViewById(R.id.txt_enemy_name);
            enemy_number_txt=(EditText)dialogView.findViewById(R.id.txt_enemy_number);
            dialog_ok_btn=(Button)dialogView.findViewById(R.id.btn_dialog_ok);
            dialog_ok_btn.setOnClickListener(new okClick());
            dialog_close_btn=(Button)dialogView.findViewById(R.id.btn_dialog_close);
            dialog_close_btn.setOnClickListener(new closeClick());

            customizeDialog.setView(dialogView);
            customizeDialog.create();
            dialog = customizeDialog.show();
        }

        private class okClick implements android.view.View.OnClickListener {
            public void onClick(View v){
                if(!enemy_name_txt.getText().toString().isEmpty()&&!enemy_number_txt.getText().toString().isEmpty())
                {
                    Friend enemy=new Friend();
                    enemy.setName(enemy_name_txt.getText().toString());
                    enemy.setNumber(enemy_number_txt.getText().toString());
                    MainActivity.enemies.add(enemy);
                    EnemyCollectOperation enemyCollectOperation =new EnemyCollectOperation();
                    enemyCollectOperation.save(EnemyListActivity.this,MainActivity.enemies);
                    Log.i("enemysize",MainActivity.enemies.size()+"");
                    adapter.addItem(MainActivity.enemies.size()-1);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
                else
                    Toast.makeText(EnemyListActivity.this,"wrong data",Toast.LENGTH_SHORT).show();
            }
        }

        private class closeClick implements View.OnClickListener {
            public void onClick(View v) {
                dialog.dismiss();
            }
        }
    }

    //id从0开始
    private class mItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3) {
            Intent intent = new Intent();
            intent.putExtra("id",arg2);
            intent.setClass(EnemyListActivity.this, EnemyInformationActivity.class);
            startActivity(intent);
        }
    }

    private class radarClick implements View.OnClickListener {
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(EnemyListActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private class friendClick implements View.OnClickListener {
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(EnemyListActivity.this, FriendListActivity.class);
            startActivity(intent);
        }
    }
}