package dev.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {

    TextView name,email,logedinon;
    Button LogOut;
    CircleImageView profilepic;

    FirebaseAuth mAuth;
    String onlineId = " ";
    DatabaseReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);


        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        LogOut = findViewById(R.id.layout);
        logedinon = findViewById(R.id.logedinon);

        profilepic = findViewById(R.id.Profile_image);

        mAuth = FirebaseAuth.getInstance();
        onlineId = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(onlineId);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                logedinon.setText(snapshot.child("logedinon").getValue().toString());
                name.setText(snapshot.child("name").getValue().toString());
                email.setText(snapshot.child("email").getValue().toString());

                Glide.with(AccountActivity.this).load(snapshot.child("profilepictureurl").getValue().toString()).into(profilepic);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//     LogOut.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View v) {
//             FirebaseAuth.getInstance().signOut();
//             Intent i = new Intent(AccountActivity.this,LoginActivity.class);
//             startActivity(i);
//         }
//     });
    }
}