package com.brahminno.tweetloc;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;


public class User_ProfileActivity extends ActionBarActivity {
    EditText etName;
    Button btnOk,btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setIcon(R.drawable.ic_hdr_edit_user_128);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__profile);
        etName = (EditText) findViewById(R.id.etName);
        btnOk = (Button) findViewById(R.id.btnOk);
        btnSkip = (Button) findViewById(R.id.btnSkip);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user__profile, menu);
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
