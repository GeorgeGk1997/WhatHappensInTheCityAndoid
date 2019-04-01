package com.example.iqmma.whathappensinthecity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, email, password;
    private Button register;

    private FirebaseAuth auth;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbarRegistration);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.editTextUsernameRegistration);
        email = findViewById(R.id.editTextEmailRegistration);
        password = findViewById(R.id.editTextPasswordRegistration);
        register = findViewById(R.id.btnRegistration);

        auth = FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String txtUsername = username.getText().toString();
                String txtPass = password.getText().toString();
                String txtEmail = email.getText().toString();

                if (txtUsername.matches("") || txtEmail.matches("") ||
                        txtPass.matches("")){
                    Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
                else if(txtPass.length()<6)
                    Toast.makeText(RegisterActivity.this, "The password must have at least 6 digits", Toast.LENGTH_SHORT).show();
                else{
                    register(txtUsername,txtEmail,txtPass);

                }
            }
        });
    }

    private void register(final String username, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(RegisterActivity.this, "Successful!", Toast.LENGTH_SHORT).show();

                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userID =firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userID);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                    else
                                        Toast.makeText(RegisterActivity.this, "hashUn", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                        else
                            Toast.makeText(RegisterActivity.this, "Uns", Toast.LENGTH_SHORT).show();

                    }
                });

    }
}
