package com.brahminno.tweetloc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.brahminno.tweetloc.backend.tweetApi.TweetApi;
import com.brahminno.tweetloc.backend.tweetApi.model.GroupBean;
import com.brahminno.tweetloc.testAdapter.AddContactsAdapter;
import com.brahminno.tweetloc.testAdapter.Contacts_Test;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//this Async class is used to add new members in an existing group......
class AddNewMembersAsyncTask extends AsyncTask<Void,Void,String>{
    private Context context;
    private static TweetApi myTweetApi = null;
    private String groupName,adminMobileNumber;
    private ArrayList<String> newMemberNumber;

    public AddNewMembersAsyncTask(Context context,String groupName,String adminMobileNumber,ArrayList<String> newMemberNumber){
        this.context = context;
        this.groupName = groupName;
        this.adminMobileNumber = adminMobileNumber;
        this.newMemberNumber = newMemberNumber;
    }

    @Override
    protected String doInBackground(Void... params) {
        if (myTweetApi == null) {
            TweetApi.Builder builder = new TweetApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://brahminno.appspot.com/_ah/api/");

            myTweetApi = builder.build();
        }
        try{
            GroupBean groupBean = new GroupBean();
            groupBean.setGroupName(groupName);
            groupBean.setMobileNumber(adminMobileNumber);
            groupBean.setGroupMember(newMemberNumber);

            myTweetApi.addAnotherGroupMember(groupBean).execute();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}

//When group admin wants to add new members into his/her group, then this class is used......
public class AddNewMemberActivity extends ActionBarActivity {

    String groupName;
    ArrayList<String> Group_Member;
    String adminMobileNumber;
    SQLiteDatabase mydb;
    ListView listViewAddNewContactToGroup;
    NonExistingContactsAdapter nonExistingContactsAdapter;
    Button btnNewAddSelectedContact;
    ArrayList<ContactNameWithNumber> nonExistingContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_member);
        listViewAddNewContactToGroup = (ListView) findViewById(R.id.addNewMemberListView);
        btnNewAddSelectedContact = (Button) findViewById(R.id.btnNewAddSelectedContact);
        //get groupName from intent......
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        groupName = bundle.getString("Group Name");
        Group_Member = new ArrayList<>();
        try{
            mydb = new SQLiteDatabase(this);
            //get Admin Mobile Number from database using groupName....
            adminMobileNumber = mydb.getAdminMobileNumberUsingGroupName(groupName);
            nonExistingContactList = new ArrayList<>();
            nonExistingContactList = mydb.getNonExistingContacts(groupName,adminMobileNumber);
            for(int i = 0; i < nonExistingContactList.size(); i++){
                Log.i("Contacts.....",""+nonExistingContactList.get(i).getContact_name()+"-->"+nonExistingContactList.get(i).getContact_number());
            }
            Log.i("size of...","non existing contacts"+nonExistingContactList.size());
            Collections.sort(nonExistingContactList, new Comparator<ContactNameWithNumber>() {
                public int compare(ContactNameWithNumber a, ContactNameWithNumber b) {
                    return a.getContact_name().compareTo(b.getContact_name());
                }
            });

            nonExistingContactsAdapter = new NonExistingContactsAdapter(getApplicationContext(),nonExistingContactList);
            listViewAddNewContactToGroup.setAdapter(nonExistingContactsAdapter);
            //on button click event......
            btnNewAddSelectedContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Group_Member = nonExistingContactsAdapter.getArrayList();
                    new AddNewMembersAsyncTask(getApplicationContext(),groupName,adminMobileNumber,Group_Member).execute();
                }
            });


        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_member, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
