package edu.boisestate.kgross.volleyballtournamentscorekeeper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by kgross on 11/22/2015.
 */
public class GamePagerActivity extends AppCompatActivity implements GameFragment.CallBacks {
    private static final String EXTRA_GAME_ID = "edu.boisestate.kgross.volleyballtournamentscorekeeper.crime_id";
    private ViewPager mViewPager;
    private List<Game> mGames;


    public void onGameUpdated(Game game){

    }

    public static Intent newIntent(Context packageContext, UUID gameID) {
        Intent intent = new Intent(packageContext, GamePagerActivity.class);
        intent.putExtra(EXTRA_GAME_ID, gameID);
        return intent;
    }


    public void onCrimeUpdated(Game game) {


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_pager);
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_GAME_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_game_pager_view_pager);
        mGames = GameList.get(this).getGames();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Game game = mGames.get(position);
                return GameFragment.newInstance(game.getId());
            }

            @Override
            public int getCount() {

                return mGames.size();
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }


            @Override
            public void onPageSelected(int position) {
                Game game = mGames.get(position);
                if (game.getGame() != null) {
                    setTitle(game.getGame());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });


        for (int i = 0; i < mGames.size(); i++) {
            if (mGames.get(i).equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}


