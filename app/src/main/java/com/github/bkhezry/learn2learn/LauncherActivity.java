package com.github.bkhezry.learn2learn;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LauncherActivity extends AppCompatActivity implements
  GoogleApiClient.OnConnectionFailedListener {
  private static final int RC_SIGN_IN = 10001;
  private GoogleApiClient mGoogleApiClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_launcher);
    ButterKnife.bind(this);
    setUpGoogleSignIn();
  }

  private void setUpGoogleSignIn() {
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build();
    mGoogleApiClient = new GoogleApiClient.Builder(this)
      .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
      .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
      .build();
  }


  @OnClick(R.id.google_login_button)
  public void login() {
    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      handleSignInResult(result);
    }
  }

  private void handleSignInResult(GoogleSignInResult result) {
    if (result.isSuccess()) {
      // Signed in successfully, show authenticated UI.
      GoogleSignInAccount acct = result.getSignInAccount();
      if (acct != null) {
        handleAccount(acct);
      }
    }
  }

  private void handleAccount(GoogleSignInAccount acct) {

  }


  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  @Override
  public void onPause() {
    super.onPause();
    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
      mGoogleApiClient.stopAutoManage(this);
      mGoogleApiClient.disconnect();
    }
  }

  @Override
  public void onResume() {
    if (mGoogleApiClient != null) {
      if (!mGoogleApiClient.isConnected()) {
        mGoogleApiClient.connect();
      }
    }
    super.onResume();
  }

}

