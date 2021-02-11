package com.example.android.arkanoid.entity;

public class Challenge {
    private String id;
    private String userID;
    private String username;
    private Long score;
    private Long yourScore;

    public Challenge(String id, String userID, String username, Long score, Long yourScore) {
        this.id = id;
        this.userID = userID;
        this.username = username;
        this.score = score;
        this.yourScore = yourScore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Long getYourScore() {
        return yourScore;
    }

    public void setYourScore(Long yourScore) {
        this.yourScore = yourScore;
    }
}
