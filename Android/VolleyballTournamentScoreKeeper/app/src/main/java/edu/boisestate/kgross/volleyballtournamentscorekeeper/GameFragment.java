package edu.boisestate.kgross.volleyballtournamentscorekeeper;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;
/**
 * Created by kgross on 11/22/2015.
 */
public class GameFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_GAME_ID="game id";
    private Button mTeam1;
    private Button mTeam2;
    private Button mRemove;
    private Button mDone;
    private Game mGame;
    private Boolean mRemovePoint;
    private Boolean mOver;
    private String mScore1;
    private String mScore2;
    private CallBacks mCallbacks;

    public interface CallBacks{
        void onGameUpdated(Game game);
    }

    public static GameFragment newInstance (UUID gameId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_GAME_ID, gameId);
        GameFragment fragment = new GameFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private void updateGame(){
        GameList.get(getActivity()).updateGame(mGame);
        mCallbacks.onGameUpdated(mGame);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (CallBacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        UUID gameId = (UUID) getArguments().getSerializable(ARG_GAME_ID);
        mGame = GameList.get(getActivity()).getGame(gameId);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game, container, false);


        mTeam1 = (Button) v.findViewById(R.id.team1);
        mTeam1.setOnClickListener(this);
        mTeam2 = (Button) v.findViewById(R.id.team2);
        mTeam2.setOnClickListener(this);
        mRemove = (Button) v.findViewById(R.id.remove_point);
        mRemove.setOnClickListener(this);
        mDone = (Button) v.findViewById(R.id.game_over);
        mDone.setOnClickListener(this);
        mDone.setText("Reset");
        mGame = new Game();
        mRemovePoint = false;
        mOver = false;
        return v;
    }



    public void onPause() {
        super.onPause();
        GameList.get(getActivity()).updateGame(mGame);
    }

    @Override
    public void onClick(View v){
        if (v==mTeam1) {
            if (!mOver) {
                if (!mRemovePoint) {
                    mGame.incrementTeam1();
                } else {
                    mGame.setScore1(mGame.getScore1() - 1);
                    mRemovePoint = false;
                    mRemove.setBackgroundColor(1);
                }
            }
        }

        if (v==mTeam2){
            if (!mOver) {
                if (!mRemovePoint) {
                    mGame.incrementTeam2();
                } else {
                    mGame.setScore2(mGame.getScore2() - 1);
                    mRemovePoint = false;
                    mRemove.setBackgroundColor(1);
                }
            }

        }
        if (v==mRemove){
            mRemovePoint=true;
            mRemove.setBackgroundColor(1980104448 );
        }
        if (v==mDone){
           if( mOver=false){
               mOver=true;
               mDone.setBackgroundColor(198010448);
           }else{
               mOver=false;
               mDone.setBackgroundColor(1);

           }

        }


        mTeam1.setText(String.valueOf(mGame.getScore1()));
        mTeam2.setText(String.valueOf(mGame.getScore2()));
    }

  //  public interface Callbacks {
   //     void onGameUpdated(Game game);
  //  }
}






