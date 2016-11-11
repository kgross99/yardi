package edu.boisestate.kgross.volleyballtournamentscorekeeper;

import android.support.v4.app.Fragment;

/**
 * Created by kgross on 11/22/2015.
 */
public class GameListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new GameListFragment();
    }
}
