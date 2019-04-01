package com.example.iqmma.whathappensinthecity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private EditText txtUsername, txtPassword, txtEmail;
    private Button btnUpdate;
    private ImageView imgProf;

    private FirebaseUser userF;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private static final int IMG_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtUsername = findViewById(R.id.editTextUsernameProf);
        txtPassword = findViewById(R.id.editTextPasswordProf);
        txtEmail = findViewById(R.id.editTextEmailProf);
        btnUpdate = findViewById(R.id.btnUpdate);
        imgProf = findViewById(R.id.imgViewProfile);

        //disable editTexts
        clickableOrNot(txtUsername, false);
        clickableOrNot(txtPassword, false);
        clickableOrNot(txtEmail, false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                txtUsername.setText(MainActivity.usernameCred);
                txtPassword.setText(LoginActivity.passwordCred);
                txtEmail.setText(LoginActivity.mailCred);

                clickableOrNot(txtUsername, true);
                clickableOrNot(txtPassword, true);

            }
        }, 5000);


        userF = FirebaseAuth.getInstance().getCurrentUser();
        reference =FirebaseDatabase.getInstance().getReference("Users").child(userF.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if(user.getImageURL() != null && !user.getImageURL().equals("default"))
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(imgProf);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!MainActivity.usernameCred.equals(txtUsername.getText().toString()) &&
                                txtEmail.getText().toString() != null)
                            dataSnapshot.getRef().child("username").setValue(txtUsername.getText().toString());
                        if (!LoginActivity.passwordCred.equals(txtPassword.getText().toString()) &&
                                txtPassword.getText().toString() != null)
                            changePassword();


                        Toast.makeText(ProfileActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        imgProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });


    }

    private void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMG_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();

            if(uploadTask != null && uploadTask.isInProgress())
                Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
            else
                uploadImage();
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }



    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getApplicationContext());
        pd.setMessage("Uploading");
        //pd.show();

        if (imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                        throw task.getException();

                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = (Uri)task.getResult();
                        String myUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userF.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", myUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    }else
                    {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });

        }else{
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }



    private void changePassword(){
        userF = FirebaseAuth.getInstance().getCurrentUser();
        if(userF != null){
            userF.updatePassword(txtPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Your password did not changed", Toast.LENGTH_SHORT).show();
                                LoginActivity.passwordCred = txtPassword.getText().toString();
                            }
                        }
                    });
        }
    }


    private void clickableOrNot(EditText editText, boolean clickable){
        if(clickable){
            editText.setEnabled(true);
            editText.setClickable(true);
        }
        else{
            editText.setEnabled(false);
            editText.setClickable(false);
        }
    }

}
