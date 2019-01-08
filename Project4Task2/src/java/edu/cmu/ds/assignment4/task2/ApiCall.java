package edu.cmu.ds.assignment4.task2;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author ZEXIAN
 */
public class ApiCall {
    List<String> keylist = new ArrayList<>();
    /**
     * constructor put keys into keylist
     */
    public ApiCall() {
        keylist.add("0530e2d16f4d5414e91dc0e105887c60");
        keylist.add("67ba978e1d181880c91367859d47c4bb");
        keylist.add("4c2f5b15d205d0f64f51acc1cb3179e9");
        keylist.add("b621d84c8631987a00c78e95202329e4");
        keylist.add("68a31b8538361d530e4f95bba8e16fc3");
    }
    /**
     * return a random key
     * @return String
     */
    private String getRandomKey() {
        return keylist.get(new Random().nextInt(keylist.size()));
    }
    /**
     * get search result from 3rd Party API
     * @param searchWord String
     * @param client MongoDBClient
     * @return String response string
     */
    public String fetchData(String searchWord, MongoDBClient client) {
        try {
            searchWord = URLEncoder.encode(searchWord, "UTF-8");
            String apikey = getRandomKey();
            String urlString = "https://www.food2fork.com/api/search/?key=" + apikey + "&q=" + searchWord + "&count=30";
            // for logs
            client.put("requestUrl_3rd_1", urlString);
            // get response of recipes
            String response = searchData(urlString);
            client.put("response_3rd", response);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            JSONArray array = (JSONArray)json.get("recipes");
            JSONArray recipes = new JSONArray();
            JSONObject recipe = new JSONObject();
            // random select a food and get its recipe through another api
            int ranNum = new Random().nextInt(array.size());
            System.out.println(array.size());
            System.out.println(ranNum);
            JSONObject js = (JSONObject)array.get(ranNum);
            String publisher = (String) js.get("publisher");
            String recipe_id = (String) js.get("recipe_id");
            String title = (String) js.get("title");
            String pic = ((String) js.get("image_url")).replace("http:", "https:");
            pic = pic.replace("http:", "https:");
            String content = getRecipe(recipe_id, client);
            recipe.put("title", title);
            recipe.put("pic", pic);
            recipe.put("publisher", publisher);
            recipe.put("recipe", content);
            recipes.add(recipe);
            // save info into a json and return json string
            return recipes.toJSONString();
        } catch (IOException e) {

            System.out.println(e);
            // Do something reasonable.  This is left for students to do.
        } catch (ParseException ex) {
            Logger.getLogger(ApiCall.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return "";
    }
    
    /**
     * get recipe of a food with recipe id
     * @param recipe_id String
     * @param client MongoDBClient
     * @return recipe content
     */
    private String getRecipe(String recipe_id, MongoDBClient client){
        try {
            String urlString = "https://www.food2fork.com/api/get?key=" + getRandomKey() + "&rId=" + recipe_id;
            client.put("requestUrl_3rd_2", urlString);
            String response = searchData(urlString);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            JSONArray recipe = (JSONArray)((JSONObject)json.get("recipe")).get("ingredients");
            String r = "";
            //parse all recipes
            for (int i = 0; i < recipe.size(); i++) {
                r += recipe.get(i).toString() + "\n";
            }
            return r;
        } catch (ParseException ex) {
            Logger.getLogger(ApiCall.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    /**
     * send request to url and get response
     * @param urlString
     * @return String json response
     */
    public String searchData(String urlString) {
        String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
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
            connection.setRequestProperty("User-Agent", USER_AGENT);
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
            Logger.getLogger(ApiCall.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ApiCall.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ApiCall.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    public static void main(String[] args) {
    }
}
