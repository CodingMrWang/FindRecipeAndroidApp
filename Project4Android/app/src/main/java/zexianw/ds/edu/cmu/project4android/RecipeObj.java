package zexianw.ds.edu.cmu.project4android;

import android.graphics.Bitmap;

/**
 * RecipeObj to store pic, recipe, title, author info
 */
public class RecipeObj {
    private Bitmap pic;
    private String recipe;
    private String title;
    private String author;

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public RecipeObj(Bitmap pic, String recipe, String title, String author) {
        this.pic = pic;
        this.recipe = recipe;
        this.title = title;
        this.author = author;
    }
}
