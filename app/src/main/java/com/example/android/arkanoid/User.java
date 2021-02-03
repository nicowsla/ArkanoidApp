package com.example.android.arkanoid;

public class User {
    private String id;
    private String username;
    private String email;
    private long bestScore;
    private long bestTime;

    public User(String id, String username, String email, long bestScore, long bestTime) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.bestScore = bestScore;
        this.bestTime = bestTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getBestScore() {
        return bestScore;
    }

    public void setBestScore(long bestScore) {
        this.bestScore = bestScore;
    }

    public long getBestTime() {
        return bestTime;
    }

    public void setBestTime(long bestTime) {
        this.bestTime = bestTime;
    }
}
