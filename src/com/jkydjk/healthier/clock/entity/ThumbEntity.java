package com.jkydjk.healthier.clock.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ThumbEntity implements Parcelable {

  int id;
  String name;

  public ThumbEntity(Parcel p) {

    id = p.readInt();
    name = p.readString();
  }

  // ////////////////////////////
  // Parcelable apis
  // ////////////////////////////
  public static final Parcelable.Creator<ThumbEntity> CREATOR = new Parcelable.Creator<ThumbEntity>() {
    public ThumbEntity createFromParcel(Parcel p) {
      return new ThumbEntity(p);
    }

    public ThumbEntity[] newArray(int size) {
      return new ThumbEntity[size];
    }
  };

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel p, int flags) {
    p.writeInt(id);
    p.writeString(name);
    // p.writeParcelable(alert, flags);
  }

  // ////////////////////////////
  // end Parcelable apis
  // ////////////////////////////

}
