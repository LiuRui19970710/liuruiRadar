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
 * Created by jszx on 2018/12/28.
 */

public class EnemyInformationActivity extends AppCompatActivity {

    TextView enemy_number;
    TextView enemy_long_lang;
    TextView enemy_name;
    Button enemies_list_btn;
    Button radar_btn;
    Button friends_btn;
    Button delete_btn;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enemy_detail);

        Intent intent=getIntent();
        id=intent.getIntExtra("id",0);

        enemy_number=(TextView)findViewById(R.id.txt_enemy_number);
        enemy_number.setText(MainActivity.enemies.get(id).getNumber());
        enemy_long_lang=(TextView)findViewById(R.id.txt_enemy_long_lang);
        enemy_long_lang.setText(MainActivity.enemies.get(id).getLongitude()+" "+MainActivity.enemies.get(id).getLatitude());
        enemy_name=(TextView)findViewById(R.id.txt_enemy_name);
        enemy_name.setText(MainActivity.enemies.get(id).getName());

        enemies_list_btn=(Button)findViewById(R.id.btn_enemies_list);
        enemies_list_btn.setOnClickListener(new mClick());

        radar_btn=(Button)findViewById(R.id.btn_radar);
        radar_btn.setOnClickListener(new mClick());

        friends_btn=(Button)findViewById(R.id.btn_friends);
        friends_btn.setOnClickListener(new mClick());

        delete_btn=(Button)findViewById(R.id.btn_delete);
        delete_btn.setOnClickListener(new deleteClick());
    }

    private class mClick implements View.OnClickListener {
        public void onClick(View v){
            Intent intent=new Intent();
            switch(v.getId())
            {
                case R.id.btn_enemies_list:
                    intent.setClass(EnemyInformationActivity.this,EnemyListActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_radar:
                    intent.setClass(EnemyInformationActivity.this,MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_friends:
                    intent.setClass(EnemyInformationActivity.this,FriendListActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    private class deleteClick implements View.OnClickListener {
        TextView enemy_number_txt;
        AlertDialog.Builder customizeDialog;
        AlertDialog dialog;
        Button dialog_ok_btn;
        Button dialog_close_btn;
        public void onClick(View v) {
            customizeDialog =
                    new AlertDialog.Builder(EnemyInformationActivity.this);
            final View dialogView = LayoutInflater.from(EnemyInformationActivity.this)
                    .inflate(R.layout.dialog_delete_enemy, null);
            enemy_number_txt = (TextView) dialogView.findViewById(R.id.txt_enemy_number);
            enemy_number_txt.setText(MainActivity.enemies.get(id).getNumber());

            dialog_ok_btn=(Button)dialogView.findViewById(R.id.btn_dialog_ok);
            dialog_ok_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MainActivity.enemies.remove(id);
                    EnemyCollectOperation enemyCollectOperation =new EnemyCollectOperation();
                    enemyCollectOperation.save(EnemyInformationActivity.this,MainActivity.enemies);

                    Intent intent = new Intent();
                    intent.setClass(EnemyInformationActivity.this, EnemyListActivity.class);
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
