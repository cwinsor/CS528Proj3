package name.heqian.cs528.googlefit;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by Paul on 2/1/16.
 */
public class ActivityRecognizedService extends IntentService {

    static Notification notification;
    static Intent intent;

    // Handler is used to send msg to the main app thread.  It is started in onStartCommand() thread
    private Handler handler;
    //  static DetectedActivity lastActivity = new DetectedActivity(DetectedActivity.UNKNOWN, 100);
    static DetectedActivity currentActivity = new DetectedActivity(DetectedActivity.UNKNOWN, 100);

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        doCurrentActivity(new DetectedActivity(DetectedActivity.UNKNOWN, 100));
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            handleDetectedActivities(result.getProbableActivities());
            myHandleDetectedActivities(result.getProbableActivities());
        }
    }

    protected void myHandleDetectedActivities(List<DetectedActivity> probableActivities) {

        DetectedActivity currentLeader = new DetectedActivity(DetectedActivity.UNKNOWN, 0);
        for (DetectedActivity activity : probableActivities) {
            if (
                    (
                            (activity.getType() == DetectedActivity.RUNNING) ||
                                    (activity.getType() == DetectedActivity.WALKING) ||
                                    (activity.getType() == DetectedActivity.STILL) ||
                                    (activity.getType() == DetectedActivity.IN_VEHICLE)
                    ) && (activity.getConfidence() >= currentLeader.getConfidence())) {
                currentLeader = activity;
            }
        }

        //  protected void myHandleDetectedActivities(ActivityRecognitionResult result) {
        //        DetectedActivity underConsiderationMostLikely = result.getMostProbableActivity();
        //
        //        if ((underConsiderationMostLikely.getType() == DetectedActivity.RUNNING) ||
        //               /* (underConsiderationMostLikely.getType() == DetectedActivity.ON_FOOT) || */
        //                (underConsiderationMostLikely.getType() == DetectedActivity.WALKING) ||
        //                (underConsiderationMostLikely.getType() == DetectedActivity.STILL) ||
        //                (underConsiderationMostLikely.getType() == DetectedActivity.IN_VEHICLE)) {


        // only create a notification if there is a CHANGE in the activity
        if (currentLeader.getType() == currentActivity.getType()) {
            // do nothing
        } else {
            // we have a new activity
            // lastActivity = currentActivity;
            currentActivity = currentLeader;

            doCurrentActivity(currentActivity);
        }
    }


    public void doCurrentActivity(DetectedActivity activity) {

        switch (activity.getType()) {
            case DetectedActivity.IN_VEHICLE: {
                showToast("In Vehicle");
                Log.e("ActivityRecogition", "--------->InVehicle");
                sendNotification("In Vehicle", R.drawable.in_vehicle);
                break;
            }
            case DetectedActivity.ON_BICYCLE: {

                break;
            }
            case DetectedActivity.ON_FOOT: {
                showToast("Walking");
                Log.e("ActivityRecogition", "--------->Walking");
                sendNotification("Walking", R.drawable.walking);
                break;
            }
            case DetectedActivity.RUNNING: {
                showToast("Running");
                Log.e("ActivityRecogition", "--------->Running");
                sendNotification("Running", R.drawable.running);
                break;
            }
            case DetectedActivity.STILL: {
                showToast("Still");
                Log.e("ActivityRecogition", "--------->Still");
                sendNotification("Still", R.drawable.still);
                break;
            }
            case DetectedActivity.TILTING: {

                break;
            }
            case DetectedActivity.WALKING: {
                showToast("Walking");
                Log.e("ActivityRecogition", "--------->Walking");
                sendNotification("Walking", R.drawable.walking);
                break;
            }
            case DetectedActivity.UNKNOWN: {

                break;
            }
        }
    }


    private void showToast(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void sendNotification(String msg, int smallIcon) {

        // the following creates a Notification which gets sent to the
        // the PendingIntent defines what happens when the user clicks on the notification
        // and in this case the PendingIntent has been defined in MainActivity itself
        // and that is to go to MainActivity
        Resources resources = getResources();
        //  Intent intent = MainActivity.newIntent(this);

       /* zona Intent */
        intent = new Intent(this, MainActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("activity_type", "unmodified"); // caller will modify this


        intent.putExtra("activity_type", msg); // <-- HERE I PUT THE EXTRA VALUE

        // pass the Intent to TaskStackBuilder (Head First pg 559)
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        // PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

       /* zona Notification */
        notification = new NotificationCompat.Builder(this)
                .setTicker(resources.getString(R.string.new_pictures_title))
                .setSmallIcon(smallIcon)
                .setContentTitle(resources.getString(R.string.new_pictures_title))
                .setContentText(resources.getString(R.string.new_pictures_text))
                .setContentIntent(pi)
                .setAutoCancel(false)
                .setOnlyAlertOnce(false)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
        // notif.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notification);
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
//                    if (activity.getConfidence() >= 75) {
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//                        builder.setContentText("Are you walking?");
//                        builder.setSmallIcon(R.mipmap.ic_launcher);
//                        builder.setContentTitle(getString(R.string.app_name));
//                        NotificationManagerCompat.from(this).notify(0, builder.build());
//                    }
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

