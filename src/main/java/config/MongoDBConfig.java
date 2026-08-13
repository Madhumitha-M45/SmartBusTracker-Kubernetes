package config;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;


public class MongoDBConfig {

    private static final String CONNECTION_STRING =
            "mongodb://localhost:27017";

    private static final String DATABASE_NAME =
            "BusTrackerDB";

    private static final MongoClient mongoClient;

    private static final MongoDatabase database;


    static {

        CodecRegistry pojoCodecRegistry =
                fromRegistries(
                        MongoClientSettings
                                .getDefaultCodecRegistry(),

                        fromProviders(
                                PojoCodecProvider
                                        .builder()
                                        .automatic(true)
                                        .build()
                        )
                );


        MongoClientSettings settings =
                MongoClientSettings
                        .builder()
                        .applyConnectionString(
                                new ConnectionString(
                                        CONNECTION_STRING
                                )
                        )
                        .codecRegistry(
                                pojoCodecRegistry
                        )
                        .build();


        mongoClient =
                MongoClients.create(settings);


        database =
                mongoClient.getDatabase(
                        DATABASE_NAME
                );
    }


    public static MongoDatabase getDatabase() {

        return database;
    }


    public static MongoClient getMongoClient() {

        return mongoClient;
    }
}