package edu.boisestate.kgross.volleyballtournamentscorekeeper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by kgross on 11/22/2015.
 */
public class SingleGame extends AppCompatActivity implements View.OnClickListener {

    Button mTeam1;
    Button mTeam2;
    Button mRemove;
    Button mDone;
    Game game;
    Boolean remove;
    Boolean over;
    String score1;
    String score2;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_game);

        mTeam1=(Button)findViewById(R.id.team1);
        mTeam1.setOnClickListener(this);
        mTeam2=(Button)findViewById(R.id.team2);
        mTeam2.setOnClickListener(this);
        mRemove=(Button)findViewById(R.id.remove_point);
        mRemove.setOnClickListener(this);
        mDone=(Button)findViewById(R.id.game_over);
        mDone.setOnClickListener(this);
        mDone.setText("Reset");
       game=new Game();
        remove=false;
        over=false;
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

    @Override
    public void onClick(View v){
        if (v==mTeam1){
            if(!remove) {
                game.incrementTeam1();
            }else{
                game.setScore1(game.getScore1()-1);
                remove=false;
                mRemove.setBackgroundColor(1);
            }
        }
        if (v==mTeam2){
            if (!remove){
                game.incrementTeam2();
            }else {
                game.setScore2(game.getScore2() - 1);
                remove = false;
                mRemove.setBackgroundColor(1 );
            }

        }
        if (v==mRemove){
            remove=true;
            mRemove.setBackgroundColor(1980104448 );
        }
        if (v==mDone){
            over=true;
            game.setScore1(0);
            game.setScore2(0);
        }


        mTeam1.setText(String.valueOf(game.getScore1()));
        mTeam2.setText(String.valueOf(game.getScore2()));
    }
}
