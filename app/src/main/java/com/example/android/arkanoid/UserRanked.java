package com.example.android.arkanoid;

public class UserRanked {
    private String id;
    private String username;
    private String email;
    private long bestScore;
    private long bestTime;
    private int livArcade;
    private int livTema;

    public UserRanked(String id, String username, String email, long bestScore, long bestTime, int livArcade, int livTema) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.bestScore = bestScore;
        this.bestTime = bestTime;
        this.livArcade = livArcade;
        this.livTema = livTema;
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

    public int getLivArcade() {
        return livArcade;
    }

    public void setLivArcade(int livArcade) {
        this.livArcade = livArcade;
    }

    public int getLivTema() {
        return livTema;
    }

    public void setLivTema(int livTema) {
        this.livTema = livTema;
    }
}
