package com.github.alkurop.mylibrary;

/**
 * Created by alkurop on 31.05.16.
 */
public class MarkerMatrixData {
    final public MarkerGeoData data;
    public boolean shouldShow;
    public double scale;
    public double xLoc;
    public double yLoc;


    public MarkerMatrixData(MarkerGeoData data, boolean shouldShow, double scale, double xLoc, double yLoc ) {
        this.data = data;
        this.shouldShow = shouldShow;
        this.scale = scale;
        this.xLoc = xLoc;
        this.yLoc = yLoc;
    }

    @Override public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof MarkerMatrixData)) return false;
        MarkerMatrixData that = (MarkerMatrixData) o;
        return data.id ==  that.data.id;
    }

    @Override public int hashCode () {
        return  data.id.hashCode() ;
    }

    @Override public String toString () {
        return "MarkerMatrixData{" +data .toString() +
                  ", shouldShow=" + shouldShow +
                  ", scale=" + scale +
                  ", xLoc=" + xLoc +
                  ", yLoc=" + yLoc +
                  '}';
    }
}
