package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.chatapp.data.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private Button buttonSend;
    private DatabaseReference usersRef;
    private DatabaseReference messagesRef;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        spinnerUsers = findViewById(R.id.spinnerUsers);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        recyclerView = findViewById(R.id.recyclerView);

        usersRef = FirebaseDatabase.getInstance().getReference("userdetails");
        messagesRef = FirebaseDatabase.getInstance().getReference("messages");

        buttonSend.setOnClickListener(view -> sendMessageToRecipient());

        loadUserListForSelection();
        loadConversationMessages();
    }

    private void loadConversationMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Message> messageList = new ArrayList<>();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String recipient = messageSnapshot.child("recipient").getValue(String.class);
                    String messageContent = messageSnapshot.child("message").getValue(String.class);
                    Message message = new Message(recipient, messageContent);
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
    }


    private void loadUserListForSelection() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> userList = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userName = userSnapshot.child("name").getValue(String.class); // Assuming "name" is the field containing the user's name
                    userList.add(userName);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ChatActivity.this, android.R.layout.simple_spinner_dropdown_item, userList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Set the dropdown view resource
                spinnerUsers.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Failed to load user list", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void sendMessageToRecipient() {
        String message = editTextMessage.getText().toString().trim();
        String recipientName = spinnerUsers.getSelectedItem().toString();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("messages");
        String messageId = databaseReference.push().getKey();

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("recipient", recipientName);
        messageData.put("message", message);

        databaseReference.child(messageId).setValue(messageData)
                .addOnSuccessListener(aVoid -> Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                });
    }
}
