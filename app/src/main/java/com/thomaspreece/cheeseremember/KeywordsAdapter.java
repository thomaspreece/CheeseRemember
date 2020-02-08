package com.thomaspreece.cheeseremember;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;


public class KeywordsAdapter extends ArrayAdapter<String> {
        private Filter keywordsFilter = new KeywordsFilter();
        private List<String> itemsAll;
        private List<String> suggestions;

        public KeywordsAdapter(Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<String> objects) {
            super(context, resource, textViewResourceId, objects);
            this.itemsAll = new ArrayList<>(objects);
            this.suggestions = new ArrayList<>();
        }

        @Override
        public Filter getFilter() {
            return keywordsFilter;
        }

        private class KeywordsFilter extends Filter {
            @Override
            protected Filter.FilterResults performFiltering(CharSequence constraint) {
                String constraintString = constraint.toString();
                String subConstraintString = constraintString.substring(constraintString.lastIndexOf(",") + 1).trim().toLowerCase();

                if(!subConstraintString.equals("")) {
                    suggestions.clear();
                    for (String keyword : itemsAll) {

                        if(keyword.toLowerCase().startsWith(subConstraintString)){
                            suggestions.add(keyword);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
                ArrayList<String> filteredList = (ArrayList<String>) results.values;

                if(results.count > 0) {
                    clear();
                    for (String c : filteredList) {
                        add(c);
                    }
                    notifyDataSetChanged();
                }

            }
        }
}


