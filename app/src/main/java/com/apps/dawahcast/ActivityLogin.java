package com.apps.dawahcast;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.apps.utils.Constant;
import com.apps.utils.JsonUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

import twitter4j.auth.RequestToken;

public class ActivityLogin extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    Toolbar toolbar;
    Button btntwitter,btnfacebook,btngmail;
    Twitter twitter;
    RequestToken requestToken = null;
    twitter4j.auth.AccessToken accessToken;
    AccessToken accessTokenfb;
    String oauth_url,oauth_verifier,profile_url;
    Dialog auth_dialog;
    WebView web;
    SharedPreferences pref;
    ProgressDialog progress;
    Bitmap bitmap;
    String strEmail, strMessage, strName, strId;
    long  uid;
    User user;
    MyApplication MyApp;
    CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(ActivityLogin.this);
        callbackManager = CallbackManager.Factory.create();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) this.findViewById(R.id.toolbar_login);
        toolbar.setTitle(getString(R.string.tool_login));
        this.setSupportActionBar(toolbar);
        setStatusColor();
        MyApp = MyApplication.getInstance();
        pref = getPreferences(0);
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer("bsSncqsj861W9y6WFvBRUErIl","HmpaHAr6IMaUNiWz5ndNsp449oInnmdWDFrt9H1aKBdrxRbvjl");


        btnfacebook=(Button)findViewById(R.id.button_facebook);
        btngmail=(Button)findViewById(R.id.button_gmail);
        btntwitter=(Button)findViewById(R.id.button_twitter);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btngmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btnfacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(ActivityLogin.this, Arrays.asList("public_profile", "email"));
            }
        });
        initCallbackManager();

        btntwitter.setOnClickListener(new LoginProcess());
     }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class LoginProcess implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            new TokenGet().execute();

        }}

    private class TokenGet extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {

            try {
                requestToken = twitter.getOAuthRequestToken();
                oauth_url = requestToken.getAuthorizationURL();
            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return oauth_url;
        }
        @Override
        protected void onPostExecute(String oauth_url) {
            if(oauth_url != null){
                Log.e("URL", oauth_url);
                auth_dialog = new Dialog(ActivityLogin.this);
                auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                auth_dialog.setContentView(R.layout.auth_dialog);
                web = (WebView)auth_dialog.findViewById(R.id.webv);
                web.getSettings().setJavaScriptEnabled(true);
                web.loadUrl(oauth_url);
                web.setWebViewClient(new WebViewClient() {
                    boolean authComplete = false;
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon){
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (url.contains("oauth_verifier") && authComplete == false){
                            authComplete = true;
                            Log.e("Url",url);
                            Uri uri = Uri.parse(url);
                            oauth_verifier = uri.getQueryParameter("oauth_verifier");

                            auth_dialog.dismiss();
                            new AccessTokenGet().execute();
                        }else if(url.contains("denied")){
                            auth_dialog.dismiss();
                            Toast.makeText(ActivityLogin.this, "Sorry !, Permission Denied", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
                auth_dialog.show();
                auth_dialog.setCancelable(true);



            }else{

                Toast.makeText(ActivityLogin.this, "Sorry !, Network Error or Invalid Credentials", Toast.LENGTH_SHORT).show();


            }
        }
    }

    private class AccessTokenGet extends AsyncTask<String, String, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(ActivityLogin.this);
            progress.setMessage("Fetching Data ...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();

        }


        @Override
        protected Boolean doInBackground(String... args) {

            try {

                accessToken = twitter.getOAuthAccessToken(requestToken, oauth_verifier);
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("ACCESS_TOKEN", accessToken.getToken());
                edit.putString("ACCESS_TOKEN_SECRET", accessToken.getTokenSecret());
                user = twitter.showUser(accessToken.getUserId());
                profile_url = user.getOriginalProfileImageURL();
                edit.putString("NAME", user.getName());
                edit.putString("IMAGE_URL", user.getOriginalProfileImageURL());

                edit.commit();
                Log.e("idd", ""+user.getName()+accessToken.getUserId());


            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();


            }

            return true;
        }
        @Override
        protected void onPostExecute(Boolean response) {
            if(response){
                progress.hide();
                strName=user.getName();
                strEmail=user.getName()+"@gmail.com";
                uid=accessToken.getUserId();

                new MyTaskLoginTwitter().execute(Constant.URL_TWITTER +strName+ "&email=" + strEmail+ "&twitter_id==" + uid);
                Log.e("fblogin",""+Constant.URL_TWITTER +strName+ "&email=" + strEmail+ "&twitter_id==" + uid);
            }

        }

     }

    public void setStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.statusBar));
        }
    }

    private class MyTaskLoginTwitter extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (null != pDialog && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (null == result || result.length() == 0) {

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        if (objJson.has(Constant.MSG)) {
                            strMessage = objJson.getString(Constant.MSG);
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        } else {
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                            strName = objJson.getString(Constant.USER_NAME);
                            strId = objJson.getString(Constant.USER_ID);


                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void initCallbackManager() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                RequestData();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }

    public void RequestData() {
        GraphRequest request = GraphRequest.newMeRequest(accessTokenfb.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject json = response.getJSONObject();
                Log.e("json",""+json);
                try {
                    if (json != null) {


                        if (JsonUtils.isNetworkAvailable(ActivityLogin.this)) {
                            strEmail = json.getString("email");
                            strName = json.getString("name").replace(" ", "%20");
                            strId=json.getString("id");

                            new MyTaskLoginFacebook().execute(Constant.URL_FACEBOOK +strName+ "&email=" + strEmail+ "&&fb_id=" + strId);
                            Log.e("fbuser",""+Constant.URL_FACEBOOK +strName+ "&email=" + strEmail+ "&&fb_id=" + strId);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,picture.width(150).height(150)");
        request.setParameters(parameters);
        request.executeAsync();
    }

     private class MyTaskLoginFacebook extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ActivityLogin.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (null != pDialog && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (null == result || result.length() == 0) {

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        if (objJson.has(Constant.MSG)) {
                            strMessage = objJson.getString(Constant.MSG);
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        } else {
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                            strName = objJson.getString(Constant.USER_NAME);
                            strId = objJson.getString(Constant.USER_ID);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }

        }
    }
    private class MyTaskLoginGoogle extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ActivityLogin.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (null != pDialog && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (null == result || result.length() == 0) {

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        if (objJson.has(Constant.MSG)) {
                            strMessage = objJson.getString(Constant.MSG);
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                        } else {
                            Constant.GET_SUCCESS_MSG = objJson.getInt(Constant.SUCCESS);
                            strName = objJson.getString(Constant.USER_NAME);
                            strId = objJson.getString(Constant.USER_ID);


                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }

        }
    }
    public void setResult() {

        if (Constant.GET_SUCCESS_MSG == 0) {
            Toast.makeText(ActivityLogin.this,"Something wrong !!",Toast.LENGTH_SHORT).show();

        } else {
            MyApp.saveIsLogin(true);
            MyApp.saveLogin(strId, strName, strEmail);
            Intent i = new Intent(ActivityLogin.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();

            strName = acct.getDisplayName().toString().replace(" ", "%20");
            strEmail = acct.getEmail();
            strId=acct.getId();
            if (JsonUtils.isNetworkAvailable(ActivityLogin.this)) {
                new MyTaskLoginGoogle().execute(Constant.URL_GMAIL +strName + "&email=" + strEmail + "&gplus_id=" + strId);
                Log.e("gmail",""+Constant.URL_GMAIL +strName + "&email=" + strEmail + "&gplus_id=" + strId);
            }
        } else {

            updateUI(false);
        }
    }

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            //showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    //hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.button_gmail).setVisibility(View.GONE);

        } else {
            findViewById(R.id.button_gmail).setVisibility(View.VISIBLE);

        }}
}