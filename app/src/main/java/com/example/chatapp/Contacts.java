package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.chatapp.data.userInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Contacts extends AppCompatActivity {

    private RecyclerView contactsRecyclerView;
    private DatabaseReference contactsRef;
    private ContactsAdapter contactsAdapter;
    private List<userInfo> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactsRecyclerView = findViewById(R.id.rvContacts);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        contactsAdapter = new ContactsAdapter(userList);
        contactsRecyclerView.setAdapter(contactsAdapter);

        contactsRef = FirebaseDatabase.getInstance().getReference("userdetails");

        contactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    String name = contactSnapshot.child("name").getValue(String.class);
                    String email = contactSnapshot.child("email").getValue(String.class);
                    userInfo userInfo = new userInfo(name, email, "");
                    userList.add(userInfo);
                }

                contactsAdapter.setOnItemClickListener(new ContactsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(userInfo user) {
                        // Add logging for debugging
                        Log.d("ChatApp", "Clicked on user: " + user.getName());

                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        intent.putExtra("recipientName", user.getEmail());
                        startActivity(intent);
                    }
                });

                contactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Contacts.this, "Failed to load user list", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
