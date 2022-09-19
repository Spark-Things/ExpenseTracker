package dev.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registeration extends AppCompatActivity {
//    private Toolbar toolbar;
    private EditText RegisterEmail,RegisterPassword;
    private Button RegisterBtn;
    private TextView LoginOn;
    private FirebaseAuth mAuth;

    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registeration);

        RegisterEmail = findViewById(R.id.RegistatationEmail);
        RegisterPassword = findViewById(R.id.RegistatationPassword);
        RegisterBtn = findViewById(R.id.RegistatationBtn);
        LoginOn = findViewById(R.id.LoginOn);

        mAuth = FirebaseAuth.getInstance();


        loader = new ProgressDialog(this);

//        toolbar = findViewById(R.id.RegistatationToolBar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Register");

        LoginOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Registeration.this,LoginActivity.class);
                startActivity(i);
            }
        });

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = RegisterEmail.getText().toString().trim();
                String password = RegisterPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    RegisterEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    RegisterPassword.setError("Password Reqired");
                    return;
                }else {
                    loader.setMessage("Registratiomn in process");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent i = new Intent(Registeration.this, HomeActivity.class);
                                startActivity(i);
                                finish();
                                loader.dismiss();
                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(Registeration.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}