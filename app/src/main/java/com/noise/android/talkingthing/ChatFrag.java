package com.noise.android.talkingthing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatFrag extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference firebaseDatabase;
    private DatabaseReference friends;
    private ListView list_item;
    final private ArrayList<String> uids = new ArrayList<>();
    //private String User;

    //private RecyclerView Userlist;

    public static ChatFrag newInstance() {
        ChatFrag fragment = new ChatFrag();
        return fragment;
    }

    public class Users {

        public String  name;
        public String image;
        public String status;

        public Users(){

        }

        public void setName(String name) {
            this.name = name;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }

        public String getStatus() {
            return status;
        }

        public Users(String name, String image, String status) {
            this.name = name;
            this.image = image;
            this.status = status;
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        list_item = rootView.findViewById((R.id.list_item));

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        friends = firebaseDatabase.child("friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        friends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                final ArrayList<String> names = new ArrayList<>();
                for (DataSnapshot child: children) {
                    uids.add(child.getKey());
                    firebaseDatabase.child("Users").child(child.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String,String> child = (HashMap<String,String>) dataSnapshot.getValue();
                            names.add(child.get("name").toString());
                            ArrayAdapter<String> chat_list = new ArrayAdapter<>(getContext(),R.layout.users_chat_template,names);
                            list_item.setAdapter(chat_list);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        list_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView text = view.findViewById(R.id.Username);
                Intent chat = new Intent(getContext(),Personal_chat_screen.class);
                chat.putExtra("User_id",uids.get(position));
                startActivity(chat);
            }
        });

        return rootView;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
//                Users.class,
//                R.layout.users_chat_template,
//                UsersViewHolder.class,
//                firebaseDatabase
//        ){
//            @Override
//            protected void populateViewHolder(UsersViewHolder view, Users model, int position) {
//                view.setName(model.getName());
//            }
//        };
//
//    }

//    public class UsersViewHolder extends RecyclerView.ViewHolder {
//
//        View view;
//
//        public UsersViewHolder(View itemView) {
//            super(itemView);
//
//            view = itemView;
//        }
//
//        public void setName(String name){
//
//            TextView usernameview = view.findViewById(R.id.Username);
//            usernameview.setText(name);
//        }
//
//
//    }

}