package com.noise.android.talkingthing;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Sourabh on 08/03/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{


    private List<Messages> mMessageList;
    private FirebaseAuth current_user;
    private DatabaseReference mUserDatabase;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_bubble ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
//        public TextView displayName;

        public MessageViewHolder(View view) {
            super(view);

            messageText = view.findViewById(R.id.message_text);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        String current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Messages c = mMessageList.get(i);
        String from_user =c.getFrom();
        viewHolder.messageText.setText(c.getMessage());

        if(from_user.equals(current_user_id)){

            viewHolder.messageText.setBackgroundColor(Color.WHITE);
            viewHolder.messageText.setGravity(Gravity.RIGHT);

        }
        else{
            viewHolder.messageText.setBackgroundColor(R.drawable.message_bubble_background);
            viewHolder.messageText.setGravity(Gravity.LEFT);
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
