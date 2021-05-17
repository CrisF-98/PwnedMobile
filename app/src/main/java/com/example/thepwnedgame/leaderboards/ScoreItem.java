package com.example.thepwnedgame.leaderboards;

public class ScoreItem {

    private final Integer position ;
    private final String username;
    private final Integer score;

    public ScoreItem(Integer position, String username, Integer score) {
        this.position = position;
        this.username = username;
        this.score = score;
    }

    public Integer getPosition(){
        return this.position;
    }

    public String getPositionAsString(){
        return String.valueOf(position);
    }

    public String getUsername(){
        return username;
    }

    public Integer getScore(){
        return score;
    }

    public String getScoreAsString(){
        return String.valueOf(score);
    }
}
