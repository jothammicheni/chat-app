package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.chatapp.data.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private Spinner spinnerUsers;
    private EditText editTextMessage;
    private Button buttonSend, buttonNewchat;
    private DatabaseReference usersRef;
    private DatabaseReference messagesRef;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
       buttonNewchat=findViewById(R.id.btnNewChat);
        spinnerUsers = findViewById(R.id.spinnerUsers);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        recyclerView = findViewById(R.id.recyclerView);

        usersRef = FirebaseDatabase.getInstance().getReference("userdetails");
        messagesRef = FirebaseDatabase.getInstance().getReference("messages");

        buttonSend.setOnClickListener(view -> sendMessageToRecipient());

        buttonNewchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), Contacts.class);
                startActivity(intent);
            }
        });

       // loadUserListForSelection();

        Log.d("ChatApp", "ChatActivity onCreate");


        if (getIntent().hasExtra("recipientName")) {
            String recipientName = getIntent().getStringExtra("recipientName");
            Log.d("ChatApp", "Recipient Name: " + recipientName);
            loadConversationMessages();
        } else {
            Toast.makeText(this, "Recipient not specified", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void loadConversationMessages() {
        FirebaseAuth mAuth;
        FirebaseUser user;



        if (getIntent().hasExtra("recipientName")) {
            String recipientName = getIntent().getStringExtra("recipientName");
            messagesRef.orderByChild("recipient").equalTo(recipientName)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<Message> messageList = new ArrayList<>();
                            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                String messageContent = messageSnapshot.child("message").getValue(String.class);
                                String senderName= messageSnapshot.child("sender").getValue(String.class);

                                Message message = new Message(messageContent,senderName,recipientName);
                                messageList.add(message);
                            }

                            messageAdapter = new MessageAdapter(messageList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                            recyclerView.setAdapter(messageAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(ChatActivity.this, "Failed to load conversation messages", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle the case where "recipientName" is not provided
            Toast.makeText(this, "Recipient not specified", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if necessary
        }
    }






    private void sendMessageToRecipient() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser!=null) {
            String message = editTextMessage.getText().toString().trim();
            String email=currentUser.getEmail();

            //   String recipientName = spinnerUsers.getSelectedItem().toString();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("messages");
            String messageId = databaseReference.push().getKey();

            Map<String, Object> messageData = new HashMap<>();

            if (getIntent().hasExtra("recipientName")) {
                String recipientName = getIntent().getStringExtra("recipientName");
                messageData.put("recipient", recipientName);
                messageData.put("message", message);
                messageData.put("sender", email);

            }


            databaseReference.child(messageId).setValue(messageData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    });
        }else{
            Toast.makeText(this, "Login first", Toast.LENGTH_SHORT).show();
        }
    }

}
