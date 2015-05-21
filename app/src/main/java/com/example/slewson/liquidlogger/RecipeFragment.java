package com.example.slewson.liquidlogger;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by Marie on 5/21/2015.
 */
public class RecipeFragment extends ListFragment {
    private ArrayList mValues;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initValues();
        String[] values = (String[]) mValues.toArray(new String[mValues.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Toast.makeText(getActivity(),
                String.valueOf(l.getItemAtPosition(position)),
                Toast.LENGTH_LONG).show();
    }

    private void initValues() {
        mValues = new ArrayList();
        mValues.add("Add Recipe");

        ParseObject po = null;

        mValues.add("Recipe 1");
    }
}
