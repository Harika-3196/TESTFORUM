package com.noise.android.talkingthing;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestsFrag extends Fragment {
    public static RequestsFrag newInstance() {
        RequestsFrag fragment = new RequestsFrag();
        return fragment;
    }

    private EditText search_bar;
    private String username;
    private Button search;
    private ListView list_item;

    public class Users {

        public String  name;
        public String email;
        public String image;
        public String status;

        public Users(){

        }

        public void setName(String name) {
            this.name = name;
        }

        public void setEmail(String email) {
            this.email = email;
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

        public String getEmail() {
            return email;
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

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference friend_data;

    private FirebaseUser User;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebaseDatabase = FirebaseDatabase.getInstance();

        friend_data = FirebaseDatabase.getInstance().getReference().child("Friend_requests");

        final DatabaseReference reference = firebaseDatabase.getReference();

        final List<String> values = new ArrayList<String>();

        final View rootview = inflater.inflate(R.layout.fragment_requests, container, false);
        search_bar = rootview.findViewById(R.id.search_bar);

        list_item = rootview.findViewById(R.id.list_item);

        search = rootview.findViewById(R.id.search_btn);

        search.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        username = search_bar.getText().toString();
                        if(TextUtils.isEmpty(username)) {
                            Toast.makeText(getActivity(), "Please fill in a username", Toast.LENGTH_SHORT).show();
                            return;
                        }

               reference.child("Users")
                       .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        final Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child: children) {
                            HashMap<String,String> values = (HashMap<String, String>) child.getValue();
                            if(values.get("name").equals(username)){
                                Intent lol = new Intent(getContext(), Other_Home.class).putExtra("name",username);
                                lol.putExtra("user_id",child.getKey().toString());
                                getActivity().startActivity(lol);
                            }
//                            Toast.makeText(getActivity(), values.get("email"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        // ...
                        Toast.makeText(getContext(), "No such user exists", Toast.LENGTH_SHORT).show();
                    }
                });

                        ArrayAdapter<String> valueAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.activity_list_item,values);

                    }
                }
        );

        friend_data.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        ArrayList<String> names = new ArrayList<>();
                        for (DataSnapshot child: children) {
                            if(child.getValue().equals("received")) {
                                DatabaseReference name_ref = reference.child("Users").child(child.getKey()).child("name");
                                names.add(name_ref.toString());
                            }
                            Toast.makeText(getContext(), child.getKey(), Toast.LENGTH_SHORT).show();
                        }
                        ArrayAdapter<String> Requests = new ArrayAdapter<String>(getContext(), R.layout.fragment_requests, names);
                        list_item.setAdapter(Requests);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), "No requests", Toast.LENGTH_SHORT).show();
                    }
                });


        return rootview;
    }
}