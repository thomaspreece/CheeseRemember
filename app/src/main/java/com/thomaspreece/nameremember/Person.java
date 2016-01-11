package com.thomaspreece.nameremember;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Person {
    private int id;
    private String firstName;
    private String lastName;
    private String description;
    private String interests;
    private Date date;

    private List<String> keywords;

    public Person(){
        this.keywords = new ArrayList<>();
    }

    public Person(String firstName, String lastName, String description, String interests){
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.interests = interests;
        this.keywords = new ArrayList<>();
    }

    public String getFirstName() {return this.firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}

    public String getLastName() {return this.lastName;}
    public void setLastName(String lastName) {this.lastName = lastName;}

    public String getFullName() {return this.firstName + " " +this.lastName; }

    public String getDescription() {return this.description;}
    public void setDescription(String description) {this.description = description;}

    public String getInterests() {return this.interests;}
    public void setInterests(String interests) {this.interests = interests;}

    public List<String> getKeywords() {return this.keywords;}
    public String getKeywordsString() {return android.text.TextUtils.join(" , ", this.keywords);}

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
        Collections.sort(this.keywords);
    }

    public void setKeywords(String keywords) {
        if (!keywords.equals("") && !keywords.equals(" ")) {
            String[] keywordStrings = keywords.split(",");
            String[] trimmedKeywordStrings = new String[keywordStrings.length];
            for (int i = 0; i < keywordStrings.length; i++) {
                trimmedKeywordStrings[i] = keywordStrings[i].trim();
            }
            this.keywords = new ArrayList<>(Arrays.asList(trimmedKeywordStrings));
            Collections.sort(this.keywords);
        }
    }

    public void addKeyword(String keyword) {
        this.keywords.add(keyword);
        Collections.sort(this.keywords);
    }

    public String getDateTime() {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        return dateFormat.format(this.date);
    }
    public void setDate(Date date) {this.date = date;}

    public int getId() {return this.id;}
    public void setId(int id) {this.id = id;}

}
