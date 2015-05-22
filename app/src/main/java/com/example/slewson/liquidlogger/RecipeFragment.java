package com.example.slewson.liquidlogger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.slewson.liquidlogger.model.RecipeObject;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marie on 5/21/2015.
 */
public class RecipeFragment extends ListFragment {
    private ArrayList<ParseObject> mValues;
    private RecipeAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mValues = new ArrayList<>();
        initValues();

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int pos, long id) {
                ParseObject po = (ParseObject) getListView().getItemAtPosition(pos);
                deleteRecipe(po, pos);
                return true;
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ParseObject po = (ParseObject) l.getItemAtPosition(position);
        RecipeObject selectedRecipe = new RecipeObject(po.getString("name"), po.getDouble("pH"),
                po.getDouble("temp"), po.getString("notes"), po.getObjectId());

        Intent i = new Intent(getActivity(), RecipeActivity.class);
        i.putExtra("recipe", selectedRecipe);
        startActivity(i);
    }

    private void initValues() {
        mValues = new ArrayList<>();

        ParseQuery<ParseObject> query = new ParseQuery<>("Recipe");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects,
                             ParseException e) {
                if (e == null) {
                    String myObject = objects.toString();

                    for (ParseObject object : objects) {
                        mValues.add(object);
                        Log.e("Recipe", object.getString("name"));
                    }
                    adapter = new RecipeAdapter(getActivity(), mValues);
                    setListAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("Recipe", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void deleteRecipe(final ParseObject po, int position) {
        final int deletePosition = position;

        AlertDialog.Builder alert = new AlertDialog.Builder(
                getActivity());

        alert.setTitle("Delete");
        alert.setMessage("Do you want delete this item?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TOD O Auto-generated method stub

                // main code on after clicking yes
                mValues.remove(deletePosition);
                po.deleteInBackground();
                adapter.notifyDataSetChanged();
                adapter.notifyDataSetInvalidated();
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    public class RecipeAdapter extends ArrayAdapter<ParseObject> {
        public RecipeAdapter(Context context, ArrayList<ParseObject> recipes) {
            super(context, R.layout.item_recipe, recipes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            ParseObject recipe = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_recipe, parent, false);
            }
            // Lookup view for data population
            TextView name = (TextView) convertView.findViewById(R.id.recipeName);
            // Populate the data into the template view using the data object
            name.setText(recipe.getString("name"));
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
