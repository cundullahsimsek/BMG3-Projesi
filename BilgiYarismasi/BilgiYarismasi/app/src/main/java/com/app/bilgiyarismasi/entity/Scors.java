package com.app.bilgiyarismasi.entity;

/**
 * Created by Cundullah on 21.4.2016.
 */
public class Scors {

    private int scor_id;
    private String username;
    private int scor;
    private String scor_header_level;
    private int scor_section;

    public Scors() {
    }

    public Scors(int scor_id, String username, int scor, String scor_header_level, int scor_section) {
        this.scor_id = scor_id;
        this.username = username;
        this.scor = scor;
        this.scor_header_level = scor_header_level;
        this.scor_section = scor_section;
    }

    public int getScor_id() {
        return scor_id;
    }

    public void setScor_id(int scor_id) {
        this.scor_id = scor_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScor() {
        return scor;
    }

    public void setScor(int scor) {
        this.scor = scor;
    }

    public String getScor_header_level() {
        return scor_header_level;
    }

    public void setScor_header_level(String scor_header_level) {
        this.scor_header_level = scor_header_level;
    }

    public int getScor_section() {
        return scor_section;
    }

    public void setScor_section(int scor_section) {
        this.scor_section = scor_section;
    }
}
