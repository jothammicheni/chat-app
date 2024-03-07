package com.example.chatapp.data;

import android.os.Parcel;
import android.os.Parcelable;

public class userInfo implements Parcelable {

    String name, email, password;

    public userInfo() {
    }

    public userInfo(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    protected userInfo(Parcel in) {
        name = in.readString();
        email = in.readString();
        password = in.readString();
    }

    public static final Parcelable.Creator<userInfo> CREATOR = new Parcelable.Creator<userInfo>() {
        @Override
        public userInfo createFromParcel(Parcel in) {
            return new userInfo(in);
        }

        @Override
        public userInfo[] newArray(int size) {
            return new userInfo[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
    }
}
