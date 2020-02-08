package com.thomaspreece.nameremember;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class NRSQLiteHelper extends SQLiteOpenHelper{
    // database version
    private static final int database_VERSION = 1;
    // database name
    private static final String database_NAME = "CheeseDB";

    private static final String table_PERSONS = "persons";
    private static final String persons_ID = "id";
    private static final String persons_CHEESENAME = "cheeseN";
    private static final String persons_PLACEOFPURCHASE = "placeOfPurchase";
    private static final String persons_DESC = "desc";
    private static final String persons_COMMENTS = "comments";
    private static final String persons_DATE = "date";
    private static final String[] table_PERSONS_COLUMNS = { persons_ID, persons_CHEESENAME, persons_PLACEOFPURCHASE, persons_DESC, persons_COMMENTS, persons_DATE };

    private static final String table_KEYWORDS = "keywords";
    private static final String keywords_ID = "id";
    private static final String keywords_KEYWORD = "keyword";
    private static final String[] table_KEYWORDS_COLUMNS = {keywords_ID, keywords_KEYWORD };

    private static final String table_PERSONS_KEYWORDS = "persons_keywords";
    private static final String persons_keywords_NAME_ID = "person_id";
    private static final String persons_keywords_KEYWORD_ID = "keyword_id";
    private static final String[] table_PERSONS_KEYWORDS_COLUMNS = {persons_keywords_NAME_ID,persons_keywords_KEYWORD_ID};


    public NRSQLiteHelper(Context context) {
        super(context, database_NAME, null, database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create person table
        String CREATE_NAME_TABLE = "CREATE TABLE " + table_PERSONS + " ( " + persons_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " + persons_CHEESENAME + " TEXT, " + persons_PLACEOFPURCHASE + " TEXT, " + persons_DESC + " TEXT, "+ persons_COMMENTS + " TEXT," + persons_DATE +" TIMESTAMP DEFAULT CURRENT_TIMESTAMP  )";
        String CREATE_NAMES_KEYWORDS_TABLE = "CREATE TABLE " + table_PERSONS_KEYWORDS + " ( " + persons_keywords_NAME_ID + " INTEGER, " + persons_keywords_KEYWORD_ID + " INTEGER )";
        String CREATE_KEYWORDS_TABLE = "CREATE TABLE " + table_KEYWORDS + " ( " + keywords_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + keywords_KEYWORD + " TEXT )";
        db.execSQL(CREATE_NAME_TABLE);
        db.execSQL(CREATE_NAMES_KEYWORDS_TABLE);
        db.execSQL(CREATE_KEYWORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // drop persons table if already exists
        if (oldVersion<4) {
            db.execSQL("DROP TABLE IF EXISTS persons");
            db.execSQL("DROP TABLE IF EXISTS persons_keywords");
            db.execSQL("DROP TABLE IF EXISTS keywords");
            this.onCreate(db);
        }
    }

    public void createPerson(Person person) {
        // get reference of the NameDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(persons_CHEESENAME, person.getCheeseName());
        values.put(persons_PLACEOFPURCHASE, person.getPlaceOfPurchase());
        values.put(persons_DESC, person.getDescription());
        values.put(persons_COMMENTS, person.getComments());

        // insert person
        long nameID = db.insert(table_PERSONS, null, values);

        //insert keywords
        List<String> keywordsList = person.getKeywords();
        for (int i = 0; i < keywordsList.size(); i++) {
            if(!keywordsList.get(i).trim().equals("")) {
                Cursor cursor_keywords = db.query(table_KEYWORDS, table_KEYWORDS_COLUMNS, "keyword = ?", new String[]{keywordsList.get(i)}, null, null, null, null);
                long keywordID;
                if (cursor_keywords.moveToFirst()) {
                    keywordID = cursor_keywords.getInt(0);
                } else {
                    ContentValues keyword = new ContentValues();
                    keyword.put(keywords_KEYWORD, keywordsList.get(i));
                    keywordID = db.insert(table_KEYWORDS, null, keyword);
                }
                cursor_keywords.close();
                ContentValues name_keyword = new ContentValues();
                name_keyword.put(persons_keywords_NAME_ID, nameID);
                name_keyword.put(persons_keywords_KEYWORD_ID, keywordID);
                db.insert(table_PERSONS_KEYWORDS, null, name_keyword);
            }
        }

        // close database transaction
        db.close();
    }

    public LinkedList<String> getAllKeywords() {
        LinkedList<String> keywords = new LinkedList<>();
        // get reference of the NamesDB database
        SQLiteDatabase db = this.getWritableDatabase();
        // Get all persons
        Cursor cursor = db.query(table_KEYWORDS, table_KEYWORDS_COLUMNS, "", null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                keywords.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        Collections.sort(keywords);
        return keywords;
    }

    public List<Person> getSearchPersons(String term , int searchType){
        List<Person> persons = new LinkedList<>();

        // get reference of the NamesDB database
        SQLiteDatabase db = this.getWritableDatabase();
        String searchQuery;
        Cursor cursor;
        // Get all persons
        if(term == null || term.trim().length() == 0 ){
            if(searchType==0){
                cursor = db.query(table_PERSONS, table_PERSONS_COLUMNS, "", null, null, null, persons_DATE + " COLLATE NOCASE DESC, " + persons_PLACEOFPURCHASE + " COLLATE NOCASE ASC, " + persons_CHEESENAME + " COLLATE NOCASE ASC");
            }else if(searchType==1) {
                cursor = db.query(table_PERSONS, table_PERSONS_COLUMNS, "", null, null, null, persons_CHEESENAME + " COLLATE NOCASE ASC, " + persons_PLACEOFPURCHASE + " COLLATE NOCASE ASC");
            }else{
                cursor = db.query(table_PERSONS, table_PERSONS_COLUMNS, "", null, null, null, persons_PLACEOFPURCHASE + " COLLATE NOCASE ASC, " + persons_CHEESENAME + " COLLATE NOCASE ASC");
            }
        }else {
            switch (searchType) {
                case 0:
                    searchQuery = "SELECT * FROM " + table_PERSONS + " WHERE `" + persons_DATE + "` LIKE ? ORDER BY " + persons_DATE + " COLLATE NOCASE DESC ," +  persons_CHEESENAME + " COLLATE NOCASE ASC";
                    cursor = db.rawQuery(searchQuery, new String[]{"%" + term + "%"});
                    break;
                case 1:
                    searchQuery = "SELECT * FROM " + table_PERSONS + " WHERE (" + persons_CHEESENAME + ") LIKE ? ORDER BY (" + persons_CHEESENAME + ") = ? COLLATE NOCASE DESC, " + persons_CHEESENAME + " LIKE ? DESC, " + persons_CHEESENAME + " COLLATE NOCASE ASC";
                    cursor = db.rawQuery(searchQuery, new String[]{"%" + term.toLowerCase() + "%", term.toLowerCase(), "%" + term.toLowerCase() + "%"});
                    break;
                case 2:
                    searchQuery = "SELECT DISTINCT " + table_PERSONS + ".* FROM ((" + table_PERSONS_KEYWORDS + " INNER JOIN " + table_KEYWORDS + " ON " + table_KEYWORDS + "." + keywords_ID + " = " + table_PERSONS_KEYWORDS + "." + persons_keywords_KEYWORD_ID + " AND " + table_KEYWORDS + "." + keywords_KEYWORD + " LIKE ?) INNER JOIN " + table_PERSONS + " ON " + table_PERSONS + "." + persons_ID + " = " + table_PERSONS_KEYWORDS + "." + persons_keywords_NAME_ID + " ) ORDER BY  " + table_KEYWORDS + "." + keywords_KEYWORD + " = ? DESC ," + table_KEYWORDS + "." + keywords_KEYWORD + " LIKE ? DESC, " + persons_CHEESENAME + " COLLATE NOCASE ASC";
                    cursor = db.rawQuery(searchQuery, new String[]{"%" + term + "%", term, "%" + term + "%"});
                    break;
                case 3:
                    searchQuery = "SELECT * FROM " + table_PERSONS + " WHERE `" + persons_DESC + "` LIKE ? ORDER BY " + persons_CHEESENAME + " COLLATE NOCASE ASC";
                    cursor = db.rawQuery(searchQuery, new String[]{"%" + term + "%"});
                    break;
                case 4:
                    searchQuery = "SELECT * FROM " + table_PERSONS + " WHERE " + persons_COMMENTS + " LIKE ? ORDER BY " + persons_CHEESENAME + " COLLATE NOCASE ASC";
                    cursor = db.rawQuery(searchQuery, new String[]{"%" + term + "%"});
                    break;
                case 5:
                    searchQuery = "SELECT * FROM " + table_PERSONS + " WHERE " + persons_PLACEOFPURCHASE + " LIKE ? ORDER BY " + persons_CHEESENAME + " COLLATE NOCASE ASC";
                    cursor = db.rawQuery(searchQuery, new String[]{"%" + term + "%"});
                    break;
                default:
                    throw new RuntimeException("Invalid Toggle State: " + searchType);
            }

        }

        // parse all results
        Person person;
        if (cursor.moveToFirst()) {
            do {
                person = new Person();
                person.setId(Integer.parseInt(cursor.getString(0)));
                person.setCheeseName(cursor.getString(1));
                person.setPlaceOfPurchase(cursor.getString(2));
                person.setDescription(cursor.getString(3));
                person.setComments(cursor.getString(4));

                String dateString = cursor.getString(5);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
                try {
                    Date date = dateFormat.parse(dateString);
                    person.setDate(date);
                }catch (ParseException e) {
                    //handle exception
                }

                String keywords_query = "SELECT " + table_KEYWORDS + "." + keywords_KEYWORD + " FROM " + table_KEYWORDS + " LEFT JOIN " + table_PERSONS_KEYWORDS + " ON " + table_KEYWORDS + "." + keywords_ID + " = " + table_PERSONS_KEYWORDS + "." + persons_keywords_KEYWORD_ID + " WHERE " + table_PERSONS_KEYWORDS + "." + persons_keywords_NAME_ID + " = ?";
                Cursor cursor_keywords = db.rawQuery(keywords_query, new String[]{String.valueOf(person.getId())});
                if (cursor_keywords.moveToFirst()) {
                    do {
                        person.addKeyword(cursor_keywords.getString(0));
                    } while (cursor_keywords.moveToNext());
                }
                cursor_keywords.close();

                // Add person to persons
                persons.add(person);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return persons;


    }

    public List<Person> getAllPersons() {
        List<Person> persons = new LinkedList<>();

        // get reference of the NamesDB database
        SQLiteDatabase db = this.getWritableDatabase();
        // Get all persons
        Cursor cursor = db.query(table_PERSONS, table_PERSONS_COLUMNS, "", null, null, null, persons_CHEESENAME+" COLLATE NOCASE ASC");

        // parse all results
        Person person;
        if (cursor.moveToFirst()) {
            do {
                person = new Person();
                person.setId(Integer.parseInt(cursor.getString(0)));
                person.setCheeseName(cursor.getString(1));
                person.setPlaceOfPurchase(cursor.getString(2));
                person.setDescription(cursor.getString(3));
                person.setComments(cursor.getString(4));

                String dateString = cursor.getString(5);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
                try {
                    Date date = dateFormat.parse(dateString);
                    person.setDate(date);
                }catch (ParseException e) {
                    //handle exception
                }

                String keywords_query = "SELECT " + table_KEYWORDS + "." + keywords_KEYWORD + " FROM " + table_KEYWORDS + " LEFT JOIN " + table_PERSONS_KEYWORDS + " ON " + table_KEYWORDS + "." + keywords_ID + " = " + table_PERSONS_KEYWORDS + "." + persons_keywords_KEYWORD_ID + " WHERE " + table_PERSONS_KEYWORDS + "." + persons_keywords_NAME_ID + " = ?";
                Cursor cursor_keywords = db.rawQuery(keywords_query, new String[]{String.valueOf(person.getId())});
                if (cursor_keywords.moveToFirst()) {
                    do {
                        person.addKeyword(cursor_keywords.getString(0));
                    } while (cursor_keywords.moveToNext());
                }
                cursor_keywords.close();

                // Add person to persons
                persons.add(person);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return persons;
    }

    public void updatePerson(Person person) {

        // get reference of the BookDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // make values to be inserted
        ContentValues values = new ContentValues();
        values.put(persons_CHEESENAME, person.getCheeseName());
        values.put(persons_PLACEOFPURCHASE, person.getPlaceOfPurchase());
        values.put(persons_DESC, person.getDescription());
        values.put(persons_COMMENTS, person.getComments());

        Integer nameID = person.getId();

        // update
        db.update(table_PERSONS, values, persons_ID + " = ?", new String[]{String.valueOf(nameID)});

        //delete keywords
        db.delete(table_PERSONS_KEYWORDS, persons_keywords_NAME_ID + " = ?", new String[] { String.valueOf(nameID) });

        //insert keywords
        List<String> keywordsList = person.getKeywords();
        for (int i = 0; i < keywordsList.size(); i++) {
            if(!keywordsList.get(i).trim().equals("")) {
                Cursor cursor_keywords = db.query(table_KEYWORDS, table_KEYWORDS_COLUMNS, "keyword = ?", new String[]{keywordsList.get(i)}, null, null, null, null);
                long keywordID;
                if (cursor_keywords.moveToFirst()) {
                    keywordID = cursor_keywords.getInt(0);
                } else {
                    ContentValues keyword = new ContentValues();
                    keyword.put(keywords_KEYWORD, keywordsList.get(i));
                    keywordID = db.insert(table_KEYWORDS, null, keyword);
                }

                ContentValues name_keyword = new ContentValues();
                name_keyword.put(persons_keywords_NAME_ID, nameID);
                name_keyword.put(persons_keywords_KEYWORD_ID, keywordID);
                db.insert(table_PERSONS_KEYWORDS, null, name_keyword);
                cursor_keywords.close();
            }
        }

        this.cleanKeywords(db);
        db.close();
    }

    // Deleting single person
    public void deletePerson(Person person) {

        // get reference of the PersonDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // delete person
        db.delete(table_PERSONS, persons_ID + " = ?", new String[] { String.valueOf(person.getId()) });
        db.delete(table_PERSONS_KEYWORDS, persons_keywords_NAME_ID + " = ?", new String[] { String.valueOf(person.getId()) });
        this.cleanKeywords(db);
        db.close();
    }

    // Deleting single person
    public void deletePerson(int personId) {

        // get reference of the PersonDB database
        SQLiteDatabase db = this.getWritableDatabase();

        // delete person
        db.delete(table_PERSONS, persons_ID + " = ?", new String[] { String.valueOf(personId) });
        db.delete(table_PERSONS_KEYWORDS, persons_keywords_NAME_ID + " = ?", new String[] { String.valueOf(personId) });
        this.cleanKeywords(db);
        db.close();
    }

    public Person readPerson(int id) {
        // get reference of the NamesDB database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        // get book query
        cursor = db.query(table_PERSONS, table_PERSONS_COLUMNS, " id = ?", new String[] { String.valueOf(id) }, null, null, null, null);

        // if results !=null, parse the first one
        if (cursor != null) {
            cursor.moveToFirst();

            Person person = new Person();
            person.setId(Integer.parseInt(cursor.getString(0)));
            person.setCheeseName(cursor.getString(1));
            person.setPlaceOfPurchase(cursor.getString(2));
            person.setDescription(cursor.getString(3));
            person.setComments(cursor.getString(4));


            String keywords_query = "SELECT " + table_KEYWORDS + "." + keywords_KEYWORD + " FROM " + table_KEYWORDS + " LEFT JOIN " + table_PERSONS_KEYWORDS + " ON " + table_KEYWORDS + "." + keywords_ID + " = " + table_PERSONS_KEYWORDS + "." + persons_keywords_KEYWORD_ID + " WHERE " + table_PERSONS_KEYWORDS + "." + persons_keywords_NAME_ID + " = ?";
            cursor = db.rawQuery(keywords_query, new String[]{String.valueOf(id)});

            if (cursor.moveToFirst()) {
                do {
                    person.addKeyword(cursor.getString(0));
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
            return person;

        } else {
            throw new RuntimeException("Invalid Person read from database");
        }

    }

    public void cleanKeywords(SQLiteDatabase db) {
        // get reference of the NamesDB database
        String keywords_query = "DELETE FROM keywords WHERE id IN (SELECT keywords.id FROM keywords LEFT JOIN persons_keywords ON keywords.id = persons_keywords.keyword_id WHERE persons_keywords.person_id IS NULL)";
        db.execSQL(keywords_query);
    }
}
