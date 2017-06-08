package info.androidhive.navigationdrawer.other;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by aggarcia on 12/24/2016.
 */

public class User  implements Parcelable {
    String uid ;
    String username;
    Boolean request=false;
    String email;
    Uri photoUrl;
    public double latitude;
    public double longitude;


      public User()
    {

    }
    public User(String uid, String username, String email, String password) {
        this.uid = uid;
        this.username = username;
        this.email = email;

    }
    public User(String username, String email, boolean request) {
        this.username = username;
        this.email = email;
        this.request=request;
    }
    public User(FirebaseUser user)
    {
       // this.username=user.getDisplayName();
        this.username= user.getDisplayName();
        this.email=user.getEmail();
        this.uid= user.getUid();
        this.photoUrl=user.getPhotoUrl();

    }


    protected User(Parcel in) {
        uid = in.readString();
        longitude=in.readDouble();
        latitude=in.readDouble();
        username = in.readString();
        email = in.readString();
        photoUrl = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeParcelable(photoUrl, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Boolean getRequest() {
        return request;
    }

    public void setRequest(Boolean request) {
        this.request = request;
    }
}
