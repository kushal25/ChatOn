package com.example.root.chaton.activity;

import java.util.ArrayList;
        import java.util.Collections;
        import java.util.Comparator;
        import java.util.List;

        import android.annotation.SuppressLint;
        import android.app.ActionBar;
        import android.app.Activity;
        import android.app.ActivityOptions;
        import android.content.Context;
        import android.content.Intent;
        import android.database.Cursor;
        import android.os.Bundle;
        import android.provider.ContactsContract;
        import android.telephony.TelephonyManager;
        import android.view.KeyEvent;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
        import android.widget.AdapterView.OnItemClickListener;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

import com.example.root.chaton.Adapters.ContactsAdapter;
import com.example.root.chaton.R;
import com.example.root.chaton.beans.ContactBean;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import org.json.JSONArray;

public class UserContacts extends Activity implements OnItemClickListener {

    private ListView listView;
    private List<ContactBean> listContacts = new ArrayList<ContactBean>();
    private PhoneNumberUtil phoneUtil;
    JSONArray contactListNumbers = new JSONArray();
    private String countryCode;
    String myNo;
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.action_bar);
        TextView tv = (TextView) findViewById(R.id.Title);
        myNo = getIntent().getExtras().getString("myNo");
        tv.setText(R.string.app_name);
        //tv.setPadding(160, 15, 0, 15);
        setContentView(R.layout.activity_user_contacts);
        listView = (ListView) findViewById(R.id.contact_listview);
        listView.setOnItemClickListener(this);
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        countryCode = manager.getNetworkCountryIso();
        phoneUtil = PhoneNumberUtil.getInstance();
        Cursor phones = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{
                        ContactsContract.CommonDataKinds.Phone._ID
                        , ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                        , ContactsContract.CommonDataKinds.Phone.NUMBER
                }, null,
                null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE NOCASE");
        while (phones.moveToNext()) {

            String Contactname = phones
                    .getString(1);

            String ContactNo = phones
                    .getString(2);


            try {

                    ContactNo = phoneUtil.format(phoneUtil.parse(ContactNo, countryCode.toUpperCase()), PhoneNumberUtil.PhoneNumberFormat.E164).replaceAll("[-+^]*", "");

            } catch (NumberParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if(!contactListNumbers.toString().contains(ContactNo) && !ContactNo.equals(myNo)) {
                ContactBean objContact = new ContactBean();
                objContact.setName(Contactname);
                objContact.setPhone(ContactNo);
                listContacts.add(objContact);

                contactListNumbers.put(ContactNo);
            }
        }
        phones.close();

        ContactsAdapter objAdapter = new ContactsAdapter(
                UserContacts.this, R.layout.list_item, listContacts);
        listView.setAdapter(objAdapter);
        if (listContacts != null && listContacts.size() != 0) {
            Collections.sort(listContacts, new Comparator<ContactBean>() {
                @Override
                public int compare(ContactBean lhs, ContactBean rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });

        } else {
            Toast.makeText(this, "No Contact Found!!!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onItemClick(AdapterView<?> listView, View view, int position,
                            long id) {
        String myNo = getIntent().getExtras().getString("myNo");
        //String myNo = "+15125786728";
        //String myNo = "+919975161966";
        try {
            myNo = phoneUtil.format(phoneUtil.parse(myNo, countryCode.toUpperCase()), PhoneNumberUtil.PhoneNumberFormat.E164).replaceAll("[-+^]*", "");
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        ContactBean cb = (ContactBean) listView.getItemAtPosition(position);
        String str = cb.getName();
        String no = cb.getPhone();
        Intent user = new Intent(UserContacts.this,UserActivity.class);
        user.putExtra("user",str);
        user.putExtra("num", no);
        user.putExtra("myNo", myNo);
        Bundle bndlanimation =
                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation1,R.anim.animation2).toBundle();
        startActivity(user, bndlanimation);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_contacts, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.share :

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey!! Game of Thrones is over.Install ChatOn and talk to your friends");
                sendIntent.setType("text/plain");

                String shareTitle = UserContacts.this.getResources().getString(R.string.share_title);
                UserContacts.this.startActivity(Intent.createChooser(sendIntent, shareTitle));
                break;


            case R.id.logout:
                Intent logout = new Intent(UserContacts.this,LoginActivity.class);
                startActivity(logout);
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


}