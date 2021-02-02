package com.example.android.arkanoid;

public class Level {
    private String id;
    private String matrixString;
    private int speed;
    private String name;

    public Level(String id, String matrixString, int speed, String name) {
        this.id = id;
        this.matrixString = matrixString;
        this.speed = speed;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatrixString() {
        return matrixString;
    }

    public void setMatrixString(String matrixString) {
        this.matrixString = matrixString;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
