package com.e.polemyiteration1;

/**
 * a general class for storing forecast weather data.
 */
public class weatherParameter {
    private String date;
    private int airQuality;
    private int grass;
    private int tree;
    private int ragweed;


    public int getAirQuality() {
        return airQuality;
    }

    public void setAirQuality(int airQuality) {
        this.airQuality = airQuality;
    }

    public int getGrass() {
        return grass;
    }

    public void setGrass(int grass) {
        this.grass = grass;
    }

    public int getTree() {
        return tree;
    }

    public void setTree(int tree) {
        this.tree = tree;
    }

    public int getRagweed() {
        return ragweed;
    }

    public void setRagweed(int ragweed) {
        this.ragweed = ragweed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
