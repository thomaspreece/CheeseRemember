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
    private String cheeseName;
    private String placeOfPurchase;
    private String description;
    private String comments;
    private Date date;

    private List<String> keywords;

    public Person(){
        this.keywords = new ArrayList<>();
    }

    public Person(String cheeseName, String placeOfPurchase, String description, String comments){
        super();
        this.cheeseName = cheeseName;
        this.placeOfPurchase = placeOfPurchase;
        this.description = description;
        this.comments = comments;
        this.keywords = new ArrayList<>();
    }

    public String getCheeseName() {return this.cheeseName;}
    public void setCheeseName(String cheeseName) {this.cheeseName = cheeseName;}

    public String getPlaceOfPurchase() {return this.placeOfPurchase;}
    public void setPlaceOfPurchase(String placeOfPurchase) {this.placeOfPurchase = placeOfPurchase;}

    public String getDescription() {return this.description;}
    public void setDescription(String description) {this.description = description;}

    public String getComments() {return this.comments;}
    public void setComments(String comments) {this.comments = comments;}

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
