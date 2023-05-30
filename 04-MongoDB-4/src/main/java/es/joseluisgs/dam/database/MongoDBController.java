package es.joseluisgs.dam.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import es.joseluisgs.dam.models.Empleado;
import lombok.NonNull;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Controllador de Bases de Datos NoSQL MongoDEB
 */
public class MongoDBController {
    private static MongoDBController controller;

    // Para las conexiones
    private String serverUrl;
    private String serverPort;
    private String dataBaseName;
    private String user;
    private String password;

    ConnectionString connectionString;
    CodecRegistry pojoCodecRegistry;
    CodecRegistry codecRegistry;
    MongoClientSettings clientSettings;

    MongoClient mongoClient;

    private MongoDBController() {
        initConfig();
    }

    /**
     * Devuelve una instancia del controladro MongoDB
     * @return instancia del controlador MongoDB
     */
    public static MongoDBController getInstance() {
        if(controller == null) {
            controller = new MongoDBController();
        }
        return controller;
    }

    /**
     * Carga la configuración de acceso al servidor de Base de Datos
     * Puede ser directa "hardcodeada" o asignada dinámicamente a traves de ficheros .env o properties
     */
    private void initConfig() {
        // Leemos los datos de la base de datos que pueden estar en
        // porperties o en .env
        // imaginemos que el usuario y pasword estaán en .env y el resto en application.properties
        // si no los rellenamos aquí.
        serverUrl = "localhost";
        serverPort = "27017";
        dataBaseName = "test";
        user = "mongoadmin";
        password = "mongopass";

        connectionString = new ConnectionString("mongodb://"+user+":"+password+"@"+serverUrl+":"+serverPort+"/"+dataBaseName+"?authSource=admin");

        pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
            // Configuramos el conector de Mongo
         clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
    }

    /**
     * Abre una conexión con la base ed datos
     */
    public void open() {
        this.mongoClient = MongoClients.create(clientSettings);
    }

    /**
     * Cierra la conexión con la base e datos
     */
    public void close() {
        if (mongoClient!=null) mongoClient.close();
    }

    /**
     * Devuelve el conjunto de Bases de datos Existentes
     * @return Conjunto de Bases de Datos existentes
     */
    public Optional<List<Document>> getDataBases() {
        return Optional.of(mongoClient.listDatabases().into(new ArrayList<>()));
    }

    /**
     * Elimina una base de datos
     * @param dataBaseName Nombre de la Base de Datos
     */
    public void removeDataBase(@NonNull String dataBaseName) {
        MongoDatabase dataBase = mongoClient.getDatabase(dataBaseName);
        dataBase.drop();// Si queremos borrar toda la base de datos
    }

    /**
     * Elimina una colleción de una base de datos
     * @param dataBaseName Nombre de la Base de Datos
     * @param collectionName Nombre de la Colección
     */
    public void removeCollection(@NonNull String dataBaseName, @NonNull String collectionName) {
        MongoDatabase dataBase = mongoClient.getDatabase(dataBaseName);
        dataBase.getCollection(collectionName).drop();
    }

    public <TDocument>MongoCollection<TDocument> getCollection(@NonNull String dataBaseName,
                                                               @NonNull String collectionName,
                                                               @NonNull java.lang.Class<TDocument> aClass) {
        MongoDatabase dataBase = mongoClient.getDatabase(dataBaseName);
        return dataBase.getCollection(collectionName, aClass);
    }

}
