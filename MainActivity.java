package com.liurui.project.liuruiapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

//la long
//LatLng cenpt = new LatLng(22.25,113.535);//设定中心点坐标
//朋友22.252964,113.536225  22.247564,113.532025
//敌人22.247364,113.538225   22.250364,113.530225
public class MainActivity extends AppCompatActivity {

    private MapView mMapView=null;
    private BaiduMap mBaidumap;
    static ArrayList<Friend> friends;
    static ArrayList<Friend> enemies;
    private Button friends_btn;
    private Button enemies_btn;
    private Button refresh_btn;
    private Button locate_btn;
    private Friend me;
    private final int ACCESS_COARSE_LOCATION_REQUEST_CODE=1;
    private final int SEND_SMS_REQUEST_CODE=2;

    private String friend_num;

    private static Handler handler;

    LatLng cenpt;

    private boolean isFirstLocation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECEIVE_SMS)!=PackageManager.PERMISSION_GRANTED)
        {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.RECEIVE_SMS
            },
                    ACCESS_COARSE_LOCATION_REQUEST_CODE);
        } else {
            initMap();
        }
    }

    public void initMap(){

        mMapView = (MapView) findViewById(R.id.mapView);

        //修改百度地图的初始位置
        mBaidumap = mMapView.getMap();

        me = new Friend();

        FriendCollectOperation friendCollectOperation =new FriendCollectOperation();
        friends= friendCollectOperation.read(MainActivity.this);
        if(friends==null)
            friends=new ArrayList<>();

        Log.i("size",friends.size()+"");

        EnemyCollectOperation enemyCollectOperation =new EnemyCollectOperation();
        enemies= enemyCollectOperation.read(getApplicationContext());
        if(enemies==null)
            enemies=new ArrayList<>();

         initLocationOption();

         refreshmap();

        friends_btn = (Button) findViewById(R.id.btn_friends);
        friends_btn.setOnClickListener(new friendlistClick());

        enemies_btn=(Button)findViewById(R.id.btn_enemies);
        enemies_btn.setOnClickListener(new enemieslistClick());

        refresh_btn = (Button) findViewById(R.id.btn_refresh);
        refresh_btn.setOnClickListener(new refreshClick());

        locate_btn=(Button)findViewById(R.id.btn_locate);
        locate_btn.setOnClickListener(new locateClick());
        SMSBroadcastReceiver RecevieMsg=new SMSBroadcastReceiver();

        IntentFilter filterSms = new IntentFilter();
        filterSms.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(RecevieMsg, filterSms);
    }

    private void refreshmap(){
        mBaidumap.clear();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle=msg.getData();
                String locate_result=bundle.getString("locate_result");
                if(locate_result.equals("ok"))
                {
                    markermap();
                }
                else if(locate_result.equals("ok")) initLocationOption();
            }
        };
}

    private void markermap(){
        if (!friends.isEmpty()) {
            for (int i = 0; i < friends.size(); i++) {
                LatLng cenpt = new LatLng(friends.get(i).getLatitude(), friends.get(i).getLongitude());//设定中心点坐标
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.friend_mar);
                //准备 marker option 添加 marker 使用
                MarkerOptions markerOption = new MarkerOptions().icon(bitmap).position(cenpt);
                //获取添加的 marker 这样便于后续的操作
                Marker marker = (Marker) mBaidumap.addOverlay(markerOption);

                List<LatLng> points = new ArrayList<LatLng>();
                points.clear();
                points.add(new LatLng(me.getLatitude(), me.getLongitude()));
                points.add(new LatLng(friends.get(i).getLatitude(), friends.get(i).getLongitude()));
                OverlayOptions ooPolyline = new PolylineOptions().width(4)
                        .color(0xFF11DA2D).points(points);
                mBaidumap.addOverlay(ooPolyline);

                cenpt = new LatLng(friends.get(i).getLatitude() - 0.0001, friends.get(i).getLongitude());
                OverlayOptions textOption = new TextOptions().bgColor(0x8000000).fontSize(52).fontColor(0xFF11DA2D).text(friends.get(i).getName()).rotate(0).position(cenpt);
                mBaidumap.addOverlay(textOption);

                cenpt = new LatLng(friends.get(i).getLatitude() - 0.0005, friends.get(i).getLongitude());
                textOption = new TextOptions().bgColor(0x8000000).fontSize(52).fontColor(0xFF11DA2D).text(friends.get(i).getNumber()).rotate(0).position(cenpt);
                mBaidumap.addOverlay(textOption);

                LatLng me_cenpt = new LatLng(me.getLatitude(), me.getLongitude());
                LatLng middle_cenpt = new LatLng((me.getLatitude() + friends.get(i).getLatitude()) / 2, (me.getLongitude() + friends.get(i).getLongitude()) / 2);
                DecimalFormat df = new DecimalFormat("#0.00");
                textOption = new TextOptions().bgColor(0x8000000).fontSize(55).fontColor(0xFF11DA2D).text(df.format(getDistance(me_cenpt, cenpt)) + "km").rotate(0).position(middle_cenpt);
                mBaidumap.addOverlay(textOption);
            }
        }

        if (!enemies.isEmpty()) {
            for (int i = 0; i < enemies.size(); i++) {
                LatLng cenpt = new LatLng(enemies.get(i).getLatitude(), enemies.get(i).getLongitude());//设定中心点坐标
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.enemy_mar);
                //准备 marker option 添加 marker 使用
                MarkerOptions markerOption = new MarkerOptions().icon(bitmap).position(cenpt);
                //获取添加的 marker 这样便于后续的操作
                Marker marker = (Marker) mBaidumap.addOverlay(markerOption);

                List<LatLng> points = new ArrayList<LatLng>();
                points.clear();
                points.add(new LatLng(me.getLatitude(), me.getLongitude()));
                points.add(new LatLng(enemies.get(i).getLatitude(), enemies.get(i).getLongitude()));
                OverlayOptions ooPolyline = new PolylineOptions().width(4)
                        .color(0xFFEB484E).points(points);
                mBaidumap.addOverlay(ooPolyline);

                cenpt = new LatLng(enemies.get(i).getLatitude() - 0.0001, enemies.get(i).getLongitude());
                OverlayOptions textOption = new TextOptions().bgColor(0x8000000).fontSize(52).fontColor(0xFFEB484E).text(enemies.get(i).getName()).rotate(0).position(cenpt);
                mBaidumap.addOverlay(textOption);

                cenpt = new LatLng(enemies.get(i).getLatitude() - 0.0005, enemies.get(i).getLongitude());
                textOption = new TextOptions().bgColor(0x8000000).fontSize(52).fontColor(0xFFEB484E).text(enemies.get(i).getNumber()).rotate(0).position(cenpt);
                mBaidumap.addOverlay(textOption);

                LatLng me_cenpt = new LatLng(me.getLatitude(), me.getLongitude());
                LatLng middle_cenpt = new LatLng((me.getLatitude() + enemies.get(i).getLatitude()) / 2, (me.getLongitude() + enemies.get(i).getLongitude()) / 2);
                DecimalFormat df = new DecimalFormat("#0.00");
                textOption = new TextOptions().bgColor(0x8000000).fontSize(55).fontColor(0xFFEB484E).text(df.format(getDistance(me_cenpt, cenpt)) + "km").rotate(0).position(middle_cenpt);
                mBaidumap.addOverlay(textOption);
            }
        }
    }

    private void initLocationOption() {
//定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        LocationClient locationClient = new LocationClient(getApplicationContext());
//声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        MyLocationListener myLocationListener = new MyLocationListener();
//注册监听函数
        locationClient.registerLocationListener(myLocationListener);
//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("gcj02");
//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
      //  locationOption.setScanSpan(1000);
        locationOption.setScanSpan(0);
//可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
//可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
//可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
//可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
//可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);

        locationClient.setLocOption(locationOption);
//开始定位
        locationClient.start();
    }

    /*实现定位回调*/
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            if(location!=null) {
                //获取纬度信息
                double latitude = location.getLatitude();
                //获取经度信息
                double longitude = location.getLongitude();
                //获取定位精度，默认值为0.0f
                float radius = location.getRadius();
                //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
                String coorType = location.getCoorType();
                //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
                int errorCode = location.getLocType();
                double altitude=location.getAltitude();

                me.setLatitude(latitude);
                me.setLongitude(longitude);

                //修改百度地图的初始位置
                BaiduMap mBaidumap = mMapView.getMap();
                cenpt = new LatLng(me.getLatitude(),me.getLongitude());//设定中心点坐标
            //    me.setLatitude(22.25);
            //    me.setLongitude(113.535);
            //    LatLng cenpt = new LatLng(22.25,113.535);//设定中心点坐标

                 MapStatus mMapStatus = new MapStatus.Builder()//定义地图状态
                .target(cenpt).zoom(17).build();;//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                mBaidumap.setMapStatus(mMapStatusUpdate);//改变地图状态
            //    Toast.makeText(MainActivity.this,location.getAddrStr(),Toast.LENGTH_LONG).show();
             //   Toast.makeText(MainActivity.this,me.getLatitude()+","+me.getLongitude(),Toast.LENGTH_SHORT).show();

               /* if (isFirstLocation) {
                    isFirstLocation = false;
                    setmarket();
                }*/
                setmarket();

                Message msg= Message.obtain();
                Bundle bundle=new Bundle();
                bundle.putString("locate_result","ok");
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }
    }

    public void setmarket()
    {
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.me);
        OverlayOptions option = new MarkerOptions()
                .position(cenpt)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaidumap.addOverlay(option);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case ACCESS_COARSE_LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMap();
                } else {
                    Toast.makeText(MainActivity.this, "你拒绝了这个权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case SEND_SMS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SendMessage(friend_num);
                } else {
                    Toast.makeText(MainActivity.this, "你拒绝了这个权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private class refreshClick implements View.OnClickListener {
        public void onClick(View v){
            if(!friends.isEmpty()){
                for(int i=0;i<friends.size();i++) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        friend_num=friends.get(i).getNumber();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_REQUEST_CODE);
                    } else {
                        SendMessage(friends.get(i).getNumber());
                    }
                }
            }
        }
    }

    public void SendMessage(String phone_num){
        String phone = phone_num;
        String context ="where are you?";
        SmsManager manager = SmsManager.getDefault();
        ArrayList<String> list = manager.divideMessage(context);  //因为一条短信有字数限制，因此要将长短信拆分
        for(String text:list){
            manager.sendTextMessage(phone, null, text, null, null);
        }
        Toast.makeText(getApplicationContext(), "发送完毕", Toast.LENGTH_SHORT).show();

    }

    public class SMSBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        //        String phone = "10086";

            String num, con;
            //读取data中存入的安全号码
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] objs = (Object[]) bundle.get("pdus");
                SmsMessage[] smsMessages = new SmsMessage[objs.length];
                for (int i = 0; i < objs.length; i++) {
                    smsMessages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
                    num = smsMessages[i].getDisplayOriginatingAddress();    //短信的号码
                    con = smsMessages[i].getDisplayMessageBody();    //短信的内容

                    Toast.makeText(context, num + "----" + con, Toast.LENGTH_SHORT).show();

                    if(con.equals("where are you?")) {
                        SmsManager manager = SmsManager.getDefault();
                        ArrayList<String> list = manager.divideMessage(me.getLatitude() + "," + me.getLongitude());  //因为一条短信有字数限制，因此要将长短信拆分
                        for (String text : list) {
                            manager.sendTextMessage(num, null, text, null, null);
                        }
                    }
                    else{
                        String[]  strs=con.split(",");
                        if(strs.length==2)
                        {
                            for (int j = 0; j < friends.size(); j++) {
                            if (num.equals(friends.get(j).getNumber())) {
                                //这里可以写一些自己的其他操作	（如包括匹配相应的号码进行操作）
                                friends.get(j).setLatitude(Double.parseDouble(strs[0]));
                                friends.get(j).setLongitude(Double.parseDouble(strs[1]));
                                FriendCollectOperation friendCollectOperation =new FriendCollectOperation();
                                friendCollectOperation.save(MainActivity.this,friends);
                                break;
                            }
                        }
                            for (int j = 0; j < enemies.size(); j++) {
                            if (num.equals(enemies.get(j).getNumber())) {
                                //这里可以写一些自己的其他操作	（如包括匹配相应的号码进行操作）
                                enemies.get(j).setLatitude(Double.parseDouble(strs[0]));
                                enemies.get(j).setLongitude(Double.parseDouble(strs[1]));
                                EnemyCollectOperation enemyCollectOperation =new EnemyCollectOperation();
                                enemyCollectOperation.save(MainActivity.this,enemies);
                                break;
                            }
                        }
                        }
                        refreshmap();
                    }
                }
                abortBroadcast();  //这里是对短信进行拦截但是并不能实现
            }
        }
    }

    private class friendlistClick implements View.OnClickListener {
        public void onClick(View v){
            Intent intent=new Intent();
            intent.setClass(MainActivity.this,FriendListActivity.class);
            startActivity(intent);
        }
    }

    private class enemieslistClick implements View.OnClickListener {
        public void onClick(View v){
            Intent intent=new Intent();
            intent.setClass(MainActivity.this,EnemyListActivity.class);
            startActivity(intent);
        }
    }

    private class locateClick implements View.OnClickListener {
        public void onClick(View v){
            initLocationOption();
            refreshmap();
        }
    }

    public double getDistance(LatLng start,LatLng end){
        double lat1=(Math.PI/180)*start.latitude;
        double lat2=(Math.PI/180)*end.latitude;
        double lon1=(Math.PI/180)*start.longitude;
        double lon2=(Math.PI/180)*end.longitude;
//doubleLat1r=(Math.PI/180)*(gp1.getLatitudeE6()/1E6);
//doubleLat2r=(Math.PI/180)*(gp2.getLatitudeE6()/1E6);
//doubleLon1r=(Math.PI/180)*(gp1.getLongitudeE6()/1E6);
//doubleLon2r=(Math.PI/180)*(gp2.getLongitudeE6()/1E6);
//地球半径
        double R=6371;
//两点间距离km，如果想要米的话，结果*1000就可以了
        double d=Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;
        return d;
    }
}

//https://blog.csdn.net/weixin_41454168/article/details/81318648
