package com.example.nfctransfer.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.nfctransfer.R;
import com.example.nfctransfer.fragments.ActionShareFragment;
import com.example.nfctransfer.fragments.MatchListFragments;
import com.example.nfctransfer.fragments.ProfileFragment;
import com.example.nfctransfer.networking.ApiResponses.Profile.Profile;
import com.example.nfctransfer.networking.ApiResponses.Profile.PullProfileResponse;
import com.example.nfctransfer.networking.HttpCodes;
import com.example.nfctransfer.networking.NfcTransferApi;
import com.example.nfctransfer.networking.Session;
import com.example.nfctransfer.sharedPreferences.Preferences;
import com.example.nfctransfer.socialAPIs.FacebookAPI;
import com.example.nfctransfer.socialAPIs.TwitterAPI;
import com.example.nfctransfer.views.TabLayoutIconResizer;
import com.example.nfctransfer.websockets.SocketConnectionManager;
import com.facebook.CallbackManager;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private SmartTabLayout mSmartTabLayout;
    private Socket mSocket;
    private TwitterAuthClient mTwitterAuthClient;
    private CallbackManager mFacebookCallbackManager;
    private float mTabLayoutIconSize;

    public static final String SOCKET_TAG = "SOCKET";
    public static final String KEY_PASS_FIELDS_TO_ADD = "fields_to_add";
    private static final int FACEBOOK_ACTIVITY_REQUEST_CODE = 64206;
    private static final int LINKEDIN_ACTIVITY_REQUEST_CODE = 3672;
    private static final int TWITTER_ACTIVITY_REQUEST_CODE = 140;

    public MainActivity() {

        mTwitterAuthClient = new TwitterAuthClient();
        mFacebookCallbackManager = CallbackManager.Factory.create();

        FacebookAPI.getInstance().setFacebookCallbackManager(mFacebookCallbackManager);
        TwitterAPI.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        connectSocket();
    }

    private void prepareTabIcon(ImageView icon) {
        TabLayoutIconResizer.scaleImageview(mContext, icon, (int) mTabLayoutIconSize);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, (int) mTabLayoutIconSize, 1.0f);
        layoutParams.gravity = Gravity.CENTER;
        icon.setLayoutParams(layoutParams);
    }

    private void initComponents() {
        mContext = getApplicationContext();

        final LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        mTabLayoutIconSize = mContext.getResources().getDimension(R.dimen.activity_main_tab_layout_icon_size);

        mSmartTabLayout = (SmartTabLayout) findViewById(R.id.view_pager_tab);
        mSmartTabLayout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {

                ImageView icon = (ImageView) inflater.inflate(R.layout.custom_viewpager_item, container, false);

                switch (position) {
                    case 0:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.share_filled));
                        break;
                    case 1:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.user_male_filled));
                        break;
                    case 2:
                        icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.conference_call_filled));
                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }
                prepareTabIcon(icon);
                return icon;
            }
        });

        FragmentPagerItemAdapter horizontalViewPagerAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Share", ActionShareFragment.class)
                .add("Profile", ProfileFragment.class)
                .add("Matchs", MatchListFragments.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(horizontalViewPagerAdapter);

        mSmartTabLayout.setViewPager(viewPager);
    }

    private void retrieveTargetDataWhenBeamed(final String targetUserId){

        Call<PullProfileResponse> call;

        call = NfcTransferApi.getInstance().getTargetProfileData(Session.accessToken, targetUserId);
        call.enqueue(new Callback<PullProfileResponse>() {
            @Override
            public void onResponse(Call<PullProfileResponse> call, Response<PullProfileResponse> response) {
                int code = response.code();
                PullProfileResponse result;

                if (code != HttpCodes.OK) {
                    return;
                }
                result = response.body();
                Profile profile = result.getUser();
                Intent intent = new Intent(mContext, ProfileDisplayer.class);
                intent.putExtra(ProfileDisplayer.EXTRA_KEY_PROFILE, profile);
                intent.putExtra(ProfileDisplayer.EXTRA_KEY_INSERT_IN_DATABASE, true);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<PullProfileResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void onLogOut() {
        Preferences.getInstance().deleteSavedCredentials(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finishAffinity();
        startActivity(intent);
    }

    private void onOpenSettings() {
        Intent toSettings = new Intent(mContext, SettingsActivity.class);
        startActivity(toSettings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.option_settings:
                onOpenSettings();
                return true;
            case R.id.option_logout:
                onLogOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSocketEvents() {
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject auth = new JSONObject();
                try {
                    auth.put("token", Session.accessToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("authenticate", auth);
            }
        });

        mSocket.on("user_got_beamed", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject reply = (JSONObject) args[0];
                String targetId;

                try {
                    if ((targetId = reply.getString("user_id")) != null) {
                        retrieveTargetDataWhenBeamed(targetId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mSocket.on("authenticated", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                SocketConnectionManager.getInstance().setSocket(mSocket);
                SocketConnectionManager.getInstance().notifySocketConnected();
            }
        });

        mSocket.on("unauthorized", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(SOCKET_TAG, "Authentication Failed");
            }
        });

        mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                SocketConnectionManager.getInstance().notifySocketDisonnected();
            }
        });
    }

    private boolean connectSocket() {
        try {
            mSocket = IO.socket("https://francoisseminerio.me");
            mSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        setSocketEvents();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case LINKEDIN_ACTIVITY_REQUEST_CODE:
                    //LISessionManager.getInstance(context).onActivityResult(this, requestCode, resultCode, data);
                    break;
                case TWITTER_ACTIVITY_REQUEST_CODE:
                    mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
                    break;
                case FACEBOOK_ACTIVITY_REQUEST_CODE:
                    mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
                    break;
                default:
                    mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
