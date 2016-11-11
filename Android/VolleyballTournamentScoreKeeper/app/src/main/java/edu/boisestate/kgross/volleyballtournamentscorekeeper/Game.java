package edu.boisestate.kgross.volleyballtournamentscorekeeper;

import java.util.UUID;

/**
 * Created by kim on 11/16/2015.
 */
public class Game {
    String mTeam1;
    String mTeam2;
    String mGame;
    int mScore1;
    int mScore2;
    boolean over;
    UUID id;

    public Game(){

        this(UUID.randomUUID());
    }
    public Game(UUID id){
        this.id=id;
        mScore1=0;
        mScore2=0;
        over=false;
    }


    public Game(String name){
        mGame=name;
        mScore1=0;
        mScore2=0;
        over=false;
    }
    public UUID getId(){
    return id;
    }
    public void team1Score(){
        mScore1++;
    }
    public void team2Score(){
        mScore2++;
    }

    public void team1Remove(){
        mScore1--;
    }
    public void team2Remove(){
        mScore2--;
    }


    public String getTeam1() {
        return mTeam1;
    }

    public void setTeam1(String mTeam1) {
        this.mTeam1 = mTeam1;
    }

    public String getTeam2() {
        return mTeam2;
    }

    public void setTeam2(String mTeam2) {
        this.mTeam2 = mTeam2;
    }

    public String getGame() {
        return mGame;
    }

    public void setGame(String mGame) {
        this.mGame = mGame;
    }

    public int getScore1() {
        return mScore1;
    }

    public void setScore1(int mScore1) {
        this.mScore1 = mScore1;
    }

    public int getScore2() {
        return mScore2;
    }

    public void setScore2(int mScore2) {
        this.mScore2 = mScore2;
    }

    public boolean isOver() {
        return over;
    }

    public void setOver(boolean over) {
        this.over = over;
    }
    public void incrementTeam1(){
        mScore1++;
    }

    public void incrementTeam2(){
        mScore2++;
    }
}
