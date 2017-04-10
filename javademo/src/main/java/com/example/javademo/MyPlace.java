package com.example.javademo;


import android.os.Parcel;
import com.github.alkurop.streetviewmarker.Location;
import com.github.alkurop.streetviewmarker.Place;

public class MyPlace implements Place {
  private final String markerId;
  private final String iconPath;
  private final int drawable;
  private final Location location;

  public MyPlace(String markerId, Location location, String iconPath, int drawable) {
    this.markerId = markerId;
    this.iconPath = iconPath;
    this.drawable = drawable;
    this.location = location;
  }

  @Override
  public String getId() {
    return markerId;
  }

  @Override
  public Location getLocation() {
    return location;
  }

  @Override
  public String getMarkerPath() {
    return iconPath;
  }

  @Override
  public int getDrawable() {
    return drawable;
  }


  @Override
  public int describeContents() { return 0; }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.markerId);
    dest.writeString(this.iconPath);
    dest.writeInt(this.drawable);
    dest.writeParcelable(this.location, flags);
  }

  protected MyPlace(Parcel in) {
    this.markerId = in.readString();
    this.iconPath = in.readString();
    this.drawable = in.readInt();
    this.location = in.readParcelable(Location.class.getClassLoader());
  }

  public static final Creator<MyPlace> CREATOR = new Creator<MyPlace>() {
    @Override
    public MyPlace createFromParcel(Parcel source) {return new MyPlace(source);}

    @Override
    public MyPlace[] newArray(int size) {return new MyPlace[size];}
  };
}
