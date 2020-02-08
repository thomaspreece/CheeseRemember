package com.thomaspreece.nameremember;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PersonActivity extends AppCompatActivity {
    Person selectedPerson;
    NRSQLiteHelper db;
    TextView placeOfPurchaseText;
    TextView descText;
    TextView commentsText;
    TextView keywordsText;
    int id;
    Button deleteButton;
    Button updateButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_person);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        id = intent.getIntExtra("person", -1);

        // open the database of the application context
        db = new NRSQLiteHelper(getApplicationContext());

        // read the book with "id" from the database
        selectedPerson = db.readPerson(id);

        placeOfPurchaseText = (TextView) findViewById(R.id.placeOfPurchase);
        descText = (TextView) findViewById(R.id.description);
        commentsText = (TextView) findViewById(R.id.comments);
        keywordsText = (TextView) findViewById(R.id.keywords);

        initializeViews();

        deleteButton = (Button) findViewById(R.id.deletePerson);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PersonActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.delete_person_alert)
                        .setMessage(R.string.delete_person_alert_text)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                db.deletePerson(id);
                                //Stop the activity
                                PersonActivity.this.finish();
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();

            }
        });


        updateButton = (Button) findViewById(R.id.editPerson);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonActivity.this, EditPersonActivity.class);
                intent.putExtra("type", "edit");
                intent.putExtra("id",id);
                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        selectedPerson = db.readPerson(id);
        initializeViews();
    }

    public void initializeViews(){
        placeOfPurchaseText.setText(selectedPerson.getPlaceOfPurchase());
        descText.setText(selectedPerson.getDescription());
        descText.setMovementMethod(new ScrollingMovementMethod());
        descText.setLongClickable(true);
        descText.setTextIsSelectable(true);
        commentsText.setText(selectedPerson.getComments());
        commentsText.setMovementMethod(new ScrollingMovementMethod());
        keywordsText.setText(selectedPerson.getKeywordsString());

        setTitle(selectedPerson.getCheeseName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            //this.finish();
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
