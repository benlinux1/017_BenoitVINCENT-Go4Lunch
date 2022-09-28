package com.benlinux.go4lunch.ui.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.benlinux.go4lunch.modules.PlaceAutoCompleteSearch;
import com.benlinux.go4lunch.ui.models.Restaurant;

import java.util.List;

public class PlaceAutoCompleteAdapter extends ArrayAdapter implements Filterable {

    private List<Restaurant> restaurants;
    private final PlaceAutoCompleteSearch placeApi = new PlaceAutoCompleteSearch();

    public PlaceAutoCompleteAdapter(Context context, int resId) {
        super(context, resId);
    }

    @Override
    public int getCount(){
        return restaurants.size();
    }

    @Override
    public String getItem(int pos){
        return restaurants.get(pos).getName();
    }

    public List<Restaurant> getRestaurantList() {
        return restaurants;
    }

    public String getRestaurantId(int pos) {
        return restaurants.get(pos).getId();
    }

    public String getRestaurantName(int pos) {
        return restaurants.get(pos).getName();
    }

    @Override
    public Filter getFilter(){
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null){
                    restaurants = placeApi.autoComplete(constraint.toString());

                    filterResults.values = restaurants;
                    filterResults.count = restaurants.size();
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