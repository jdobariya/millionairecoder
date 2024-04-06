package com.millionairecodergame.millionairecoder.service;

import com.millionairecodergame.millionairecoder.model.Player;
import com.millionairecodergame.millionairecoder.service.customcodec.BooleanArrayCodec;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.print.Doc;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


public class DatabaseService {
    private static final String DATABASE_NAME = "millionairecoder";
    private static final String COLLECTION_NAME = "players";
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Player> collection;

    private static final String connectionString = "mongodb://localhost:27017";
    ConnectionString uri = new ConnectionString(connectionString);
    CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
    CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry, CodecRegistries.fromCodecs(new BooleanArrayCodec()));
    MongoClientSettings clientSettings = MongoClientSettings.builder()
            .applyConnectionString(uri)
            .codecRegistry(codecRegistry)
            .build();
    public DatabaseService() {
        // Establish MongoDB connection
        MongoClient mongoClient = MongoClients.create(clientSettings);
        this.mongoClient = mongoClient;
        this.database = mongoClient.getDatabase(DATABASE_NAME);
        this.collection = this.database.getCollection(COLLECTION_NAME, Player.class);

    }

    // Insert a document into the collection
    public String insertDocument(Player player) {
        InsertOneResult result = this.collection.insertOne(player);
        if(result.wasAcknowledged()) {
            return result.getInsertedId().asObjectId().getValue().toString();
        }
        return null;
    }

    // Read a document from the collection
    public Player findDocumentById(String id, String playerName){
        if (id == null) {
            return this.collection.find(eq("playerName", playerName)).first();
        }
        ObjectId objectId = new ObjectId(id);
        return this.collection.find(eq("_id", objectId)).first();
    }

    public FindIterable<Player> findDocumentsByField(Bson filter){
        return this.collection.find(filter);
    }

    // Update a document in the collection
    public Player updateDocument(String id, Document update) {
        Bson filter = Filters.eq("_id", new ObjectId(id));
        UpdateResult result = this.collection.updateOne(filter, update);
        if(result.wasAcknowledged()){
            return this.collection.find(eq("_id", new ObjectId(id))).first();
        }
        return null;
    }

    // Delete a document from the collection
    public Player deleteDocument(String id) {
        Bson filter = Filters.eq("_id", new ObjectId(id));
        return this.collection.findOneAndDelete(filter);
    }

    public MongoDatabase getDatabase() {
        return this.database;
    }
    public MongoCollection<Player> getCollection() {
        return this.collection;
    }

    // Close the MongoDB client when shutting down the application
    public void close() {
        this.mongoClient.close();
    }
}

