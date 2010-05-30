package com.honza.fishwire;

import android.os.Parcel;
import android.os.Parcelable;

public final class RemoteMessage implements Parcelable {
    
    public String body;
    public String user;
    public long id;

    public static final Parcelable.Creator<RemoteMessage> CREATOR = new Parcelable.Creator<RemoteMessage>() {
        public RemoteMessage createFromParcel(Parcel in) {
            return new RemoteMessage(in);
        }

        public RemoteMessage[] newArray(int size) {
            return new RemoteMessage[size];
        }
    };

    public RemoteMessage() {
    }

    private RemoteMessage(Parcel in) {
        readFromParcel(in);
    }

    public void writeToParcel(Parcel out, int a) {
    	
    	out.writeString(body);
    	out.writeString(user);
    	out.writeLong(id);
    	
    }

    public void readFromParcel(Parcel in) {
    	
    	body = in.readString();
    	user = in.readString();
    	id = in.readLong();
    	
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}