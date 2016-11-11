package edu.boisestate.kgross.volleyballtournamentscorekeeper;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kgross on 11/22/2015.
 */
public class GameListFragment extends Fragment {
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView mGameRecyclerView;
    private GameAdapter mAdapter;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;

    }
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_game:
                Game game = new Game();
                GameList.get(getActivity()).addGame(game);
                //Intent intent=CrimePagerActivity.newIntent(getActivity(),crime.getId());
                //startActivity(intent);
                updateUI();
                mCallbacks.onGameSelected(game);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible=!mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void updateSubtitle() {
        GameList list = GameList.get(getActivity());
        int crimeCount = list.getGames().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);
        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_game_list, menu);
        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        GameList list = GameList.get(getActivity());
        List<Game> games = list.getGames();
        if (mAdapter == null) {
            mAdapter = new GameAdapter(games);
            mGameRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setGames(games);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_game_list, container, false);
        mGameRecyclerView= (RecyclerView) view.findViewById(R.id.game_recycler_view);
        mGameRecyclerView.setLayoutManager((new LinearLayoutManager(getActivity())));

        if (savedInstanceState!=null){
            mSubtitleVisible=savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();
        return view;
    }
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    public interface Callbacks {
        void onGameSelected(Game game);
    }


    private class GameHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mTeam1;
        private TextView mTeam2;
        private CheckBox mSolvedCheckBox;
        private Game mGame;

        public GameHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_game_title_text_view);
            mTeam1 = (TextView) itemView.findViewById(R.id.list_item_game_score1_text_view);
            mTeam2 = (TextView) itemView.findViewById(R.id.list_item_game_score2_text_view);
        }

        public void bindGame(Game game) {
            mGame = game;
            mTitleTextView.setText(mGame.getGame());
            mTeam1.setText(String.valueOf(mGame.getScore1()));
            mTeam2.setText(String.valueOf(mGame.getScore2()));
        }

        @Override
        public void onClick(View v) {
            //  Intent intent=CrimePagerActivity.newIntent(getActivity(),mCrime.getId());
            // startActivity(intent);
            mCallbacks.onGameSelected(mGame);
        }
    }


    private class GameAdapter extends RecyclerView.Adapter<GameHolder>{
        private List<Game> mGames;
        public GameAdapter(List<Game> games){
            mGames=games;
        }
        @Override
        public GameHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view= layoutInflater.inflate(android.R.layout.simple_list_item_1,parent, false);
            return new GameHolder(view);
        }
        @Override
        public void onBindViewHolder(GameHolder holder, int position){
            Game game = mGames.get(position);
            holder.mTitleTextView.setText(game.getGame());
        }
        @Override
        public int getItemCount(){
            return mGames.size();
        }
        public void setGames(List<Game> games){
            mGames=games;
        }

    }

}
