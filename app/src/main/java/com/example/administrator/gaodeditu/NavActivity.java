package com.example.administrator.gaodeditu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.example.administrator.gaodeditu.listener.MyAMapNaviListener;
import com.example.administrator.gaodeditu.util.TTSController;

import java.util.ArrayList;
import java.util.List;

public class NavActivity extends AppCompatActivity implements AMapNaviViewListener{
    AMapNaviView mAMapNaviView;
    private AMapNavi mAMapNavi;
    private String navitype;
    private LatLng mylocation;
    private LatLonPoint deslocation;
    protected TTSController mTtsManager;
    private MyAMapNaviListener myAMapNaviListener;
    private List<NaviLatLng> sList;
    private List<NaviLatLng> eList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        //实例化语音引擎
//        mTtsManager = TTSController.getInstance(getApplicationContext());
//        mTtsManager.init();
        initIntentInfo();

        //算路终点坐标
        NaviLatLng mEndLatlng = new NaviLatLng(deslocation.getLatitude(),deslocation.getLongitude());
        //算路起点坐标
        NaviLatLng mStartLatlng = new NaviLatLng(mylocation.latitude, mylocation.longitude);
        //存储算路起点的列表
        sList = new ArrayList<NaviLatLng>();
        //存储算路终点的列表
        eList = new ArrayList<NaviLatLng>();
        //获取 AMapNaviView 实例
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        myAMapNaviListener = new MyAMapNaviListener() {
            @Override
            public void myOnInitNaviSuccess() {
                if ("自动".equals(navitype)){
                    driveNav();
                }
                if ("驾车".equals(navitype)){
                    driveNav();
                }
                if ("骑行".equals(navitype)){
                    mAMapNavi.calculateRideRoute(new NaviLatLng(mylocation.latitude, mylocation.longitude), new NaviLatLng(deslocation.getLatitude(),deslocation.getLongitude()));
                }
                if ("步行".equals(navitype)){
                    mAMapNavi.calculateWalkRoute(new NaviLatLng(mylocation.latitude, mylocation.longitude), new NaviLatLng(deslocation.getLatitude(),deslocation.getLongitude()));
                }
            }

            @Override
            public void myOnCalculateRouteSuccess() {
                if ("自动".equals(navitype)){
                    mAMapNavi.startNavi(NaviType.EMULATOR);
                }
                if ("驾车".equals(navitype)){
                    mAMapNavi.startNavi(NaviType.GPS);
                }
                if ("骑行".equals(navitype)){
                    mAMapNavi.startNavi(NaviType.EMULATOR);
                }
                if ("步行".equals(navitype)){
                    mAMapNavi.startNavi(NaviType.EMULATOR);
                }

            }

        };

        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        AMapNaviViewOptions viewOptions = mAMapNaviView.getViewOptions();
        viewOptions.setLayoutVisible(true);
        viewOptions.setLaneInfoShow(true);
        viewOptions.setAutoChangeZoom(true);
        viewOptions.setAutoDrawRoute(true);
        viewOptions.setTrafficLine(true);
        //获取AMapNavi实例
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        //设置模拟导航的行车速度
        mAMapNavi.setEmulatorNaviSpeed(75);

        sList.add(mStartLatlng);
        eList.add(mEndLatlng);//添加监听回调，用于处理算路成功

        mAMapNavi.addAMapNaviListener(new MyAMapNaviListener() {
            @Override
            public void myOnInitNaviSuccess() {
                /**
                 * 方法:
                 *   int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
                 * 参数:
                 * @congestion 躲避拥堵
                 * @avoidhightspeed 不走高速
                 * @cost 避免收费
                 * @hightspeed 高速优先
                 * @multipleroute 多路径
                 *
                 * 说明:
                 *      以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
                 * 注意:
                 *      不走高速与高速优先不能同时为true
                 *      高速优先与避免收费不能同时为true
                 */
                int strategy=0;
                try {
                    strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mAMapNavi.calculateDriveRoute(sList, eList, null, strategy);
            }

            @Override
            public void myOnCalculateRouteSuccess() {
                mAMapNavi.startNavi(NaviType.EMULATOR);
            }
        });
    }
    private void driveNav() {
        /**
         * 方法:
         *   int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
         * 参数:
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         * 说明:
         *      以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         * 注意:
         *      不走高速与高速优先不能同时为true
         *      高速优先与避免收费不能同时为true
         */
        int strategy = 0;
        try {
            strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAMapNavi.calculateDriveRoute(sList, eList, null, strategy);
    }

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviCancel() {

    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public void onNaviMapMode(int i) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onLockMap(boolean b) {

    }

    @Override
    public void onNaviViewLoaded() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
    }
    private void initIntentInfo() {
        Intent intent = getIntent();
        if(intent!=null){
            Bundle bundle = intent.getBundleExtra("bundle");
            mylocation = bundle.getParcelable("我的坐标");
            deslocation = bundle.getParcelable("目的地");
            navitype = bundle.getString("navitype");

        }


    }
}
