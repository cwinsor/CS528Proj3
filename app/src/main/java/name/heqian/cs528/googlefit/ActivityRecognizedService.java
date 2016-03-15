package name.heqian.cs528.googlefit;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.text.DateFormat;
import java.util.Collections;
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
static int zona = 0;

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

            sendMsgToMainThread(result);
        }
    }

    protected void sendMsgToMainThread(ActivityRecognitionResult result) {
        DetectedActivity underConsiderationMostLikely = result.getMostProbableActivity();
        // initially the currentActivity will be null - avoid this case
        if (currentActivity == null) {
            currentActivity = underConsiderationMostLikely;
        }
        if (underConsiderationMostLikely.getConfidence() >= 75) {
            if (underConsiderationMostLikely.getType() == currentActivity.getType()) {
                // do nothing

                zona = zona + 1;

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

    private void showText(final String text) {

        // zona
        
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }
}
