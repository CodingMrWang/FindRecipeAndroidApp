package zexianw.ds.edu.cmu.project4android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * UI thread
 */
public class GetRecipe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_recipe);
        final GetRecipe gr = this;
//        set action to button
        Button searchBut = (Button)findViewById(R.id.search);
        searchBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View viewParam) {
//                first set textview to be patient
                TextView textBlock = (TextView) findViewById(R.id.textblock);
                textBlock.setText(getString(R.string.str, "Be patient, good things take time"));
                String searchFood = ((EditText) findViewById(R.id.searchField)).getText().toString();
                RecipeApi api = new RecipeApi();
//                pass this to api as a callback way
                api.search(searchFood, gr);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * when the aysn task is done, set content to the UI
     * @param result
     */
    public void resultReady(RecipeObj result) {
        ImageView pictureView = (ImageView)findViewById(R.id.img);
        TextView textBlock = (TextView) findViewById(R.id.textblock);
        TextView searchView = (TextView) findViewById(R.id.searchField);
        TextView recipeblock = (TextView) findViewById(R.id.recipe);
//       if has result, set pic, text
        if (result != null && result.getTitle().length() != 0) {
            pictureView.setImageBitmap(result.getPic());
            pictureView.setVisibility(View.VISIBLE);
            textBlock.setText(getString(R.string.text, result.getTitle(), result.getAuthor()));
            recipeblock.setText(getString(R.string.str, result.getRecipe()));
        } else if (result != null){
//            this is when there is no search result
            pictureView.setImageBitmap(result.getPic());
            pictureView.setVisibility(View.VISIBLE);
            textBlock.setText("");
            recipeblock.setText("");
            textBlock.setText(getString(R.string.noblock));
        } else {
//            this is when there is an exception in asyn task
            pictureView.setImageResource(R.mipmap.ic_launcher);
            pictureView.setVisibility(View.INVISIBLE);
            textBlock.setText("");
            recipeblock.setText("");
            textBlock.setText(getString(R.string.noblock));
        }
        searchView.setText("");
        pictureView.invalidate();
    }
}
