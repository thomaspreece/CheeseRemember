package com.thomaspreece.nameremember;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import java.util.List;

public class EditPersonActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText firstN;
    private EditText lastN;
    private EditText desc;
    private EditText interests;
    private AutoCompleteTextView keywords;

    NRSQLiteHelper db = new NRSQLiteHelper(this);
    private String type;
    private Person person;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        if (type.equals("new")) {
            setContentView(R.layout.activity_new_person);
            setTitle(getResources().getString(R.string.new_person));
        }else if (type.equals("edit")) {
            setContentView(R.layout.activity_edit_person);
            setTitle(getResources().getString(R.string.edit_person));
            int id = intent.getIntExtra("id", -1);
            person = db.readPerson(id);
        }else{
            throw new RuntimeException("Unrecognised EditPersonActivity type");
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        firstN = (EditText) findViewById(R.id.firstN);
        lastN = (EditText) findViewById(R.id.lastN);
        desc = (EditText) findViewById(R.id.desc);
        interests = (EditText) findViewById(R.id.interests);
        keywords = (AutoCompleteKeywordsTextBox) findViewById(R.id.tags);

        List<String> keywordList = db.getAllKeywords();
        ArrayAdapter keywordAdapter = new KeywordsAdapter(this, R.layout.autocomplete_row_layout, R.id.autocomplete_listText, keywordList);
        keywords.setAdapter(keywordAdapter);
        keywords.setThreshold(0);

        if (type.equals("edit")) {
            populateFields();
        }

        Button save_button = (Button) findViewById(R.id.savePerson);
        save_button.setOnClickListener(this);
    }

    public void populateFields(){
        firstN.setText(person.getFirstName());
        lastN.setText(person.getLastName());
        desc.setText(person.getDescription());
        interests.setText(person.getInterests());
        keywords.setText(person.getKeywordsString());
    }

    public void onClick(View v){
        if (type.equals("edit")) {
            person.setFirstName(firstN.getText().toString());
            person.setLastName(lastN.getText().toString());
            person.setDescription(desc.getText().toString());
            person.setInterests(interests.getText().toString());
            person.setKeywords(keywords.getText().toString());
            db.updatePerson(person);
        }else {
            String firstNameString = firstN.getText().toString();
            String lastNameString = lastN.getText().toString();
            String descriptionString = desc.getText().toString();
            String interestsString = interests.getText().toString();
            Person person = new Person(firstNameString, lastNameString, descriptionString, interestsString);
            person.setKeywords(keywords.getText().toString());
            db.createPerson(person);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(EditPersonActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.lose_changes_alert)
                .setMessage(R.string.lose_changes_alert_text)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                new AlertDialog.Builder(EditPersonActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.lose_changes_alert)
                        .setMessage(R.string.lose_changes_alert_text)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
