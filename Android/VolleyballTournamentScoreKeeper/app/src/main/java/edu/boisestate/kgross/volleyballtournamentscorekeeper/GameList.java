package edu.boisestate.kgross.volleyballtournamentscorekeeper;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by kgross on 11/22/2015.
 */
public class GameList
{

    private static GameList sGameList;
    private List<Game> mGames;

    public static GameList get (Context context){
        if (sGameList==null){
            sGameList=new GameList(context);
        }
        return sGameList;
    }
    private GameList(Context context){
        mGames= new ArrayList<>();

    }
    public List<Game> getGames(){
        return mGames;
    }
    public Game getGame(UUID id){
        for (Game game: mGames){
            if (game.getId().equals(id));
            return game;
        }
        return null;
    }
    public void addGame(Game g){
     //   ContentValues values=getContentValues(c)
       mGames.add(g);
    }
    public void updateGame(Game game){

    }

}
