package edu.boisestate.kgross.volleyballtournamentscorekeeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ScoreKeeper extends AppCompatActivity implements View.OnClickListener {

    Button mSimple;
    Button mMultiple;
    Button mTourney;
    Button mSeed;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scorekeeper);


       mSimple=(Button) findViewById(R.id.simple);
       mSimple.setOnClickListener(this);

        mMultiple=(Button)findViewById(R.id.multi);
        mMultiple.setOnClickListener(this);


        mTourney=(Button)findViewById(R.id.tournament);
        mTourney.setOnClickListener(this);

        mSeed=(Button)findViewById(R.id.pool);
        mSeed.setOnClickListener(this);

    }
    @Override
    public void onClick(View v){
        if (v==mSimple){
            Intent i=new Intent(ScoreKeeper.this, SingleGame.class);
           startActivity(i);
            // simple button
        }
        if (v==mMultiple){
            Intent i=new Intent(ScoreKeeper.this, GameListActivity.class);
            startActivity(i);
        }
        if (v==mTourney){
            //tournament
        }
        if (v==mSeed){
            //tournament with seed
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_score_keeper, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
