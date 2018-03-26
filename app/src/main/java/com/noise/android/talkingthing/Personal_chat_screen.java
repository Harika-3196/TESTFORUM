package com.noise.android.talkingthing;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Personal_chat_screen extends AppCompatActivity {

    private DatabaseReference firebaseDatabase;
    private DatabaseReference databaseReference;
    private EditText input_message;
    private ImageButton send_button;
    private RecyclerView list_of_messages;
    private final List<Messages> messagelist = new ArrayList<>();
    private LinearLayoutManager linearlayout;
    private MessageAdapter messageAdapter;

    private String target_user;
    private String current_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat_screen);

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        target_user = getIntent().getStringExtra("User_id");
        current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        input_message = findViewById(R.id.input_message);
        send_button = findViewById(R.id.send_button);

        messageAdapter = new MessageAdapter(messagelist);

        list_of_messages = findViewById(R.id.list_of_messages);

        linearlayout = new LinearLayoutManager(this);
        list_of_messages.setHasFixedSize(true);
        list_of_messages.setLayoutManager(linearlayout);

        list_of_messages.setAdapter(messageAdapter);

        loadmessages();

        firebaseDatabase.child("Users").child(target_user)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String,String> child = (HashMap<String,String>) dataSnapshot.getValue();
                        getSupportActionBar().setTitle(child.get("name"));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        databaseReference = firebaseDatabase.child("Chats");
        databaseReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChild(target_user)){
                            Map chatAddMap = new HashMap();
                            chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                            Map chatUserMap = new HashMap();
                            chatUserMap.put("Chat/" + current_user + "/" + target_user,chatAddMap);
                            chatUserMap.put("Chat/" + target_user + "/" + current_user,chatAddMap);

                            firebaseDatabase.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    if(databaseError != null){
                                        Toast.makeText(Personal_chat_screen.this, "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                list_of_messages.scrollToPosition(messagelist.size() + 1);

                String message = input_message.getText().toString();

                if(!TextUtils.isEmpty(message)){
                    String for_current_user = "message/" + current_user + "/" + target_user;
                    String for_target_user = "message/" + target_user + "/" + current_user;

                    DatabaseReference store_message = firebaseDatabase.child("messages").child(current_user).child(target_user).push();

                    String push_id = store_message.getKey();

                    Map messagemap = new HashMap();
                    messagemap.put("message", message);
                    messagemap.put("timestamp",ServerValue.TIMESTAMP);
                    messagemap.put("from", current_user);

                    Map message_user_map = new HashMap();
                    message_user_map.put(for_current_user + "/" + push_id,messagemap);
                    message_user_map.put(for_target_user + "/" + push_id,messagemap);

                    firebaseDatabase.updateChildren(message_user_map, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            input_message.setText("");
                        }
                    });
                }
            }
        });

    }

    private void loadmessages() {
        firebaseDatabase.child("message").child(current_user).child(target_user).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);
                messagelist.add(message);
                messageAdapter.notifyDataSetChanged();

                list_of_messages.scrollToPosition(messagelist.size() + 1);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
