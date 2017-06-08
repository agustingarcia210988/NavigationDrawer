package info.androidhive.navigationdrawer.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.other.User;

public class BaseActivity extends AppCompatActivity {


    public  final static String PAR_KEY = "info.androidhive.navigationdrawer.activity.par";
    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
    public void PacelableMethod(User user){

        Intent mIntent = new Intent(this,MainActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putParcelable(PAR_KEY, user);
        mIntent.putExtras(mBundle);

        startActivity(mIntent);
    }

}


