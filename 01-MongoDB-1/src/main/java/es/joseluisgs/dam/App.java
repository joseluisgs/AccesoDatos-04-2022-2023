package es.joseluisgs.dam;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;
import es.joseluisgs.dam.models.Grade;
import es.joseluisgs.dam.models.Prueba;
import es.joseluisgs.dam.models.Score;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hola a MongoDB con JAVA");
        Jsonb jsonb = JsonbBuilder.create();

        Prueba p = new Prueba(1, "Pepe");
        System.out.println(p);
        System.out.println(jsonb.toJson(p));


        ejemploDocumentosSinMapear();

        ejemploDocumentosMapeados();

        // ¿Que te gusta más?

    }

    public static void ejemploDocumentosSinMapear() {
        ConnectionString connectionString = new ConnectionString("mongodb://mongoadmin:mongopass@localhost/test?authSource=admin");
        MongoClient mongoClient = MongoClients.create(connectionString);

        // Obtenemos las bases de datos
        List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
        System.out.println("Todos las bases de datos existentes");
        databases.forEach(db -> System.out.println(db.toJson()));

        // Obtenemos la base de datos que necesitamos
        MongoDatabase testDB = mongoClient.getDatabase("test");
        MongoCollection<Document> pruebaCollection = testDB.getCollection("prueba");
        // testDB.drop(); // Si queremos borrar toda la base de datos

        // Mostramos todos los documentos...
        System.out.println("Todos los documentos de la BD prueba");
        List<Document> collectionList = pruebaCollection.find().into(new ArrayList<>());
        collectionList.forEach(c -> System.out.println(c.toJson()));


        // Borramos todos los datos de Grades
        MongoCollection<Document> gradesCollection = testDB.getCollection("grades");
        gradesCollection.drop();

        // Vamos a crear un documento BSON
        Random rand = new Random();
        // Un studainte tiene una lista de pruebas
        Document student = new Document("_id", new ObjectId());
        student.append("student_id", 10000d)
                .append("class_id", 5d)
                .append("name", "Pepe Perez")
                .append("email", "pepe@pepe.com")
                .append("scores",
                        asList(new Document("type", "exam").append("score", rand.nextDouble() * 100),
                                new Document("type", "quiz").append("score", rand.nextDouble() * 100),
                                new Document("type", "homework").append("score", rand.nextDouble() * 100),
                                new Document("type", "homework").append("score", rand.nextDouble() * 100)));


        // Operaciones CRUD

        // Insertamos
        System.out.println("Insertamos Estudiante");
        gradesCollection.insertOne(student);
        // Podemos crear un array de documentos e insertar varios del tiron con insertMany
        // gradesCollection.insertMany(grades, new InsertManyOptions().ordered(false));

        // Leer
        System.out.println("Estudiante con ID = 10000");
        Document student1 = gradesCollection.find(new Document("student_id", 10000)).first();
        System.out.println("Student 1: " + student1.toJson());

        // Podemos hacer búsquedas con filtros
        System.out.println("Lista de estudiantes con ID > 10000");
        List<Document> studentList = gradesCollection.find(gte("student_id", 10000)).into(new ArrayList<>());
        studentList.forEach(s -> System.out.println(s.toJson()));

        // Indice
        Document indice = new Document();
        gradesCollection.createIndex(Indexes.ascending("student_id"));
        gradesCollection.createIndex(Indexes.descending("class_id"));

        // Super consulta
        List<Document> docs = gradesCollection.find(and(eq("student_id", 10000), lte("class_id", 1)))
                .projection(fields(excludeId(),
                        include("class_id",
                                "student_id")))
                .sort(descending("class_id"))
                .skip(2)
                .limit(2)
                .into(new ArrayList<>());

        System.out.println("Estudiantes ordenados,  eliminados y limitados: ");
        for (Document s : docs) {
            System.out.println(s.toJson());
        }

        System.out.println("Actualizando documentos");
        // Update, upsert, updateMany e FindUpdate findReplace
        // findOneAndUpdate, lo buscamos o con find and replace
        Bson filter = eq("student_id", 10000);
        Bson update1 = inc("x", 10); // incrementa x en 10, como no existe lo crea
        Bson update2 = rename("class_id", "new_class_id"); // renombra "class_id" en "new_class_id".
        Bson update3 = mul("scores.0.score", 2); // multiplica por 2 el valor de la posicion 0 de scores
        Bson update4 = addToSet("comments", "This comment is uniq"); // crea un array con comment
        Bson update5 = addToSet("comments", "This comment is uniq"); // Como es el mismo en un conjunto no tiene efecto
        Bson updates = combine(update1, update2, update3, update4, update5); // combinamos todas las actualizaciones

        Document oldVersion = gradesCollection.findOneAndUpdate(filter, updates);
        System.out.println("\n=> FindOneAndUpdate. Imprime la versión vieja por defecto:");
        System.out.println(oldVersion.toJson());
        // Pero podemos conseguir la nueva ñadiendo las FindUpdateOptions
        filter = eq("student_id", 10000);
        FindOneAndUpdateOptions optionAfter = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER);
        Document newVersion = gradesCollection.findOneAndUpdate(filter, updates, optionAfter);
        System.out.println("\n=> FindOneAndUpdate. Nueva versin del documento por defecto:");
        System.out.println(newVersion.toJson());

        System.out.println("Eliminando documentos");
        filter = eq("student_id", 10000);
        DeleteResult result = gradesCollection.deleteOne(filter);
        System.out.println(result);

        // Tambén puedo usar Bson filter = eq("student_id", 10002);
        //Document doc = gradesCollection.findOneAndDelete(filter);
        // Devuelve el documento


        // Vuelvo a insertar quiero que haya uno
        student = new Document("_id", new ObjectId());
        student.append("student_id", 10000d)
                .append("class_id", 5d)
                .append("name", "Pepe Perez")
                .append("email", "pepe@pepe.com")
                .append("scores",
                        asList(new Document("type", "exam").append("score", rand.nextDouble() * 100),
                                new Document("type", "quiz").append("score", rand.nextDouble() * 100),
                                new Document("type", "homework").append("score", rand.nextDouble() * 100),
                                new Document("type", "homework").append("score", rand.nextDouble() * 100)));


        // Insertamos
        System.out.println("Insertamos Estudiante");
        gradesCollection.insertOne(student);

        mongoClient.close();

    }

    public static void ejemploDocumentosMapeados() {
        Jsonb jsonb = JsonbBuilder.create();
        ConnectionString connectionString = new ConnectionString("mongodb://mongoadmin:mongopass@localhost/test?authSource=admin");
        // Mapeando
        System.out.println("Mapeando conexiones con objetos");
        // Primero debo crearme una conexión que permita el paso a POJO
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        // Configuramos el conector de Mongo

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();

        // Creamos el cliente de Mongo
        com.mongodb.client.MongoClient mc = MongoClients.create(clientSettings);
        // Nos conectamos a la base de datos
        MongoDatabase db = mc.getDatabase("test");
        // Ahora sí que lo tenemos todo tipado
        MongoCollection<Grade> grades = db.getCollection("grades", Grade.class);
        System.out.println("Conexion Mapeado con mis POJOS");

        // Listar
        System.out.println("Listando con mis POJOS");
        grades.find().into(new ArrayList<>()).forEach(g->System.out.println(jsonb.toJson(g)));
        grades.find().into(new ArrayList<>()).forEach(System.out::println);



        // Insertando
        System.out.println("Insertando con POJOS");
        Score newScore = new Score();
        newScore.setType("homework");
        newScore.setScore(50d);
        Grade newGrade = new Grade();
        newGrade.setStudentId(10003d);
        newGrade.setClassId(10d);
        newGrade.setEmail("insert@insert.com");
        newGrade.setName("Fernando Fernandez");
        newGrade.setScores(singletonList(newScore));
        grades.insertOne(newGrade);
        System.out.println("Objeto insertado");

        // Buscando
        System.out.println("Buscando con POJOS");
        Grade grade = grades.find(eq("student_id", 10003d)).first();
        System.out.println("Encontrado:\t" + grade);

        //Actualizando insertando un grado
        System.out.println("Actualizando con POJOS");
        List<Score> newScores = new ArrayList<>(grade.getScores());
        newScore.setType("exam");
        newScore.setScore(42d);
        newScores.add(newScore);
        grade.setScores(newScores);
        grade.setName("Actualizando");
        // Lo busco
        Document filterByGradeId = new Document("_id", grade.getId());
        FindOneAndReplaceOptions returnDocAfterReplace = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
        Grade updatedGrade = grades.findOneAndReplace(filterByGradeId, grade, returnDocAfterReplace);
        System.out.println("Actualizado:\t" + updatedGrade);

        // Eliminado
        System.out.println("Eliminando con POJOS");
        // System.out.println("Grade deleted:\t" + grades.deleteOne(filterByGradeId));
        Grade deleteGrade = grades.findOneAndDelete(filterByGradeId);
        System.out.println("Eliminado: " + deleteGrade);

        mc.close();
    }


}

