package com.company.imetlin.fishmarker.customview.spinner;

import android.os.Parcel;
import android.os.Parcelable;

import kotlinx.android.parcel.Parcelize;

public class DataSpinner implements Parcelable {
    private long id;
    private String name;
    private boolean isSelected;
    private Integer image;
    private Object object;

    public DataSpinner(){

    }

    protected DataSpinner(Parcel in) {
        id = in.readLong();
        name = in.readString();
        isSelected = in.readByte() != 0;
        if (in.readByte() == 0) {
            image = null;
        } else {
            image = in.readInt();
        }
    }

    public static final Creator<DataSpinner> CREATOR = new Creator<DataSpinner>() {
        @Override
        public DataSpinner createFromParcel(Parcel in) {
            return new DataSpinner(in);
        }

        @Override
        public DataSpinner[] newArray(int size) {
            return new DataSpinner[size];
        }
    };

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the isSelected
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * @param isSelected the isSelected to set
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        if (image == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(image);
        }
    }
}