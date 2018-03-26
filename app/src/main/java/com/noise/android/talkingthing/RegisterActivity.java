package com.noise.android.talkingthing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    //Declaring the objects
    private Button Signup;
    private EditText Email_Reg;
    private EditText Password_1_Reg;
    private EditText Password_2_Reg;
    private EditText Username_Reg;

    private ProgressDialog progressdialog_Reg;

    private FirebaseAuth firebaseAuth_Reg;
    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initializing objects
        Signup = findViewById(R.id.Signup);
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = Username_Reg.getText().toString().trim();
                String email = Email_Reg.getText().toString().trim();
                String password1 = Password_1_Reg.getText().toString().trim();
                String password2 = Password_2_Reg.getText().toString().trim();

                SignupUser(username, email, password1, password2);
            }
        });

        Username_Reg = findViewById(R.id.Username_Reg);
        Email_Reg = findViewById(R.id.Email_Reg);
        Password_1_Reg = findViewById(R.id.Password_1_Reg);
        Password_2_Reg = findViewById(R.id.Password_2_Reg);

        progressdialog_Reg = new ProgressDialog(this);

        firebaseAuth_Reg = FirebaseAuth.getInstance();
    }

    private void SignupUser(String username, String email, String password1, String password2) {

            //Check emptiness of inputs
            if (TextUtils.isEmpty(username) | TextUtils.isEmpty(password1) | TextUtils.isEmpty(email) | TextUtils.isEmpty(password2)) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            //Check if password1 and password2 are the same
            if (!TextUtils.equals(password1, password2)) {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                return;
            }

        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference reference = firebaseDatabase.getReference();
                        reference.child("Users")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Get Post object and use the values to update the UI
                                        final Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                                        for (DataSnapshot child: children) {
                                            HashMap<String,String> values = (HashMap<String, String>) child.getValue();
                                            if(values.get("name").equals(Username_Reg.getText().toString())){
                                                Toast.makeText(RegisterActivity.this, "Username taken", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Getting Post failed, log a message
                                        // ...
                                    }
                                });

            //Inputs are valid apparently
            progressdialog_Reg.setMessage("Signing up, please wait.");
            progressdialog_Reg.setCancelable(true);
            progressdialog_Reg.show();

            reg(username,email,password1);
    }

    private void reg(final String username,final String email,String password1) {
        firebaseAuth_Reg.createUserWithEmailAndPassword(email, password1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
                            String UID = User.getUid();

                            DatabaseReference firebaseDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);

                            HashMap<String, String> UsersData = new HashMap<>();
                            UsersData.put("name", username);
                            UsersData.put("email",email);
                            UsersData.put("status", "Active");

                            firebaseDatabase1.setValue(UsersData);

                            progressdialog_Reg.dismiss();
                            Toast.makeText(RegisterActivity.this, "Registration complete", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        } else {
                            progressdialog_Reg.dismiss();
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(RegisterActivity.this, "Email address taken", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


/*            FirebaseUser user = firebaseAuth_Reg.getCurrentUser();

        if(!user.isEmailVerified()){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(RegisterActivity.this, "Verfication Email Sent", Toast.LENGTH_SHORT).show();
                }
            });
        }
*/

}

