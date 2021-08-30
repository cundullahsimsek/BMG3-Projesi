package com.app.bilgiyarismasi.entity;


public class Levels {

    private int level_id;
    private int level_name;
    private int level_point;
    private int locked;

    public Levels() {
    }

    public Levels(int level_id, int level_name, int level_point, int locked) {
        this.level_id = level_id;
        this.level_name = level_name;
        this.level_point = level_point;
        this.locked = locked;
    }

    public int getLevel_id() {
        return level_id;
    }

    public void setLevel_id(int level_id) {
        this.level_id = level_id;
    }

    public int getLevel_name() {
        return level_name;
    }

    public void setLevel_name(int level_name) {
        this.level_name = level_name;
    }

    public int getLevel_point() {
        return level_point;
    }

    public void setLevel_point(int level_point) {
        this.level_point = level_point;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }
}
