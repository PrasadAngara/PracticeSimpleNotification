package com.example.practicesimplenotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

mauth=FirebaseAuth.getInstance();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult().getToken();
                            saveToken(token);
                        } else {
Toast.makeText(ProfileActivity.this,"error",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mauth.getCurrentUser()==null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    public void saveToken(String token){

        String email=mauth.getCurrentUser().getEmail();
        User user=new User(email,token);
        DatabaseReference dbusers= FirebaseDatabase.getInstance().getReference("users");
        dbusers.child(mauth.getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ProfileActivity.this,"Token Saved",Toast.LENGTH_LONG).show();
            }
        });

    }
}