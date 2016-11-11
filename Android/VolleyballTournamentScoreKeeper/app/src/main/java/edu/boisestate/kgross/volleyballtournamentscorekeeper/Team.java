package edu.boisestate.kgross.volleyballtournamentscorekeeper;

import java.util.UUID;

/**
 * Created by kgross on 11/22/2015.
 */
public class Team


{




    UUID mId;
    String mCoach;
    String mName;
    String mDiscription;
    int mWins;
    int mLoses;


    public Team(){
        this(UUID.randomUUID());
    }
    public Team(UUID id) {
        mId = id;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID mId) {
        this.mId = mId;
    }

    public String getCoach() {
        return mCoach;
    }

    public void setCoach(String mCoach) {
        this.mCoach = mCoach;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getDiscription() {
        return mDiscription;
    }

    public void setDiscription(String mDiscription) {
        this.mDiscription = mDiscription;
    }

    public int getWins() {
        return mWins;
    }

    public void setWins(int mWins) {
        this.mWins = mWins;
    }

    public int getLoses() {
        return mLoses;
    }

    public void setLoses(int mLoses) {
        this.mLoses = mLoses;
    }
    public void increaseWins(){
        mWins++;
    }
    public void increaseLoses(){
        mLoses++;
    }
}
