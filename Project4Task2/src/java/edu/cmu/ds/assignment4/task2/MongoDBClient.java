/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.cmu.ds.assignment4.task2;


import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;

/**
 *
 * @author ZEXIAN
 */
public class MongoDBClient {
    MongoDatabase mongoDatabase;
    MongoCollection<Document> collection;
    Document document;
    /**
     * constructor to connect with database
     */
    public MongoDBClient() {
        try {
//            from mlab offical api
            MongoClientURI mongouri = new MongoClientURI("mongodb://13252526:wzx950923@ds247449.mlab.com:47449/spring");
            MongoClient mongoclient = new MongoClient(mongouri);
            mongoDatabase = mongoclient.getDatabase(mongouri.getDatabase());
            System.out.println("Connect successfully");
            document = new Document();
//            get collection
            collection = mongoDatabase.getCollection("statistics");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    /**
     * put record into document
     * @param key String
     * @param value String
     */
    public void put(String key, Object value) {
        document.append(key, value);
    }
    public static void main(String[] args) {
        MongoDBClient client = new MongoDBClient();
        System.out.println(client.toString());
    }
    /**
     * insert document into database.
     */
    public void insertDB() {
        
        collection.insertOne(document);
    }
    /**
     * get all documents in database
     * @return MongoCursor<Document>
     */
    public MongoCursor<Document> getCollections() {
//        search for all docs
        FindIterable<Document> findIterable = collection.find();  
        MongoCursor<Document> mongoCursor = findIterable.iterator(); 
        System.out.println(mongoCursor == null);
        return mongoCursor;
    }
    /**
     * all documents in the collection to String.
     * @return String
     */
    @Override
    public String toString() {
        String result = "";
        FindIterable<Document> findIterable = collection.find();  
        MongoCursor<Document> mongoCursor = findIterable.iterator();  
        while(mongoCursor.hasNext()){  
            result += mongoCursor.next().toJson() + "\n";  
        }  
        return result;
    }
    /**
     * get the analytics of total count as well as success count
     * @return String analytics result.
     */
    public String getCount() {
        List<Document> allDoc = new ArrayList<>();
        long total = collection.countDocuments();
        collection.find(eq("success", 1)).into(allDoc);
        long success = allDoc.size();
        return total + "(" + success + "/" + (total - success) + ")";
    }
    /**
     * get average delay of all requests.
     * @return String average delay
     */
    public String getAvgDelay() {
//        cite from https://stackoverflow.com/questions/40307659/get-average-from-mongo-collection-using-aggrerate
// create filter of monogodb and do aggreate on all documents then get average
        AggregateIterable<org.bson.Document> aggregate = collection.aggregate(Arrays.asList(Aggregates.group("_id", new BsonField("averageDelay", new BsonDocument("$avg", new BsonString("$timeCost"))))));
        Document result = aggregate.first();
        System.out.println(result.toJson());
        double delay = result.getDouble("averageDelay");
        return String.valueOf(delay);
    }
    /**
     * get user count
     * @return String number of users
     */
    public String getUsersCount() {
        MongoCursor<String> list = collection.distinct("clientIp", String.class).iterator();
        int count = 0;
//        go through cursor and count total number
        while (list.hasNext()) {
            count++;
            list.next();
        }
        return String.valueOf(count);
    }
}
