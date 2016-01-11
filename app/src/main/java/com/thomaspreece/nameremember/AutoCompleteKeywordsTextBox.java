package com.thomaspreece.nameremember;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;


public class AutoCompleteKeywordsTextBox extends AutoCompleteTextView {

    public AutoCompleteKeywordsTextBox(Context context) {
        super(context);
    }

    public AutoCompleteKeywordsTextBox(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    public AutoCompleteKeywordsTextBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
    }

    @Override
    public void replaceText(CharSequence text) {
        clearComposingText();
        String oldText = getText().toString().trim();
        String oldKeywords;

        if(oldText.lastIndexOf(",") == -1 || oldText.lastIndexOf(",") == 0){
            setText(text);
        }else {
            oldKeywords = oldText.substring(0, oldText.lastIndexOf(",")).trim();
            setText(oldKeywords + " , " + text);
        }

        // make sure we keep the caret at the end of the text view
        Editable spannable = getText();
        Selection.setSelection(spannable, spannable.length());
    }

}


