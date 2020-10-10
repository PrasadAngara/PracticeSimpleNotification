package com.example.practicesimplenotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "simple noti";
    private static final String CHANNEL_NAME = "simple noti";
    private static final String CHANNEL_DESC = "simple noti";
    private EditText eusername, epassword;
    private Button signbutton;
    private ProgressBar progressBar;
    private FirebaseAuth mauth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mauth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);

            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);


        }

        eusername = findViewById(R.id.username);
        epassword = findViewById(R.id.password);
        signbutton = findViewById(R.id.signup);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);
        signbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUSer();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mauth.getCurrentUser() != null) {
            startProfileActivity();
        }
    }

    public void createUSer() {
        final String uname = eusername.getText().toString().trim();
        final String upass = epassword.getText().toString().trim();
        if (uname.isEmpty() && upass.isEmpty()) {
            eusername.setError("username required");
            eusername.requestFocus();
            return;
        } else {
            progressBar.setVisibility(View.VISIBLE);
            mauth.createUserWithEmailAndPassword(uname, upass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startProfileActivity();
                            } else {
                                Log.d("here", "signup");
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                    userLogin(uname, upass);
                                    Log.d("here", "signin");

                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                }
                            }


                        }
                    });
        }

    }

    private void userLogin(final String uname, final String upass) {
        mauth.signInWithEmailAndPassword(uname, upass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startProfileActivity();
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    private void startProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

//    public void diplayNotification(View view) {
//        NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Notification").setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentText("Notofication coing")
//                .setSmallIcon(R.drawable.ic_baseline_local_post_office_24);
//
//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
//        notificationManagerCompat.notify(1, mbuilder.build());
//
//    }
}