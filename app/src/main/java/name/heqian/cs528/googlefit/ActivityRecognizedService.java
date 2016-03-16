package name.heqian.cs528.googlefit;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Paul on 2/1/16.
 */
public class ActivityRecognizedService extends IntentService {

    // Handler is used to send msg to the main app thread.  It is started in onStartCommand() thread
    private Handler handler;
    static DetectedActivity lastActivity = null;
    static DetectedActivity currentActivity = null;

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result.getProbableActivities());

            // send toast message to MainActivity
            myHandleDetectedActivities(result);

            // the following creates a Notification which gets sent to the
            // the PendingIntent defines what happens when the user clicks on the notification
            // and in this case the PendingIntent has been defined in MainActivity itself
            // and that is to go to MainActivity
            Resources resources = getResources();
            Intent i = MainActivity.newIntent(this);
            i.putExtra("activity_type", "flying"); // <-- HERE I PUT THE EXTRA VALUE

            // PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
            PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);



            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(R.drawable.running)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(false)
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, notification);

        }
    }

    // use a "handler" to send a Runnable "toast"
    protected void myHandleDetectedActivities(ActivityRecognitionResult result) {
        DetectedActivity underConsiderationMostLikely = result.getMostProbableActivity();
        // initially the currentActivity will be null - avoid this case
        if (currentActivity == null) {
            currentActivity = underConsiderationMostLikely;
        }
        if (underConsiderationMostLikely.getConfidence() >= 75) {
            if (underConsiderationMostLikely.getType() == currentActivity.getType()) {
                // do nothing
            } else {
                // we have a new activity
                lastActivity = currentActivity;
                currentActivity = underConsiderationMostLikely;

                // toast to the user
                showText(currentActivity.toString() + DateFormat.getDateTimeInstance().format(new Date()));
                // showText(currentActivity.zzho(currentActivity.getType()));
            }
        }
    }

    private void showText(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {

        for (DetectedActivity activity : probableActivities) {
            switch (activity.getType()) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e("ActivityRecogition", "In Vehicle: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e("ActivityRecogition", "On Bicycle: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e("ActivityRecogition", "On Foot: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e("ActivityRecogition", "Running: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e("ActivityRecogition", "Still: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e("ActivityRecogition", "Tilting: " + activity.getConfidence());
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e("ActivityRecogition", "Walking: " + activity.getConfidence());
                    if (activity.getConfidence() >= 75) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                        builder.setContentText("Are you walking?");
                        builder.setSmallIcon(R.mipmap.ic_launcher);
                        builder.setContentTitle(getString(R.string.app_name));
                        NotificationManagerCompat.from(this).notify(0, builder.build());

                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e("ActivityRecogition", "Unknown: " + activity.getConfidence());
                    break;
                }
            }
        }
    }

}
