package zexianw.ds.edu.cmu.project4android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class RecipeApi {
    GetRecipe gr = null;
    public void search(String searchFood, GetRecipe gr) {
        // use callback so that can set picture back to UI thread
        this.gr = gr;
//        do search in async task
        new AsyncRecipeSearch().execute(searchFood);
    }
    private class AsyncRecipeSearch extends AsyncTask<String, Void, RecipeObj> {
          protected RecipeObj doInBackground(String... urls) {
              RecipeObj result = search(urls[0]);
              return result;
          }

          protected void onPostExecute(RecipeObj result) {gr.resultReady(result);}

        /**
         * search for the keyword input by user and return all info needed
         * @param searchFood String
         * @return RecipeObj
         */
          private RecipeObj search(String searchFood) {
              String searchWord = null;
              try {
//                  encode the search word
                  searchWord = URLEncoder.encode(searchFood, "UTF-8");
//                  https://project4task1-tskcvcjbcz.now.sh
                  String urlString = "https://project4task2-epezqehmra.now.sh/recipe?food=" + searchWord;
//                 send get request and get json response
                  String response = searchData(urlString);
//                  parse the json string
                  JSONParser parser = new JSONParser();
                  JSONArray array = (JSONArray)parser.parse(response);
                  if (array.size() > 0) {
//                      if has search result, store them in recipe object and return
                      JSONObject json = (JSONObject) array.get(0);
                      String pic = (String)json.get("pic");
                      Bitmap picture = getRemoteImage(new URL(pic));
                      String recipe = (String)json.get("recipe");
                      String author = (String)json.get("publisher");
                      String title = (String)json.get("title");
                      return new RecipeObj(picture, recipe, title, author);
                  } else {
//                      if no result, parse a nothing pic
                      Bitmap nothing = getRemoteImage(new URL("https://ws4.sinaimg.cn/large/006tNbRwly1fwwxrq9i9lj30hw09q7eg.jpg"));
                      return new RecipeObj(nothing, "", "", "");
                  }
              } catch (ParseException e) {
                  Bitmap nothing = null;
                  try {
//                      if error, put a nothing pic
                      nothing = getRemoteImage(new URL("https://ws4.sinaimg.cn/large/006tNbRwly1fwwxrq9i9lj30hw09q7eg.jpg"));
                      return new RecipeObj(nothing, "", "", "");
                  } catch (MalformedURLException e1) {
                      e1.printStackTrace();
                  }
              } catch (UnsupportedEncodingException e) {
                  e.printStackTrace();
              } catch (MalformedURLException e) {
                  e.printStackTrace();
              }
              return null;
          }

        /**
         * send get request to API and get response
         * @param urlString url
         * @return String response
         */
        public String searchData(String urlString) {
            try {
                URL url = new URL(urlString);
                String response = "";
                /*
                 * Create an HttpURLConnection.  This is useful for setting headers
                 * and for getting the path of the resource that is returned (which
                 * may be different than the URL above if redirected).
                 * HttpsURLConnection (with an "s") can be used if required by the site.
                 */
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                // Read all the text returned by the server
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String str;
                // Read each line of "in" until done, adding each to "response"
                while ((str = in.readLine()) != null) {
                    // str is one line of text readLine() strips newline characters
                    response += str;
                }

                in.close();
                return response;
            } catch (ProtocolException ex) {
                System.out.println(ex);
            } catch (MalformedURLException ex) {
                System.out.println(ex);
            } catch (IOException ex) {
                System.out.println(ex);
            }
            return "";
        }

        /*
         * Given a URL referring to an image, return a bitmap of that image
         */
        private Bitmap getRemoteImage(final URL url) {
            try {
                final URLConnection conn = url.openConnection();
                conn.connect();
//                download the pic and transfer to bitmap with BitmapFactory
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                Bitmap bm = BitmapFactory.decodeStream(bis);
                bis.close();
                return bm;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
