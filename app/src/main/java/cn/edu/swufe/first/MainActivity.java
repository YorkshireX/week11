package cn.edu.swufe.first;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final String TAG="main";

    TextView score;
    TextView scoreB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG,"onCreate:");

        score = (TextView) findViewById(R.id.score);
        scoreB = (TextView) findViewById(R.id.scoreB);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        String scorea=((TextView)findViewById(R.id.score)).getText().toString();
        String scoreb=((TextView)findViewById(R.id.scoreB)).getText().toString();

        Log.i(TAG,"onSaveInstanceState:");
        outState.putString("teama_score",scorea);
        outState.putString("teamb_score",scoreb);

    }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        String scorea=savedInstanceState.getString("teama_score");
        String scoreb=savedInstanceState.getString("teamb_score");

        Log.i(TAG,"onRestoreInstanceState:");
      ((TextView)findViewById(R.id.score)).setText(scorea);
      ((TextView)findViewById(R.id.scoreB)).setText(scoreb);

  }

    @Override
    protected void onStart(){
        super.onStart();
        Log.i(TAG,"onStart:");
    }


    public void btn_1(View btn){
        if(btn.getId()==R.id.btn_1){
            showScore(1);}
        else{
            showScoreB(1);
        }
    }

    public void btn_2(View btn){
        if(btn.getId()==R.id.btn_2){
            showScore(2);}
        else{
            showScoreB(2);
        }}

    public void btn_3(View btn){
        if(btn.getId()==R.id.btn_3){
            showScore(3);}
        else{
            showScoreB(3);
        }
    }

    public void btn_reset(View btn){
        score.setText("0");
        scoreB.setText("0");
    }

    private void showScore(int inc) {
        Log.i("show", "inc=" + inc);
        String oldScore = (String) score.getText();
        int newScore = Integer.parseInt(oldScore) + inc;
        score.setText("" + newScore);
    }

    private void showScoreB(int inc){
        Log.i("show","inc="+inc);
        String oldScore= (String) scoreB.getText();
        int newScore=Integer.parseInt(oldScore)+inc;
        scoreB.setText(""+newScore);
    }

}
