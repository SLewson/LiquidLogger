package com.example.slewson.liquidlogger.model;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marie on 6/3/2015.
 */
public class ParseManager {

    public static List<ParseObject> getAllRecipes() {
        List<ParseObject> parseObjects = new ArrayList<>();
        ParseQuery<ParseObject> query = new ParseQuery<>("Recipe");

        try {
            parseObjects = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects,
//                             ParseException e) {
//                if (e == null) {
//                    for (ParseObject object : objects) {
//                        parseObjects.add(object);
//                        Log.e("Recipe", object.getString("name"));
//                    }
//                } else {
//                    Log.e("Recipe", "Error: " + e.getMessage());
//                }
//
//                callback.onParseComplete(parseObjects);
//            }
//        });

        return parseObjects;
    }

    public static void deleteRecipe(final ParseObject po) {
        po.deleteInBackground();
    }
}
