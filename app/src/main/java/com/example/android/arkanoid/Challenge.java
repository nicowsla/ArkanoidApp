package com.example.android.arkanoid;

public class Challenge {
    private String id;
    private String userID;
    private String username;
    private Long score;
    private Long yourScore;
    private Boolean accepted;
    private Boolean refused;

    public Challenge(String id, String userID, String username, Long score, Long yourScore, Boolean accepted, Boolean refused) {
        this.id = id;
        this.userID = userID;
        this.username = username;
        this.score = score;
        this.yourScore = yourScore;
        this.accepted = accepted;
        this.refused = refused;
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

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Boolean getRefused() {
        return refused;
    }

    public void setRefused(Boolean refused) {
        this.refused = refused;
    }
}
