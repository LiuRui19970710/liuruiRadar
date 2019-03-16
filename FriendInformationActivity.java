package com.liurui.project.liuruiapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by jszx on 2018/12/24.
 */

public class FriendInformationActivity extends AppCompatActivity {

    TextView friend_number;
    TextView friend_long_lang;
    TextView friend_name;
    Button friends_list_btn;
    Button radar_btn;
    Button enemies_btn;
    Button delete_btn;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_detail);

        Intent intent=getIntent();
        id=intent.getIntExtra("id",0);

        friend_number=(TextView)findViewById(R.id.txt_friend_number);
        friend_number.setText(MainActivity.friends.get(id).getNumber());
        friend_long_lang=(TextView)findViewById(R.id.txt_friend_long_lang);
        friend_long_lang.setText(MainActivity.friends.get(id).getLongitude()+" "+MainActivity.friends.get(id).getLatitude());
        friend_name=(TextView)findViewById(R.id.txt_friend_name);
        friend_name.setText(MainActivity.friends.get(id).getName());

        friends_list_btn=(Button)findViewById(R.id.btn_friends_list);
        friends_list_btn.setOnClickListener(new mClick());

        radar_btn=(Button)findViewById(R.id.btn_radar);
        radar_btn.setOnClickListener(new mClick());

        enemies_btn=(Button)findViewById(R.id.btn_enemies);
        enemies_btn.setOnClickListener(new mClick());

        delete_btn=(Button)findViewById(R.id.btn_delete);
        delete_btn.setOnClickListener(new deleteClick());
    }

    private class mClick implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.btn_friends_list:
                    intent.setClass(FriendInformationActivity.this, FriendListActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_radar:
                    intent.setClass(FriendInformationActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_enemies:
                    intent.setClass(FriendInformationActivity.this, EnemyListActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }
    private class deleteClick implements View.OnClickListener {
        TextView friend_number_txt;
        AlertDialog.Builder customizeDialog;
        AlertDialog dialog;
        Button dialog_ok_btn;
        Button dialog_close_btn;
        public void onClick(View v) {
            customizeDialog =
                    new AlertDialog.Builder(FriendInformationActivity.this);
            final View dialogView = LayoutInflater.from(FriendInformationActivity.this)
                    .inflate(R.layout.dialog_delete_friend, null);
            friend_number_txt = (TextView) dialogView.findViewById(R.id.txt_friend_number);
            friend_number_txt.setText(MainActivity.friends.get(id).getNumber());

            dialog_ok_btn=(Button)dialogView.findViewById(R.id.btn_dialog_ok);
            dialog_ok_btn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            MainActivity.friends.remove(id);
                            FriendCollectOperation friendCollectOperation =new FriendCollectOperation();
                            friendCollectOperation.save(FriendInformationActivity.this,MainActivity.friends);

                            Intent intent = new Intent();
                            intent.setClass(FriendInformationActivity.this, FriendListActivity.class);
                            startActivity(intent);

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
}
