package com.github.alkurop.mylibrary;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alkurop on 2/3/17.
 */

public class Location implements Parcelable {
    public double lat;
    public double lng;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
    }

    public Location() {}

    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    protected Location(Parcel in) {
        this.lat = in.readDouble();
        this.lng = in.readDouble();
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        @Override public Location createFromParcel(Parcel source) {return new Location(source);}

        @Override public Location[] newArray(int size) {return new Location[size];}
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (Double.compare(location.lat, lat) != 0) return false;
        return Double.compare(location.lng, lng) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lng);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override public String toString() {
        return "Location{" +
            "lat=" + lat +
            ", lng=" + lng +
            '}';
    }
}
