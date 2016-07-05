package com.example.root.chaton.activity;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

        import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
        import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

import com.example.root.chaton.Adapters.ChatListAdapter;
import com.example.root.chaton.Adapters.CustomAdapter;
import com.example.root.chaton.R;
import com.example.root.chaton.beans.Chat;
import com.example.root.chaton.db.ChatBean;
import com.example.root.chaton.db.ChatModel;
import com.example.root.chaton.helpers.ConnectionDetector;
import com.firebase.client.DataSnapshot;
        import com.firebase.client.Firebase;
        import com.firebase.client.FirebaseError;
        import com.firebase.client.ValueEventListener;

@SuppressLint("InflateParams")
public class UserActivity extends Activity {
    private Firebase ref;
    private String url = "https://kushal.firebaseio.com/";
    public String key;
    private ChatListAdapter chatListAdapter;
    public String num, myNo, user;
    private ValueEventListener connectedListener;
    private CustomAdapter customAdapter;
    private ArrayList<ChatBean> chats = new ArrayList<>();
    ListView lv;
    @SuppressLint({ "InflateParams", "SimpleDateFormat" })
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Firebase.setAndroidContext(this);
        lv = (ListView) findViewById(R.id.list);
        user = getIntent().getExtras().getString("user");
        num = getIntent().getExtras().getString("num");
        myNo = getIntent().getExtras().getString("myNo");
        key = numberSorting(myNo,num);
        ref = new Firebase(url).child("zChats");
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        LayoutInflater title_inflater = LayoutInflater.from(this);
        View customView = title_inflater.inflate(R.layout.action_bar, null);
        getActionBar().setCustomView(customView);
        getActionBar().setDisplayShowCustomEnabled(true);
        TextView tv = (TextView) customView.findViewById(R.id.Title);
        tv.setText(user);
        tv.setPadding(160, 15, 0, 15);

        final ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        final Boolean isInternetPresent = cd.isConnectingToInternet();

        if (!isInternetPresent) {
            cd.showAlertDialog(UserActivity.this, "No Internet Connection",
                    "Press Ok to redirect to Network Connectionsß", false);
        }

        ChatModel cm = new ChatModel(getApplicationContext());
        chats = cm.getChats(key);
        customAdapter = new CustomAdapter(UserActivity.this,chats,myNo);
        lv.setAdapter(customAdapter);
        lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lv.setStackFromBottom(true);

//        chatListAdapter = new ChatListAdapter(list, this, R.layout.list_chat, myNo);
//        lv.setAdapter(chatListAdapter);
//        lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//        lv.setStackFromBottom(true);

        ChatModel.closeDBConnection(cm);

//        final ListView lv = getListView();
        chatListAdapter = new ChatListAdapter(ref.child(key).limitToLast(100), this, R.layout.list_chat, myNo);
        lv.setAdapter(chatListAdapter);
        lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lv.setStackFromBottom(true);


        connectedListener = ref.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Chat post = postSnapshot.getValue(Chat.class);
                    System.out.println(post.getMessage() + " - " + post.getSender());
                    Toast.makeText(getApplicationContext(),post.getMessage().toString(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "Failed. Try Again!", Toast.LENGTH_SHORT).show();

            }
        });

        ref.getRoot().child(".info/connected").removeEventListener(connectedListener);

        //getMessage(key);

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
                final Boolean isInternetPresent = cd.isConnectingToInternet();

                if (!isInternetPresent) {
                    cd.showAlertDialog(UserActivity.this, "No Internet Connection",
                            "Press Ok to redirect to Network Connections", false);
                }
                else
                {
                    sendMessage();
                }
            }
        });
    }

    private void getMessage(String chatId)
    {
        ChatModel cm = new ChatModel(getApplicationContext());
        chats = cm.getChats(chatId);
        lv.setAdapter(customAdapter);
        lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lv.setStackFromBottom(true);
//        chatListAdapter = new ChatListAdapter(list, this, R.layout.list_chat, myNo);
//        lv.setAdapter(chatListAdapter);
//        lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//        lv.setStackFromBottom(true);

        ChatModel.closeDBConnection(cm);

    }

    @SuppressLint("SimpleDateFormat")
    private void sendMessage() {
        EditText inputText = (EditText)findViewById(R.id.messageInput);
        String input = inputText.getText().toString();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat tim = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String tym = tim.format(c.getTime());

        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, myNo, tym);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            ref.child(key).push().setValue(chat);
            inputText.setText("");

            ChatModel cm = new ChatModel(getApplicationContext());
            if (cm.addNewRecord(myNo, tym, num, key, input)) {

                ChatBean cb = new ChatBean();
                cb.setSentBy(myNo);
                cb.setSentAt(tym);
                cb.setSentTo(num);
                cb.setMsg(input);
                customAdapter.addItem(cb);
                customAdapter.notifyDataSetChanged();
//                CustomAdapter customAdapter = new CustomAdapter(UserActivity.this,chats,myNo);
//                customAdapter.addItem(cb);
//                customAdapter.notifyDataSetChanged();
                //lv.setAdapter(customAdapter);
                //lv.smoothScrollToPosition(customAdapter.getCount());
                //lv.setAdapter(customAdapter);

            }
            ChatModel.closeDBConnection(cm);


        }
    }

    public String numberSorting(String myNo , String num){
        String result = myNo + '_' + num;
        try{
            if(Long.parseLong(myNo) > Long.parseLong(num))
            {
                result = num + '_' + myNo;
            }
        }
        catch(Exception e){
            e.getStackTrace();
        }
        return result;
    }

}

