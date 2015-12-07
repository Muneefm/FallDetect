package com.mea.felldetect;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gc.materialdesign.views.Button;

import java.util.List;

import static android.widget.Toast.LENGTH_LONG;


public class ContactActivity extends ActionBarActivity {


    ListView contactListlv;
    ContactAdapter adapter;
     DbHelper db;
    Button addContact;
    Context c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        contactListlv = (ListView) findViewById(R.id.contactList);
        addContact = (Button) findViewById(R.id.addc);
        c= getApplicationContext();
        android.support.v7.app.ActionBar actionBar =  getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0091EA")));
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogue();
            }
        });
        db = new DbHelper(getApplicationContext());
        //db.addContact(new Contacts("Muneef M","23241334"));
        //db.addContact(new Contacts("Stanley M","7564932"));
        //db.addContact(new Contacts("4234234"));

        List<Contacts> contactsList = db.getAllContacts();
        adapter = new ContactAdapter(getApplication(),contactsList);

        contactListlv.setAdapter(adapter);





    }

public void ShowDialogue(){


    final Dialog dialog = new Dialog(this);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.dialogue_addcontact);
    final EditText number =  (EditText) dialog.findViewById(R.id.number);
    final EditText name =  (EditText) dialog.findViewById(R.id.nameedt);
    Button b = (Button) dialog.findViewById(R.id.addd);
    b.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            db.addContact(new Contacts(name.getText().toString(),number.getText().toString()));
            List<Contacts> contactsList = db.getAllContacts();
            adapter = new ContactAdapter(getApplication(),contactsList);

            contactListlv.setAdapter(adapter);
            Toast.makeText(c,"Contact Added!",Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    });



    dialog.show();

}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact, menu);
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
