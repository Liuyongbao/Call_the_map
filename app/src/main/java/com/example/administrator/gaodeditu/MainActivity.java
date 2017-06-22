package com.example.administrator.gaodeditu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.gaodeditu.R.id.location;

public class MainActivity extends AppCompatActivity implements AMap.InfoWindowAdapter {

    private MapView mapView;
    private AMap aMap;
    private android.app.ActionBar actionBar;
    private UiSettings uiSettings;
    private LatLng latLng;
    private View v;
    private ListView lv;
    private Marker currentMarker;
    private AlertDialog alertDialog;
    private boolean value=true;
    private ArrayList<String> list;
    private LatLng mylatlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //做一个Dialog的View页面
        v = LayoutInflater.from(this).inflate( R.layout.navlist, null,false);
        lv = (ListView) v.findViewById(R.id.ls);

        actionBar = getActionBar();
        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (currentMarker!=null&&currentMarker.isInfoWindowShown()){
                    if (alertDialog!=null&&alertDialog.isShowing()){
                        alertDialog.dismiss();
                    }
                    currentMarker.hideInfoWindow();
                }
            }
        });
        //用Amap的实例去调用UiSettings这个类，然后通过UiSettings的实例调用方法
        uiSettings = aMap.getUiSettings();
        //将缩放的图标隐藏
        uiSettings.setZoomControlsEnabled(false);
        //将“高德地图”的Logo隐藏
        uiSettings.setLogoBottomMargin(-300);

        //获取MarkerOptions的实例
        MarkerOptions markerOptions = new MarkerOptions();
        //设置标记的图标
//        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
        //设置标记的坐标 参数（纬度，经度，多数填true）
        latLng = new LatLng(39.915212, 116.403909, true);
        markerOptions.alpha(0.8f)//设置标记物的透明度
//                .icon(bitmapDescriptor)//设置标记物的图标
                .position(latLng)//设置标记物的坐标
                .visible(true)//是否显示
                .title("天安门")//标记的标题
                .snippet("地址：北京市东城区东长安街");//标记的内容

        aMap.addMarker(markerOptions);
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                //调起百度地图
//                Intent intent;
//                if (isAvilible(MainActivity.this, "com.baidu.BaiduMap")) {//传入指定应用包名
//
//                    try {
//                        intent = Intent.getIntent("intent://map/direction?origin=latlng:40.238264,116.132021|name:北京工商管理专修学院&destination=天安门&mode=driving®ion=北京&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
////                        intent = Intent.getIntent("intent://map/direction?" +
////                                //"origin=latlng:"+"34.264642646862,108.95108518068&" +   //起点  此处不传值默认选择当前位置
////                                "destination=latlng:"+location[0]+","+location[1]+"|name:我的目的地"+        //终点
////                                "&mode=driving&" +          //导航路线方式
////                                "region=北京" +           //
////                                "&src=慧医#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
//                        MainActivity.this.startActivity(intent); //启动调用
//                    } catch (URISyntaxException e) {
//                        Log.e("intent", e.getMessage());
//                    }
//                } else {//未安装
//                    //market为路径，id为包名
//                    //显示手机上所有的market商店
//                    Toast.makeText(MainActivity.this, "您尚未安装百度地图", Toast.LENGTH_LONG).show();
//                    Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
//                    intent = new Intent(Intent.ACTION_VIEW, uri);
//                    MainActivity.this.startActivity(intent);
//                }

//                //调起高德地图
//                Intent intent;
//                if (isAvilible(MainActivity.this, "com.autonavi.minimap")) {
//                    try{
//                        intent = Intent.getIntent("androidamap://navi?sourceApplication=北京工商管理专修学院&poiname=我的目的地&lat="+latLng.latitude+"&lon="+latLng.longitude+"&dev=0");
//                        MainActivity.this.startActivity(intent);
//                    } catch (URISyntaxException e)
//                    {e.printStackTrace(); }
//                }else{
//                    Toast.makeText(MainActivity.this, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
//                    Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
//                    intent = new Intent(Intent.ACTION_VIEW, uri);
//                    MainActivity.this.startActivity(intent);
//                }
                currentMarker=marker;
                return false;
            }
        });
        aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                initLv(value);
                alertDialog.show();
            }
        });
    }

    private void initLv(boolean value) {
        if (!value){
            return;
        }
        if (value){
            this.value=false;
        }
        boolean installqq = isInstallByread("com.tencent.map");
        boolean installnav = isInstallByread("com.autonavi.minimap");
        boolean installbaidu = isInstallByread("com.baidu.BaiduMap");
        list = new ArrayList<>();
        if (installqq){
            list.add("腾讯地图");
        } if (installbaidu){
            list.add("百度地图");
        } if (installnav){
            list.add("高德地图");
        }else {
            startActivity(new Intent(MainActivity.this,NavActivity.class));
            return;
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.get(position).equals("腾讯地图")){
                    if (mylatlng!=null&&latLng!=null){
                        Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("qqmap://map/routeplan?type=drive&from="+"我这里" +"&fromcoord=" + mylatlng.latitude + "," + mylatlng.longitude + "&to=" + "目的地" + "&tocoord=" + latLng.latitude + "," + latLng.longitude + "&policy=0&referer=appName"));
                        startActivity(naviIntent);
                    }
                }else if (list.get(position).equals("百度地图")){
                    if (mylatlng != null && latLng != null) {
                        // 百度地图
                        Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("baidumap://map/geocoder?location=" + latLng.latitude + "," + latLng.longitude));
                        startActivity(naviIntent);
                    }
                }else if (list.get(position).equals("高德地图")){
                    if (mylatlng != null && latLng != null) {
                        // 高德地图
                        Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("androidamap://route?sourceApplication=appName&slat=&slon=&sname=我的位置&dlat=" + latLng.latitude + "&dlon=" + latLng.longitude + "&dname=目的地&dev=0&t=2"));
                        startActivity(naviIntent);
                    }
                }
            }
        });
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setView(lv);
        alertDialog=builder.create();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 关联actionbar到布局中
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case location:
                MyLocationStyle myLocationStyle;
                myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
                myLocationStyle.interval(2000000000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
                myLocationStyle.showMyLocation(true);
//                myLocationStyle.anchor(0.0f,0.0f);
                aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
                aMap.getUiSettings().setMyLocationButtonEnabled(true);
                aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
                aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        mylatlng = new LatLng(latitude, longitude);
                    }
                });
                break;
            case R.id.in_door_info:
//                aMap.showBuildings(true);
                aMap.showIndoorMap(true);
                break;
            case R.id.night:
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);
                break;
            case R.id.nav:
                aMap.setMapType(AMap.MAP_TYPE_NAVI);
                break;
            case R.id.normal:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                break;
            case R.id.satellate:
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.bus:
                aMap.setMapType(AMap.MAP_TYPE_BUS);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * 检查手机上是否安装了指定的软件
     * @param context
     * @param packageName：应用包名
     * @return
             */
    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.info_window, null, false);
        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
    /**
     * 判断是否安装目标应用
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }
}
