package com.tutorials.hp.gridviewrealmimages;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.tutorials.hp.gridviewrealmimages.m_Realm.RealmHelper;
import com.tutorials.hp.gridviewrealmimages.m_Realm.Spacecraft;
import com.tutorials.hp.gridviewrealmimages.m_UI.CustomAdapter;

import io.realm.Realm;
import io.realm.RealmChangeListener;


public class MainActivity extends AppCompatActivity {

    Realm realm;
    RealmChangeListener realmChangeListener;
    CustomAdapter adapter;
    GridView gv;
    EditText nameEditText,propEditTxt,descEditTxt,urlEditTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        gv= (GridView) findViewById(R.id.gv);

        //INITIALIZE REALM
        realm=Realm.getDefaultInstance();
        final RealmHelper helper=new RealmHelper(realm);

        //RETRIEVE
        helper.retrieveFromDB();

        //ADAPTER
        adapter=new CustomAdapter(this,helper.justRefresh());
        gv.setAdapter(adapter);

        //DATA CHANGE EVENTS
        realmChangeListener=new RealmChangeListener() {
            @Override
            public void onChange() {
                adapter=new CustomAdapter(MainActivity.this,helper.justRefresh());
                gv.setAdapter(adapter);
            }
        };

        //ADD TO REALM
        realm.addChangeListener(realmChangeListener);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayInputDialog();
            }
        });
    }

    //SHOW INPUT DIALOG
    private void displayInputDialog()
    {
        Dialog d=new Dialog(this);
        d.setTitle("Save to Ream database");
        d.setContentView(R.layout.input_dialog);

        //EDITTEXTS
        nameEditText= (EditText) d.findViewById(R.id.nameEditText);
        propEditTxt= (EditText) d.findViewById(R.id.propellantEditText);
        descEditTxt= (EditText) d.findViewById(R.id.descEditText);
        urlEditTxt= (EditText) d.findViewById(R.id.urlEditText);
        Button saveBtn= (Button) d.findViewById(R.id.saveBtn);

        //SAVE
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //GET DATA
                String name=nameEditText.getText().toString();
                String propellant=propEditTxt.getText().toString();
                String desc=descEditTxt.getText().toString();
                String imageUrl=urlEditTxt.getText().toString();

                //VERIFY
                if(name != null && name.length()>0)
                {
                    //SET DATA
                    Spacecraft s=new Spacecraft();
                    s.setName(name);
                    s.setPropellant(propellant);
                    s.setDescription(desc);
                    s.setImageUrl(imageUrl);

                    //SAVE
                    RealmHelper helper=new RealmHelper(realm);
                    if(helper.save(s))
                    {
                        nameEditText.setText("");
                        propEditTxt.setText("");
                        descEditTxt.setText("");
                        urlEditTxt.setText("");

                    }else {

                        Toast.makeText(MainActivity.this, "Input Not Valid", Toast.LENGTH_SHORT).show();

                    }



                }else
                {
                    Toast.makeText(MainActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


        d.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.removeChangeListener(realmChangeListener);
        realm.close();
    }
}













