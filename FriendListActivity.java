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

public class FriendListActivity extends AppCompatActivity {

    private Button friends_list_add_btn;
    private Button friends_list_radar_btn;
    private Button friends_list_enemies_btn;
    private ListView Friends_list;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);

        Friends_list=(ListView)findViewById(R.id.lvw_friends_list);
        Friends_list.setDivider(null);//去掉条目间的分割线
        Friends_list.setSelector(new ColorDrawable());//设置默认状态选择器为全透明，不传颜色就是没颜色（也就是条目被点击时，背景没颜色）
        adapter=new ListViewAdapter();
        Friends_list.setAdapter(adapter);
        Friends_list.setOnItemClickListener(new mItemClick());

        friends_list_add_btn=(Button)findViewById(R.id.btn_friends_list_add);
        friends_list_add_btn.setOnClickListener(new addClick());

        friends_list_radar_btn=(Button)findViewById(R.id.btn_friends_list_radar);
        friends_list_radar_btn.setOnClickListener(new radarClick());

        friends_list_enemies_btn=(Button)findViewById(R.id.btn_friends_list_enemies);
        friends_list_enemies_btn.setOnClickListener(new enemyClick());
    }

    public class ListViewAdapter extends BaseAdapter {

        ArrayList<View> itemViews;

        public ListViewAdapter(){
            itemViews=new ArrayList<>();
            for(int i=0;i<MainActivity.friends.size();i++)
            {
                itemViews.add(makeItemView(i));
            }
        }

        public int getCount(){
            return MainActivity.friends.size();
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
            LayoutInflater inflater = (LayoutInflater) FriendListActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 使用View的对象itemView与R.layout.item关联
            View itemView = inflater.inflate(R.layout.friends_list_item, null);
            TextView friend_name = (TextView) itemView.findViewById(R.id.name_cell);
            friend_name.setText(MainActivity.friends.get(id).getName());

            Button delete_button = (Button) itemView.findViewById(R.id.delete_button_cell);
            delete_button.setOnClickListener(new View.OnClickListener() {
                                                 TextView friend_number_txt;
                                                 AlertDialog.Builder customizeDialog;
                                                 AlertDialog dialog;
                                                 Button dialog_ok_btn;
                                                 Button dialog_close_btn;
                                                 public void onClick(View v) {
                                                     customizeDialog =
                                                             new AlertDialog.Builder(FriendListActivity.this);
                                                     final View dialogView = LayoutInflater.from(FriendListActivity.this)
                                                             .inflate(R.layout.dialog_delete_friend, null);
                                                     friend_number_txt = (TextView) dialogView.findViewById(R.id.txt_friend_number);
                                                     friend_number_txt.setText(MainActivity.friends.get(id).getNumber());

                                                     dialog_ok_btn=(Button)dialogView.findViewById(R.id.btn_dialog_ok);
                                                     dialog_ok_btn.setOnClickListener(new View.OnClickListener() {
                                                         public void onClick(View v) {
                                                             MainActivity.friends.remove(id);
                                                            itemViews.remove(id);
                                                             FriendCollectOperation friendCollectOperation =new FriendCollectOperation();
                                                             friendCollectOperation.save(FriendListActivity.this,MainActivity.friends);
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
        EditText friend_name_txt;
        EditText friend_number_txt;
        Button dialog_ok_btn;
        Button dialog_close_btn;
        AlertDialog.Builder customizeDialog;
        AlertDialog dialog;

        public void onClick(View v){
            customizeDialog =
                    new AlertDialog.Builder(FriendListActivity.this);
            final View dialogView = LayoutInflater.from(FriendListActivity.this)
                    .inflate(R.layout.dialog_add_friend,null);

            friend_name_txt=(EditText)dialogView.findViewById(R.id.txt_friend_name);
            friend_number_txt=(EditText)dialogView.findViewById(R.id.txt_friend_number);
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
                if(!friend_name_txt.getText().toString().isEmpty()&&!friend_number_txt.getText().toString().isEmpty())
                {
                    Friend friend=new Friend();
                    friend.setName(friend_name_txt.getText().toString());
                    friend.setNumber(friend_number_txt.getText().toString());
                    MainActivity.friends.add(friend);
                    FriendCollectOperation friendCollectOperation =new FriendCollectOperation();
                    friendCollectOperation.save(FriendListActivity.this,MainActivity.friends);
                    Log.i("friendsize",MainActivity.friends.size()+"");
                    adapter.addItem(MainActivity.friends.size()-1);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
                else
                    Toast.makeText(FriendListActivity.this,"wrong data",Toast.LENGTH_SHORT).show();
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
            intent.setClass(FriendListActivity.this, FriendInformationActivity.class);
            startActivity(intent);
        }
    }

    private class radarClick implements View.OnClickListener {
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(FriendListActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private class enemyClick implements View.OnClickListener {
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(FriendListActivity.this, EnemyListActivity.class);
            startActivity(intent);
        }
    }
}
