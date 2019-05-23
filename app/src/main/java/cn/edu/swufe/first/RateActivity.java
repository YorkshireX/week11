package cn.edu.swufe.first;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RateActivity extends AppCompatActivity implements Runnable{

    private final String TAG="Rate";
    private float dollarRate=1/6.7f;
    private float euroRate=1/7.5f;
    private float wonRate=590f;
    private String updateDate = "";

    EditText rmb;
    TextView show;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb=findViewById(R.id.rmb);
        show=(TextView)findViewById(R.id.showOut);

        //获取sp里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
        dollarRate=sharedPreferences.getFloat("dollar_rate",1/6.7f);
        euroRate=sharedPreferences.getFloat("euro_rate",1/7.5f);
        wonRate=sharedPreferences.getFloat("won_rate",590f);
        updateDate = sharedPreferences.getString("update_date","");

        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);


        Log.i(TAG,"onCreate: sp dollarRate=" + dollarRate);
        Log.i(TAG,"onCreate: sp euroRate=" + euroRate);
        Log.i(TAG,"onCreate: sp wonRate=" + wonRate);
        Log.i(TAG,"onCreate: sp updateDate=" + updateDate);

        //判断时间
        if(!todayStr.equals(updateDate)){
            Log.i(TAG,"onCreate:需要更新");
            //开启子线程
            Thread t = new Thread(this);
            t.start();
        }else{
            Log.i(TAG,"onCreate:不需要更新");
        }



        handler = new Handler() {
            @Override
            public void handleMessage(Message msg){
                if(msg.what==5){
                    Bundle bdl = (Bundle) msg.obj;
                    dollarRate = bdl.getFloat("dollar-rate");
                    euroRate = bdl.getFloat("euro-rate");
                    wonRate = bdl.getFloat("won-rate");

                    Log.i(TAG,"handleMessage:dollar:"+dollarRate);
                    Log.i(TAG,"handleMessage:euroRate:"+euroRate);
                    Log.i(TAG,"handleMessage:wonRate:"+wonRate);

                    //保存更新的日期
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("update_date",todayStr);
                    editor.apply();

                    Toast.makeText(RateActivity.this,"汇率已更新", Toast.LENGTH_SHORT).show();

                }
                super.handleMessage(msg);
            }
        };
    }



    public void onClick(View btn) {
        //获取用户输入内容
        String str = rmb.getText().toString();
        float r = 0;
        if (str.length() > 0) {
            r = Float.parseFloat(str);
        } else {
            //提示用户输入内容
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
        }

        if (btn.getId() == R.id.btn_dollar) {
            show.setText(String.format("%.2f",r*dollarRate));
        } else if (btn.getId() == R.id.btn_euro) {
            show.setText(String.format("%.2f",r*euroRate));
        } else if (btn.getId() == R.id.btn_won) {
            show.setText(String.format("%.2f",r*wonRate));
        }

    }

    public void openOne(View btn){
        //打开一个页面Activity
        Log.i("open","openOne");
        openConfig();

    }

    private void openConfig() {
        Intent config = new Intent(this,ConfigActivity.class);
        config.putExtra("dollar_rate_key",dollarRate);
        config.putExtra("euro_rate_key",euroRate);
        config.putExtra("won_rate_key", wonRate);

        Log.i(TAG,"openOne:dollarRate=" + dollarRate);
        Log.i(TAG,"openOne:euroRate=" + euroRate);
        Log.i(TAG,"openOne:wonRate=" + wonRate);

        startActivityForResult(config,1);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.menu_set){
            openConfig();
        }else if(item.getItemId()==R.id.open_list){
            //打开列表窗口
            Intent list = new Intent(this,RateListActivity.class);
            startActivity(list);
            //测试数据库
//            RateItem item1 = new RateItem("aaaa","123");
//            RateManager manager = new RateManager(this);
//            manager.add(item1);
//            manager.add(new RateItem("bbbb","23.5"));
//            Log.i(TAG,"onOptionItemSelected：写入数据完毕");
//
//            //查询所有数据
//            List<RateItem> textList = manager.listAll();
//            for(RateItem i : textList){
//                Log.i(TAG,"onOptionItemSelected:取出数据[id="+i.getId()+"]Name=" + i.getCurName() + "Rate=" + i.getCurRate());
//            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==1 && resultCode==2){
            Bundle bundle = data.getExtras();
            dollarRate= bundle.getFloat("key_dollar",1/6.7f);
            euroRate= bundle.getFloat("key_euro",1/7.5f);
            wonRate= bundle.getFloat("key_won",590);
            Log.i(TAG,"onActivityResult:dollarRate=" + dollarRate);
            Log.i(TAG,"onActivityResult:dollarRate=" + euroRate);
            Log.i(TAG,"onActivityResult:dollarRate=" + wonRate);

            //将新设置的汇率写到sp里
            SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.commit();
            Log.i(TAG,"onActivityResult:数据已保存到sharedPreferences");

        }

        super.onActivityResult(requestCode,resultCode,data);
    }

    public void run(){
        Log.i(TAG,"run: run()......");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //用于保存获取的汇率
        Bundle bundle = new Bundle();
        //获取网络数据
        bundle = getFromBOC();
        //bundle中保存所获取的汇率
        //获取Msg对象，用于返回主线程
        Message msg= handler.obtainMessage(5);
        msg.obj=bundle;
        handler.sendMessage(msg);


    }

    /*
    *从bankofChina获取数据
    * @return
     */

    private Bundle getFromBOC() {
        Bundle bundle = new Bundle();
        Document doc=null;
        try {
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();
            Log.i(TAG, "run: " + doc.title());
            Elements tables = doc.getElementsByTag("table");

            Element table=tables.get(1);
            //获取TD中的数据
            Elements tds = table.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=6){
                Element td1=tds.get(i);
                Element td2=tds.get(i+5);
                Log.i(TAG,"run:"+td1.text()+"==>"+td2.text());
                String str1=td1.text();
                String val=td2.text();

                if("美元".equals(str1)){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }else if("欧元".equals(str1)){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                }else if("韩元".equals(str1)){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return bundle;
    }


    private Bundle getFromUsdCny() {
        Bundle bundle = new Bundle();
        Document doc=null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Log.i(TAG, "run: " + doc.title());
            Elements tables = doc.getElementsByTag("table");

            Element table=tables.get(0);
            //获取TD中的数据
            Elements tds = table.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=6){
                Element td1=tds.get(i);
                Element td2=tds.get(i+5);
                Log.i(TAG,"run:"+td1.text()+"==>"+td2.text());
                String str1=td1.text();
                String val=td2.text();

                if("美元".equals(str1)){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }else if("欧元".equals(str1)){
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                }else if("韩元".equals(str1)){
                    bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return bundle;
    }

    private String inputString2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"gb2312");
        for(; ; ){
            int rsz = in.read(buffer,0,buffer.length);
            if(rsz<0)
                break;
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }
}
