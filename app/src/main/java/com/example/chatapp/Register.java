package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.data.userInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Register extends AppCompatActivity {


    EditText editName,editPassword,editEmail;
    TextView tvBackTologin;
    Button btnReagister;

    private FirebaseAuth mAuth;

    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editEmail=findViewById(R.id.editTextRegEmaill);
        editPassword=findViewById(R.id.editTextRegPassword);
        editName=findViewById(R.id.editTextRegName);
        tvBackTologin=findViewById(R.id.TVbackToLogin);
        btnReagister=findViewById(R.id.btnRegister);


        mAuth=FirebaseAuth.getInstance();

        databaseReference=FirebaseDatabase.getInstance().getReference("userdetails");

        //onclick listenerss
        btnReagister.setOnClickListener(view->register());

       tvBackTologin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(getApplicationContext(), Login.class);
               startActivity(intent);
           }
       });

    }

    public void register(){
        String name=editName.getText().toString().trim();
        String email=editEmail.getText().toString().trim();
        String password=editPassword.getText().toString().trim();

        if(name.isEmpty()){
            editName.setError("Enter a valid name");
            editName.requestFocus();
            return;
        }
        if(email.isEmpty()|| !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Enter a valid email");
            editEmail.requestFocus();
            return;
        }
        if(password.length()<6){
            editPassword.setError("Password must be atleast 6 characters");
            editPassword.requestFocus();
        }


        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        userInfo user = new userInfo(name, email, password);
                        databaseReference.child(email).setValue(user);
                        Toast.makeText(Register.this, "registred", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });




       // Toast.makeText(this, "user registered", Toast.LENGTH_SHORT).show();
    }








}