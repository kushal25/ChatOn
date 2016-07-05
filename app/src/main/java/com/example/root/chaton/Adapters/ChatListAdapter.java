package com.example.root.chaton.Adapters;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.root.chaton.R;
import com.example.root.chaton.beans.Chat;
import com.firebase.client.Query;

public class ChatListAdapter extends FirebaseListAdapter<Chat> {

    private String number;

    public ChatListAdapter(Query ref, Activity activity, int layout, String number) {
        super(ref, Chat.class, layout, activity);
        this.number = number;
    }


    @Override
    protected void populateView(View view, Chat chat) {
        // Map a Chat object to an entry in our listview
        	String num = chat.getSender();
        	TextView ch = (TextView) view.findViewById(R.id.tv_chat);
        	TextView sent = (TextView) view.findViewById(R.id.tv_time);

        if (num.equals(number)) {
        	ch.setText(chat.getMessage());
        	ch.setBackgroundResource(R.drawable.bubble_a);
        	ch.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        	ch.setPadding(250, 5, 40, 15);
        	sent.setText(chat.getSent_time());
        	sent.setTextSize(11);
        	sent.setPadding(10, 10, 5, 0);	
        }
         else {
        	ch.setText(chat.getMessage());
        	ch.setBackgroundResource(R.drawable.bubble_b);
        	ch.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        	ch.setPadding(40, 20, 250, 15);
        	sent.setText(chat.getSent_time());
        	sent.setTextSize(11);
        	sent.setPadding(500, 20, 5, 0);
        }
    }
}
