package com.wkp.randomlayoutdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wkp.randomlayout.FlyLayout;
import com.wkp.randomlayout.RandomLayout;

public class MainActivity extends AppCompatActivity {
    String[] tag1 = new String[]{
            "博鳌亚洲论坛", "哈佛商业评论", "财经国家周刊", "每日经济新闻", "中国企业家", "路透中文网", "国际金融报",
            "中国证券网", "中国经营报", "经济观察报", "中国经济网",
            "印度去年四季度GDP增7.3% 领跑全", "大年初一全国电影总票房6.6亿元刷新纪", "都说爱钱如命 你知道人民币上的姑娘是谁",
            "中国最美女富豪身价过百亿 马云曾千里寻", "100万在世界各国能买什么房 最后一张", "一个捡破烂的！他三年竟然赚了270万"};
    String[] tag2 = new String[]{   "FT中文网", "财经网", "创业家", "福布斯", "美通社", "21世纪经济报道", "华尔街见闻", "中国黄金交易网",
            "CCTV证券资讯网", "中国发展研究基金会", "证券日报", "中国民族证券", "新财富杂志", "环球企业家", "中国证券报",
            "证券时报网", "易三板", "中国金融网", "易三板", "未央网", "商学院", "欧美股市大跌 金价创八个月来新高",
            "证券市场周刊", "财新网", "华夏时报", "第一财经",
            "中国货币政策拐点或已到来", "春运最拥挤火车站公布 北京西站未进前十", "日本拟建1700米高“天空之城” 超越"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FlyLayout flyLayout = (FlyLayout) findViewById(R.id.rl);
        flyLayout.setData(tag1,tag2);
        flyLayout.setOnFlyEverythingListener(new FlyLayout.OnFlyEverythingListener() {
            @Override
            public void onItemClick(View view, int position, String text) {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd(RandomLayout randomLayout, int animationCount) {
                Log.d("test", "randomLayout:" + randomLayout);
            }
        });
    }
}
