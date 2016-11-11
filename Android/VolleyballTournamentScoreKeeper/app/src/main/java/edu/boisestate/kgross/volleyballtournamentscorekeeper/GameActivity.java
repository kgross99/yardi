package edu.boisestate.kgross.volleyballtournamentscorekeeper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.UUID;

/**
 * Created by kgross on 11/22/2015.
 */
public class GameActivity extends SingleFragmentActivity {

    private static final String EXTRA_GAME_ID = "edu.boisestate.kgross.ScoreKeeper.game_id";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment=fm.findFragmentById(R.id.fragment_container);

        if (fragment==null){
            fragment = new GameFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

    }

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, GameActivity.class);
        intent.putExtra(EXTRA_GAME_ID, crimeId);
        return intent;
    }

    protected Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_GAME_ID);
        return GameFragment.newInstance(crimeId);
    }

}



