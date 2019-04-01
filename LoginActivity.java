package com.example.iqmma.whathappensinthecity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText email, pass;
    private TextView register;
    private Button login;
    private Boolean logedIn = false;

    private FirebaseAuth auth;
    FirebaseUser firebaseUser;
    public static String mailCred;
    public static String passwordCred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        boolean x = true;

        if(x) {
            android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbarLogin);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Login");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            email = findViewById(R.id.editTextEmail);
            pass = findViewById(R.id.editTextPassword);
            login = findViewById(R.id.btnLogin);
            register = findViewById(R.id.textViewRegistration);

            auth = FirebaseAuth.getInstance();

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String txtEmail = email.getText().toString();
                    final String txtPass = pass.getText().toString();

                    if (TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPass))
                        Toast.makeText(LoginActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    else {
                        auth.signInWithEmailAndPassword(txtEmail, txtPass)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            mailCred = txtEmail;
                                            passwordCred = txtPass;
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else{
                                            findViewById(R.id.textViewForgetPassword).setVisibility(View.VISIBLE);
                                            findViewById(R.id.textViewForgetPassword).setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if(TextUtils.isEmpty(txtEmail))
                                                        Toast.makeText(LoginActivity.this, "Please give your email", Toast.LENGTH_SHORT).show();
                                                    else
                                                        resetPassword(txtEmail);
                                                }
                                            });

                                            Toast.makeText(LoginActivity.this, "Email/Password is invalid!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                    }
                }
            });


            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goTo();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null)
            logedIn = true;
    }

    private void goTo(){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void resetPassword(String mail){
        auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Toast.makeText(LoginActivity.this, "Check your E-mail to reset your password", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(LoginActivity.this, "Your E-mail does not exist, Try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}