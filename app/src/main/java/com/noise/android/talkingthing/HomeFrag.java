package com.noise.android.talkingthing;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.name;
import static android.R.attr.onClick;
import static com.noise.android.talkingthing.R.id.Register;
import static com.noise.android.talkingthing.R.id.Signin;
import static com.noise.android.talkingthing.R.id.signout;
import static com.noise.android.talkingthing.R.id.start;


public class HomeFrag extends android.support.v4.app.Fragment {

    private Button signout;
    private FirebaseAuth firebaseAuth;
    private TextView username;
    private TextView status;
    private TextView email;
    private TextView change_email_button;
    private TextView change_username_button;

    public HomeFrag() {
    }

    public static HomeFrag newInstance() {
        HomeFrag fragment = new HomeFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        final Button signout = (Button) rootView.findViewById(R.id.signout);
        final TextView change_email_button = rootView.findViewById(R.id.change_email_button);
        final TextView change_username_button = rootView.findViewById(R.id.change_username_button);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        String email_db = user.getEmail();
        email = rootView.findViewById(R.id.Email);
        email.setText(email_db);
        //Bringing in User information from the database
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                username = rootView.findViewById(R.id.Username_changeable);
                username.setText(name);

                String status_string = dataSnapshot.child("status").getValue().toString();
                status = rootView.findViewById(R.id.status_changeable);
                status.setText(status_string);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Unable to retrive user data", Toast.LENGTH_SHORT).show();
            }
        });

        change_email_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startActivity(new Intent(getContext(),Emailpopup.class));
            }
        });

        change_username_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startActivity(new Intent(getContext(),Usernamepopup.class));
            }
        });

        signout.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        if (view.getId() == R.id.signout) {
                            String User = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference firebaseDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users").child(User);

                            HashMap<String, String> UsersData = new HashMap<>();
                            UsersData.put("name", username.getText().toString());
                            UsersData.put("email",email.getText().toString());
                            UsersData.put("status", "Offline");

                            firebaseDatabase1.setValue(UsersData);

                            firebaseAuth.getInstance().signOut();
                            getActivity().startActivity(new Intent(getContext(), MainActivity.class));
                            onDestroyView();
                        }

                    }
                }
        );
        return rootView;
    }
}

