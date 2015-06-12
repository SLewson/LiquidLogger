package com.example.slewson.liquidlogger.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Marie on 5/21/2015.
 */
public class RecipeObject implements Parcelable {
    private String name;
    private Double pH;
    private Double temp;
    private String notes;
    private String id;

    public RecipeObject() {
        this.name = null;
        this.pH = 0.0;
        this.temp = 0.0;
        this.notes = null;
        this.id = null;
    }

    public RecipeObject(String name, Double pH, Double temp, String notes, String id) {
        this.name = name;
        this.pH = pH;
        this.temp = temp;
        this.notes = notes;
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeString(name);
        arg0.writeDouble(pH);
        arg0.writeDouble(temp);
        arg0.writeString(notes);
    }

    public static final Parcelable.Creator<RecipeObject> CREATOR = new Parcelable.Creator<RecipeObject>() {
        public RecipeObject createFromParcel(Parcel in) {
            return new RecipeObject(in);
        }

        public RecipeObject[] newArray(int size) {
            return new RecipeObject[size];
        }
    };

    public RecipeObject(Parcel in) {
        name = in.readString();
        pH = in.readDouble();
        temp = in.readDouble();
        notes = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getpH() {
        return pH;
    }

    public void setpH(Double pH) {
        this.pH = pH;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
