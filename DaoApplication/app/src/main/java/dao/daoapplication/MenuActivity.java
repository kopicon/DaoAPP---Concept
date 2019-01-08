package dao.daoapplication;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dao.daoapplication.Entities.DischargedScooter;
import dao.daoapplication.Entities.RoutePackage;
import dao.daoapplication.Entities.Scooter;
import dao.daoapplication.Entities.UndeliveredNewspaper;
import dao.daoapplication.Entities.Package;
import dao.daoapplication.MenuActivities.CarriersActivity;
import dao.daoapplication.MenuActivities.ProblemsListActivity;
import dao.daoapplication.MenuActivities.RouteListMapActivity;
import dao.daoapplication.MenuActivities.RouteStatusActivity;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";

    private Button btnRouteStatus;
    private Button btnRouteListMap;
    private Button btnCarriers;
    private Button btnIssuesList;
    private Button btnLogout;

    public static boolean mLocationPermissionGranted = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private SharedPreferences locationPref;
    private SharedPreferences.Editor editor;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private String id_done = "channel_done";
    private String id_DS = "channel_DischargedS";
    private String id_Newspapers = "channel_newspapers";
    private String id_packages = "channel_package";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnRouteStatus = findViewById(R.id.btn_RouterStatus);
        btnRouteListMap = findViewById(R.id.btn_RouteListMap);
        btnCarriers = findViewById(R.id.btn_Carriers);
        btnIssuesList = findViewById(R.id.btn_IssuesList);
        btnLogout = findViewById(R.id.btn_Logout);
        locationPref = getSharedPreferences("Location",MODE_PRIVATE);
        editor = locationPref.edit();

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        getLocationPermission();
        sendNotification();
        setupNotificationChannels();


        btnRouteStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this,RouteStatusActivity.class);
                startActivity(i);
            }
        });
        btnRouteListMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this,RouteListMapActivity.class);
                startActivity(i);
            }
        });
        btnCarriers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this,CarriersActivity.class);
                startActivity(i);
            }
        });
        btnIssuesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this,ProblemsListActivity.class);
                startActivity(i);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MenuActivity.this);
                dialog.setContentView(R.layout.dialog_are_you_sure_logout);
                Button btnLogoutYes = dialog.findViewById(R.id.btn_dialog_logout_yes);
                Button btnLogoutNo = dialog.findViewById(R.id.btn_dialog_logout_no);
                btnLogoutNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btnLogoutYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: Getting LocationPermission");
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                editor.putBoolean("LocationPermission",mLocationPermissionGranted);
                editor.apply();
            }else{
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    private void sendNotification(){
        mRef.child("RouteStatuses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot DSRP) {
                for (DataSnapshot cRP : DSRP.getChildren()) {
                    RoutePackage cRoutePackage = DSRP.child(cRP.getKey()).getValue(RoutePackage.class);
                    if(!cRoutePackage.isNotified() && cRoutePackage.isFinished()){
                        String massageBody = cRoutePackage.getWorkerName();
                        String title = cRoutePackage.getName();
                        Intent intent = new Intent(getApplicationContext(), RouteStatusActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                        builder.setContentText(massageBody+" Finnished");
                        builder.setContentIntent(pendingIntent);
                        builder.setContentTitle(title);
                        builder.setAutoCancel(true);
                        builder.setSmallIcon(R.drawable.ic_tick);
                        builder.setStyle(new NotificationCompat.BigPictureStyle());
                        builder.setChannelId(id_done);
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(1, builder.build());
                        mRef.child("RouteStatuses").child(cRP.getKey()).child("notified").setValue(true);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRef.child("Problems").child("DischargedScooters").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot cS :dataSnapshot.getChildren()){
                    DischargedScooter cDS = dataSnapshot.child(cS.getKey()).getValue(DischargedScooter.class);
                    if(!cDS.isNotified()){
                        String massageBody = cDS.getCarrierName();
                        String title = cDS.getRoutePackage();
                        Intent intent = new Intent(getApplicationContext(), ProblemsListActivity.class);
                        intent.putExtra("Problem","Scooter");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                        if(!cDS.isFinished()){
                            builder.setContentText(massageBody+" Discharged Scooter / NOT FINISHED!!");
                        }else if(cDS.isFinished()){
                            builder.setContentText(massageBody+" Discharged Scooter / Finished!!");
                        }
                        builder.setContentIntent(pendingIntent);
                        builder.setContentTitle(title);
                        builder.setAutoCancel(true);
                        builder.setSmallIcon(R.drawable.ic_scooter_red);
                        builder.setStyle(new NotificationCompat.BigPictureStyle());
                        builder.setChannelId(id_DS);
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(1, builder.build());
                        mRef.child("Problems").child("DischargedScooters").child(cS.getKey()).child("notified").setValue(true);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRef.child("Problems").child("MissingNewspapers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot cmn :dataSnapshot.getChildren()){
                    UndeliveredNewspaper cMN = dataSnapshot.child(cmn.getKey()).getValue(UndeliveredNewspaper.class);
                    if(!cMN.isNotified()){
                        String massageBody = cMN.getAddress();
                        String title = cMN.getNewspapers().toString();
                        Intent intent = new Intent(getApplicationContext(), ProblemsListActivity.class);
                        intent.putExtra("Problem","MissingNewspaper");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                        builder.setContentText("Missing newspaper "+massageBody);
                        builder.setContentIntent(pendingIntent);
                        builder.setContentTitle(title);
                        builder.setAutoCancel(true);
                        builder.setSmallIcon(R.drawable.ic_missing_news);
                        builder.setStyle(new NotificationCompat.BigPictureStyle());
                        builder.setChannelId(id_Newspapers);
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(1, builder.build());
                        mRef.child("Problems").child("MissingNewspapers").child(cmn.getKey()).child("notified").setValue(true);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRef.child("Problems").child("Package").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot cp :dataSnapshot.getChildren()){
                    Package cP = dataSnapshot.child(cp.getKey()).getValue(Package.class);
                    if(!cP.isNotified()){
                        String massageBody = cP.getName();
                        String title = cP.getPackagNumber();
                        Intent intent = new Intent(getApplicationContext(), ProblemsListActivity.class);
                        intent.putExtra("Problem","Package");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                        builder.setContentText("Problems with package "+massageBody);
                        builder.setContentIntent(pendingIntent);
                        builder.setContentTitle(title);
                        builder.setAutoCancel(true);
                        builder.setSmallIcon(R.drawable.ic_package_red);
                        builder.setStyle(new NotificationCompat.BigPictureStyle());
                        builder.setChannelId(id_packages);
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(1, builder.build());
                        mRef.child("Problems").child("Package").child(cp.getKey()).child("notified").setValue(true);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRef.child("Problems").child("Scooter").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot cs :dataSnapshot.getChildren()){
                    Scooter cS= dataSnapshot.child(cs.getKey()).getValue(Scooter.class);
                    if(!cS.isNotified()){
                        String massageBody = cS.getCarrierName();
                        String title = cS.getScooterNumber();
                        Intent intent = new Intent(getApplicationContext(), ProblemsListActivity.class);
                        intent.putExtra("Problem","Scooter");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                        builder.setContentText("Scooter Failure "+massageBody);
                        builder.setContentIntent(pendingIntent);
                        builder.setContentTitle(title);
                        builder.setAutoCancel(true);
                        builder.setSmallIcon(R.drawable.ic_scooter_red);
                        builder.setStyle(new NotificationCompat.BigPictureStyle());
                        builder.setChannelId(id_packages);
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(1, builder.build());
                        mRef.child("Problems").child("Scooter").child(cs.getKey()).child("notified").setValue(true);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setupNotificationChannels(){
        NotificationManager mNotificationManager = getSystemService(NotificationManager.class);

        CharSequence name = getString(R.string.common_google_play_services_notification_channel_name);

        String description = getString(R.string.app_name);
        int importance = NotificationManager.IMPORTANCE_HIGH;

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build();

        Uri sounduri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationChannel channelDone = new NotificationChannel(id_done, name,importance);
        NotificationChannel channelDS = new NotificationChannel(id_DS,name,importance);
        NotificationChannel channelNewspapers = new NotificationChannel(id_Newspapers,name,importance);
        NotificationChannel channelPackages = new NotificationChannel(id_packages,name,importance);

        channelPackages.setDescription(description);
        channelPackages.enableLights(true);
        channelPackages.setLightColor(Color.RED);
        channelPackages.enableVibration(true);
        channelPackages.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channelPackages.setSound(sounduri,audioAttributes);
        channelPackages.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(channelPackages);

        channelNewspapers.setDescription(description);
        channelNewspapers.enableLights(true);
        channelNewspapers.setLightColor(Color.RED);
        channelNewspapers.enableVibration(true);
        channelNewspapers.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channelNewspapers.setSound(sounduri,audioAttributes);
        channelNewspapers.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(channelNewspapers);

        channelDS.setDescription(description);
        channelDS.enableLights(true);
        channelDS.setLightColor(Color.RED);
        channelDS.enableVibration(true);
        channelDS.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channelDS.setSound(sounduri,audioAttributes);
        channelDS.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(channelDS);

        channelDone.setDescription(description);
        channelDone.enableLights(true);
        channelDone.setLightColor(Color.GREEN);
        channelDone.enableVibration(true);
        channelDone.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        channelDone.setSound(sounduri,audioAttributes);
        channelDone.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(channelDone);
    }
}
