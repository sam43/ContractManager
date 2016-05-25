package com.cmanager.main.contractmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int EDIT = 0, DELETE = 1;

    EditText name,phone,email,address;
    //Button addbtn;
    ImageView contactImgView;
    List<Contact> Contacts = new ArrayList<Contact>();
    ListView ContactList;
    /*Uri imgUri = Uri.parse("F:\\AndroidProjects\\ContractManager\\app\\src\\main\\res\\drawable\\add.png");*/
    Uri imgUri = Uri.parse("android.resource://com.cmanager.main.contractmanager/drawable/add.png");
    DatabaseHandeler dbHandeler;
    int longOnSelectedItem;

    ArrayAdapter<Contact> ContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);*/


        name= (EditText) findViewById(R.id.nametxt);
        phone= (EditText) findViewById(R.id.phntxt);
        email= (EditText) findViewById(R.id.emailtxt);
        address= (EditText) findViewById(R.id.adtxt);

        contactImgView = (ImageView) findViewById(R.id.contactimgView);
        ContactList = (ListView) findViewById(R.id.listofcntct);
        dbHandeler = new DatabaseHandeler(getApplicationContext());

        this.registerForContextMenu(ContactList);

        ContactList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longOnSelectedItem = position;
                return false;
            }
        });

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        tabSpec.setContent(R.id.CreateContact);
        tabSpec.setIndicator("Add Contact");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.ContactList);
        tabSpec.setIndicator("Contact List");
        tabHost.addTab(tabSpec);

        final Button addbtn= (Button) findViewById(R.id.button);

        addbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Contact contact = new Contact(dbHandeler.getContanctCount(), String.valueOf(name.getText()), String.valueOf(phone.getText()), String.valueOf(email.getText()), String.valueOf(address.getText()), imgUri);
                //Contacts.add(new Contact(0, name.getText().toString(),phone.getText().toString(),email.getText().toString(),address.getText().toString(), imgUri));
                if (!contactExists(contact)) {
                    dbHandeler.createContact(contact);
                    Contacts.add(contact);
                    ContactAdapter.notifyDataSetChanged();
                    //showlist(); //showing contact second time which we don't wanna see!
                    Toast.makeText(getApplicationContext(), String.valueOf(name.getText()) + " has been ADDED to your Contact List!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), String.valueOf(name.getText()) + " is Already EXISTS! Please Pick a new name.", Toast.LENGTH_SHORT).show();
            }

        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //addbtn.setEnabled(String.valueOf(name.getText()).trim().length() > 0);
                if(s.length() > 0)
                    addbtn.setEnabled(true);
                else
                    addbtn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        contactImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),1);
            }
        });

       /* List<Contact> addableContact = dbHandeler.getAllContacts();

        int contactCount = dbHandeler.getContanctCount();

        for(int i = 0; i < contactCount; i++) {
            Contacts.add(addableContact.get(i));
        }*/

        if(dbHandeler.getContanctCount() != 0)
            Contacts.addAll(dbHandeler.getAllContacts());
        //if(!addableContact.isEmpty())
            showlist();
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.setHeaderIcon(R.drawable.pencil_icon);
        menu.setHeaderTitle("Choose!");
        menu.add(Menu.NONE, EDIT, menu.NONE, "Edit");
        menu.add(Menu.NONE, DELETE, menu.NONE, "Delete");

    }

    public boolean onContactItemSelected(MenuItem item){
        switch (item.getItemId()) {

            case EDIT:
                // codes
                break;
            case DELETE:
                dbHandeler.deleteContact(Contacts.get(longOnSelectedItem));
                Contacts.remove(longOnSelectedItem);
                ContactAdapter.notifyDataSetChanged();

                break;
        }
        return super.onContextItemSelected(item);
    }

    private boolean contactExists(Contact contact){
        String name = contact.getName();
        int contactCount = Contacts.size();

        for(int i =0; i < contactCount; i++){
            if(name.compareToIgnoreCase(Contacts.get(i).getName())== 0)
            return true;
        }

        return false;
    }


    public void onActivityResult(int reqCode, int resCode, Intent data){
        if(resCode == RESULT_OK){
            if(reqCode == 1)
                imgUri = data.getData();
                contactImgView.setImageURI(imgUri);
        }
    }

    //rest codes, methods goes here...

    public void showlist(){
        ContactAdapter = new ContactListAdapter();
        ContactList.setAdapter(ContactAdapter);
     }

    /*private void addContact(String name, String phone, String email, String address){
        Contacts.add(new Contact(name,phone,email,address));
    }*/

    public class ContactListAdapter extends ArrayAdapter<Contact>{

        public ContactListAdapter(){

            super(MainActivity.this, R.layout.list_item,Contacts);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent){

            if(view == null)
                view = getLayoutInflater().inflate(R.layout.list_item,parent,false);

            Contact currentContact = Contacts.get(position);

            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(currentContact.getName());
            TextView phone = (TextView) view.findViewById(R.id.phone);
            phone.setText(currentContact.getPhone());
            TextView email = (TextView) view.findViewById(R.id.email);
            email.setText(currentContact.getEmail());
            TextView address = (TextView) view.findViewById(R.id.address);
            address.setText(currentContact.getAddress());
            ImageView CnimgView = (ImageView) view.findViewById(R.id.CnimgView);
            CnimgView.setImageURI(currentContact.getImgUri());

            return view;

        }
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
