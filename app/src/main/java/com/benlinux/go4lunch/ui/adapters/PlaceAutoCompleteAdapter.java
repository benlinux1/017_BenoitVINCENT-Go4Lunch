package com.benlinux.go4lunch.ui.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.benlinux.go4lunch.ui.models.PlaceAutoCompleteSearch;

import java.util.ArrayList;

public class PlaceAutoCompleteAdapter extends ArrayAdapter implements Filterable {
    
    private ArrayList<String> results;
    private final PlaceAutoCompleteSearch placeApi = new PlaceAutoCompleteSearch();

    public PlaceAutoCompleteAdapter(Context context, int resId) {
        super(context, resId);
    }

    @Override
    public int getCount(){
        return results.size();
    }

    @Override
    public String getItem(int pos){
        return results.get(pos);
    }

    @Override
    public Filter getFilter(){
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null){
                    results = placeApi.autoComplete(constraint.toString());

                    filterResults.values = results;
                    filterResults.count = results.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

}