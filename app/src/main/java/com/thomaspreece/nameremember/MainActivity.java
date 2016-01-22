package com.thomaspreece.nameremember;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, View.OnClickListener{
    public static String PACKAGE_NAME;

    NRSQLiteHelper db = new NRSQLiteHelper(this);
    List<Person> personList;
    private SimpleAdapter adapter;
    private ListView list;
    private String searchText;

    private ToggleButton recentToggle;
    private ToggleButton nameToggle;
    private ToggleButton keywordsToggle;
    private ToggleButton descToggle;
    private ToggleButton interestsToggle;
    private LinearLayout searchOptionButtons;

    private boolean searchOptionsButtonsInView;

    private int activeToggle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.activeToggle = 0;
        super.onCreate(savedInstanceState);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Open Database
        db.getWritableDatabase();

        // drop this database if already exists
        //db.onUpgrade(db.getWritableDatabase(), 1, 2);

        searchOptionButtons = (LinearLayout) findViewById(R.id.searchButtons);
        searchOptionButtons.setVisibility(View.GONE);
        searchOptionsButtonsInView = false;

        // get all persons
        personList = db.getAllPersons();
        refreshPersonList();

        list = (ListView) findViewById(R.id.list);
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PersonActivity.class);
                intent.putExtra("person", personList.get(position).getId());
                startActivityForResult(intent, 1);
            }

        });

        Button newButton = (Button) findViewById(R.id.addPerson);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditPersonActivity.class);
                intent.putExtra("type", "new");
                startActivityForResult(intent, 1);
            }
        });

        recentToggle = (ToggleButton) findViewById(R.id.searchOptionButtonRecent);
        nameToggle = (ToggleButton) findViewById(R.id.searchOptionButtonName);
        keywordsToggle = (ToggleButton) findViewById(R.id.searchOptionButtonKeywords);
        descToggle = (ToggleButton) findViewById(R.id.searchOptionButtonDesc);
        interestsToggle = (ToggleButton) findViewById(R.id.searchOptionButtonInterests);
        searchOptionButtons = (LinearLayout) findViewById(R.id.searchButtons);

        recentToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeToggle = 0;
                setToggles();
                updateToggleSearch();
            }
        });

        nameToggle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                activeToggle = 1;
                setToggles();
                updateToggleSearch();
            }
        });

        keywordsToggle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                activeToggle = 2;
                setToggles();
                updateToggleSearch();
            }
        });

        descToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeToggle = 3;
                setToggles();
                updateToggleSearch();
            }
        });

        interestsToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeToggle = 4;
                setToggles();
                updateToggleSearch();
            }
        });

        this.setToggles();




    }

    private void updateToggleSearch(){
        personList = db.getSearchPersons(searchText,activeToggle);
        this.refreshPersonList();
    }

    private void setToggles(){
        recentToggle.setChecked(false);
        nameToggle.setChecked(false);
        keywordsToggle.setChecked(false);
        descToggle.setChecked(false);
        interestsToggle.setChecked(false);

        switch (activeToggle){
            case 0:
                recentToggle.setChecked(true);
                break;
            case 1:
                nameToggle.setChecked(true);
                break;
            case 2:
                keywordsToggle.setChecked(true);
                break;
            case 3:
                descToggle.setChecked(true);
                break;
            case 4:
                interestsToggle.setChecked(true);
                break;
            default:
                throw new RuntimeException("Invalid Toggle State: "+activeToggle );
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        personList = db.getAllPersons();
        this.refreshPersonList();
    }

    private void refreshPersonList(){
        ArrayList<HashMap<String,String>> dataList = new ArrayList<>();

        for (int i = 0; i < personList.size(); i++) {
            HashMap<String,String> temp = new HashMap<>();
            temp.put("name", personList.get(i).getFirstName()+" "+personList.get(i).getLastName());
            if(searchOptionsButtonsInView == true){
                switch(activeToggle){
                    case 0:
                        temp.put("other", "");
                        break;
                    case 1:
                    case 3:
                        temp.put("other", personList.get(i).getDescription());
                        break;
                    case 2:
                        temp.put("other", personList.get(i).getKeywordsString());
                        break;
                    case 4:
                        temp.put("other", personList.get(i).getInterests());
                        break;
                    default:
                        throw new RuntimeException("Invalid Toggle State: "+activeToggle );
                }
            }else {
                temp.put("other", "");
            }
            temp.put("date", personList.get(i).getDateTime());
            dataList.add(temp);
        }

        if (searchOptionsButtonsInView == false || activeToggle == 0) {
            adapter = new SimpleAdapter(
                    this,
                    dataList,
                    R.layout.person_list_2_row_layout,
                    new String[]{"name", "date"},
                    new int[]{R.id.listText, R.id.listText3}
            );
        }else{
            adapter = new SimpleAdapter(
                    this,
                    dataList,
                    R.layout.person_list_3_row_layout,
                    new String[]{"name", "other", "date"},
                    new int[]{R.id.listText, R.id.listText2, R.id.listText3}
            );
        }
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.setOnSearchClickListener(this);
        return true;
    }

    //Search On Open Function
    public void onClick(View v){
        searchOptionButtons.setVisibility(View.VISIBLE);
        searchOptionsButtonsInView = true;
        this.updateToggleSearch();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    //Search On Close Function
    public boolean onClose() {
        searchOptionButtons.setVisibility(View.GONE);
        searchOptionsButtonsInView = false;
        personList = db.getAllPersons();
        this.refreshPersonList();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchOptionButtons.setVisibility(View.VISIBLE);
        searchOptionsButtonsInView = true;
        searchText = newText;
        personList = db.getSearchPersons(newText,activeToggle);
        this.refreshPersonList();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchText = query;
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_add) {
            Intent intent = new Intent(MainActivity.this, EditPersonActivity.class);
            intent.putExtra("type", "new");
            startActivityForResult(intent, 1);
            return true;
        }else if (id == R.id.menu_export) {
            //Open your local db as the input stream
            try {
                File externalFilesDir = this.getExternalFilesDir(null);
                if (externalFilesDir == null) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.SD_not_mounted), Toast.LENGTH_LONG).show();
                    return true;
                }

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.UK);
                String strDate = sdf.format(c.getTime());


                File inFileName = this.getDatabasePath("NameDB");
                String outFileName = externalFilesDir.toString() + "/NameDB_"+strDate+".bak";
                String outFileNameOnly = "NameDB_"+strDate+".bak";


                FileInputStream fis = new FileInputStream(inFileName);

                //Open the empty db as the output stream
                OutputStream output = new FileOutputStream(outFileName);
                //transfer bytes from the inputfile to the outputfile
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                //Close the streams
                output.flush();
                output.close();
                fis.close();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.DB_saved_to_SD)+" "+outFileNameOnly, Toast.LENGTH_LONG).show();

            } catch(IOException e1) {
                e1.printStackTrace();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.export_failed), Toast.LENGTH_LONG).show();
                return true;
            }


            return true;
        }else if (id == R.id.menu_import) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_implemented_yet), Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
