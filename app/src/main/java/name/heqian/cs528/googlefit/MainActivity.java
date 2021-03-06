package name.heqian.cs528.googlefit;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionApi;
import com.google.android.gms.wallet.wobs.TimeInterval;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int NOTIFY_ID = 1357;

    public GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // this PendingIntent is part of the Android Activity Recognition
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // The following static method builds an Intent which will
    // be used by others to generate a Notification that
    // will come back to me
    //  public static Intent newIntent(Context context) {
    //      Intent intent = new Intent(context, MainActivity.class);
    //       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    //      // intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    //      intent.putExtra("activity_type", "unmodified"); // caller will modify this
    //      return intent;
    //  }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("MainActivity", "------------------------->onResume");
        Intent intent = getIntent();

        handleUi(intent);
    }

    // The following is how the Intent arrives back to me
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("MainActivity", "------------------------->onNewIntent");
        setIntent(intent);

        handleUi(intent);
    }

    public void handleUi(Intent intent) {
        String activityType = intent.getStringExtra("activity_type");
        TextView t = (TextView) findViewById(R.id.textView2);
        if (activityType != null) {
            t.setText("You are " + activityType);
        }

        int imageTarget = intent.getIntExtra("image_source", 0);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        if (imageView != null) {
            imageView.setImageResource(imageTarget);
        }
    }

}


