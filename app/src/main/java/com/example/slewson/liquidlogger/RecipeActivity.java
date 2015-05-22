package com.example.slewson.liquidlogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.slewson.liquidlogger.model.RecipeObject;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by Marie on 5/21/2015.
 */
public class RecipeActivity extends AppCompatActivity {

    private RecipeObject recipe;

    private EditText mEditName;
    private EditText mEditpH;
    private EditText mEditTemp;
    private EditText mEditNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Bundle recipeBundle = getIntent().getExtras();
        recipe = recipeBundle.getParcelable("recipe");
        if (recipe == null) {
            Log.e("mgrap", "Recipe is null");
        }

        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            saveRecipe();
            return true;
        }
        if (id == R.id.action_cancel) {
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mEditName = (EditText) findViewById(R.id.edit_name);
        mEditpH = (EditText) findViewById(R.id.edit_ph);
        mEditTemp = (EditText) findViewById(R.id.edit_temp);
        mEditNotes = (EditText) findViewById(R.id.edit_notes);

        if(recipe.getName() != null){
            mEditName.setText(recipe.getName());
        }

        if(recipe.getpH() != null){
            mEditpH.setText(String.valueOf(recipe.getpH()));
        }

        if(recipe.getTemp() != null){
            mEditTemp.setText(String.valueOf(recipe.getTemp()));
        }

        if(recipe.getNotes() != null){
            mEditNotes.setText(recipe.getNotes());
        }
    }

    private void saveRecipe() {

        if (recipe.getId() != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
            query.getInBackground(recipe.getId(), new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        object.put("name", mEditName.getText().toString());
                        object.put("pH", Double.valueOf(mEditpH.getText().toString()));
                        object.put("temp", Double.valueOf(mEditTemp.getText().toString()));
                        object.put("notes", mEditNotes.getText().toString());
                        object.saveInBackground();
                    } else {
                        Log.e("Recipe Activity", "Id not found");
                    }
                }
            });
        } else {
            ParseObject po = new ParseObject("Recipe");
            po.put("name", mEditName.getText().toString());
            po.put("pH", Double.valueOf(mEditpH.getText().toString()));
            po.put("temp", Double.valueOf(mEditTemp.getText().toString()));
            po.put("notes", mEditNotes.getText().toString());
            po.saveInBackground();
        }
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(i);
    }
}
