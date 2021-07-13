package com.example.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.example.order.utils.OnRecyclerItemClickListener;
import com.example.order.utils.RvAdapter;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements Inputtips.InputtipsListener,TextWatcher {
    MapView mMapView = null;
    AMap aMap = null;
    String newText;
    EditText editText;
    Button button;
    private Tip tip;
    Inputtips inputTips;
    private RvAdapter rvAdapter;
    private RecyclerView recyclerView;
    private AMapNavi mAMapNavi;
    private CameraUpdate cameraUpdate;
    LatLonPoint point;
    LatLng latLng;
    private int flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //定义了一个地图view
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        editText = findViewById(R.id.et_search);
        button = findViewById(R.id.start);
        editText.addTextChangedListener(this);
        newText = editText.getText().toString();
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rvAdapter = new RvAdapter(this,recyclerView,new ArrayList<>());
        recyclerView.setAdapter(rvAdapter);


        rvAdapter.setRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(int Position, List<Tip> dataList) {
                tip = dataList.get(Position);
                point = tip.getPoint();
                latLng = new LatLng(point.getLatitude(),point.getLongitude());
                //改变可视区域为指定位置

                //CameraPosition4个参数分别为位置，缩放级别，目标可视区域倾斜度，可视区域指向方向(正北逆时针算起，0-360)

                cameraUpdate= CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng,17,0,30));

                aMap.moveCamera(cameraUpdate);//地图移向指定区域
                final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(tip.getName()).snippet("DefaultMarker"));

                Toast.makeText(MapActivity.this,tip.getName(),Toast.LENGTH_SHORT).show();
                rvAdapter.list.clear();
                rvAdapter.notifyDataSetChanged();
                flag = 1;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==1){
                    Poi poi = new Poi(tip.getName(),new LatLng(point.getLatitude(),point.getLongitude()),tip.getPoiID());
                    AmapNaviParams params = new AmapNaviParams(null,null,poi, AmapNaviType.RIDE, AmapPageType.ROUTE);//AmapNaviType.RIDE导航方式为骑行，walk为步行，driver为驾驶
                    AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(),params,null);
                }else {
                    Toast.makeText(MapActivity.this,"请先输入要去的地址",Toast.LENGTH_SHORT).show();
                }

            }
        });
        inputTips = new Inputtips(MapActivity.this, (InputtipsQuery) null);
        inputTips.setInputtipsListener(this);

        mAMapNavi = AMapNavi.getInstance(this);
        mAMapNavi.setUseInnerVoice(true,false);
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(22000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。

        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.showIndoorMap(true);
        aMap.setTrafficEnabled(true);//显示实时路况图层，aMap是地图控制器对象。
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));//设置地图的显示级别
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }



    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        Tip tip = list.get(1);
        Log.e(String.valueOf(MapActivity.this), tip.getName());
        rvAdapter.setData(list);
    }
    //监听文本变化前并作出相应响应
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
    //监听文本变化时并作出相应响应
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        InputtipsQuery inputquery = new InputtipsQuery(String.valueOf(s), "");
        inputquery.setCityLimit(true);//限制在当前城市
        inputTips.setQuery(inputquery);
        inputTips.requestInputtipsAsyn();
    }
    //对文本变化后进行监听
    @Override
    public void afterTextChanged(Editable s) {

    }


}