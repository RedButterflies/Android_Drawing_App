package com.example.lab5;

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable {
    public long id;
    public String name;

    public Image(long id, String name) {
        this.id = id;
        this.name = name;
    }

    protected Image(Parcel in) {
        id = in.readLong();
        name = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
