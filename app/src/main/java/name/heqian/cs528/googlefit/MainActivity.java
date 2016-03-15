package name.heqian.cs528.googlefit;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;


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

        // this PendingIntent is used by ActivityRecognizedService to send a notification to MainActivity (me)
        // reference Busy Coders Guide (.pdf) page 1006
        /*
        Intent intent2 = new Intent( this, MainActivity.class );
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent2);
        PendingIntent pendingIntent2 = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("ZONA YOWZA")
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .build();
        PendingIntent i = null;
        notification.setContentIntent(i);
*/
//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

        NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder normal = buildNormal();
        NotificationCompat.InboxStyle big = new NotificationCompat.InboxStyle(normal);

        mgr.notify(NOTIFY_ID,
                big.setSummaryText(getString(R.string.summary))
                        .addLine(getString(R.string.entry))
                        .addLine(getString(R.string.another_entry))
                        .addLine(getString(R.string.third_entry))
                        .addLine(getString(R.string.yet_another_entry))
                        .addLine(getString(R.string.low)).build());


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private NotificationCompat.Builder buildNormal() {
        NotificationCompat.Builder b = new NotificationCompat.Builder(this);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(getString(R.string.download_complete))
                .setContentText(getString(R.string.fun))
                .setContentIntent(buildPendingIntent(Settings.ACTION_SECURITY_SETTINGS))
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setTicker(getString(R.string.download_complete))
                .setPriority(Notification.PRIORITY_HIGH)
                .addAction(android.R.drawable.ic_media_play,
                        getString(R.string.play),
                        buildPendingIntent(Settings.ACTION_SETTINGS));

        return (b);
    }

    private PendingIntent buildPendingIntent(String action) {
        Intent i = new Intent(action);

        return (PendingIntent.getActivity(this, 0, i, 0));
    }

}
