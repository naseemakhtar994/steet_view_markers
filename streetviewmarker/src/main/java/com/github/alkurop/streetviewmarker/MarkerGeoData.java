package com.github.alkurop.streetviewmarker;

/**
 * Created by alkurop on 31.05.16.
 */
public class /**/MarkerGeoData {
    public Place place;
    public double distance;
    public double azimuth;
    public String id;

    public MarkerGeoData (Place place, double distance, double azimuth) {
        this.distance = distance;
        this.azimuth = azimuth;
        this.place = place;
    }

    public String getId () {
        return place.getId();
    }

    @Override public String toString () {
        return "MarkerGeoData{" +
                  "distance=" + distance +
                  ", azimuth=" + azimuth +
                  '}';
    }

    @Override public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof MarkerGeoData)) return false;
        MarkerGeoData that = (MarkerGeoData) o;
        if (Double.compare(that.distance, distance) != 0) return false;
        return Double.compare(that.azimuth, azimuth) == 0;
    }

    @Override public int hashCode () {
        int result;
        long temp;
        temp = Double.doubleToLongBits(distance);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(azimuth);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
