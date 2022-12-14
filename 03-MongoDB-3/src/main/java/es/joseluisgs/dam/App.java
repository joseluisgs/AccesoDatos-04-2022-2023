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
import es.joseluisgs.dam.models.*;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.*;
import java.util.function.Predicate;

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
        System.out.println("MongoDB Empleados->Departamentos");

        // Cadena de conexion
        ConnectionString connectionString = new ConnectionString("mongodb://mongoadmin:mongopass@localhost/test?authSource=admin");
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
        // Obtenemos las bases de datos
        List<Document> databases = mc.listDatabases().into(new ArrayList<>());
        System.out.println("Todos las bases de datos existentes");
        // databases.forEach(db -> System.out.println(db.toJson()));

        // A partir de aquí Las consultas serán mapeadas a POJOS!!!

        // Me connecto a la base de datos de test
        MongoDatabase db = mc.getDatabase("test");
        // Me conecto a la colección de departamentos
        MongoCollection<Empleado> empleadosCollection = db.getCollection("empleados", Empleado.class);
        // La borro para tenerla limpia
        empleadosCollection.drop();

        // Vamos a crearnos un Departamentos, objectID no es obligatorio crearlo, pero lo necesitamos
        Departamento dep1 = new Departamento(new ObjectId(), "Java Departamento");
        Departamento dep2 = new Departamento(new ObjectId(), "TypeScript Departamento");
        Departamento dep3 = new Departamento(new ObjectId(), "Prueba Departamento");


        // Vamos con los empleados
        System.out.println("Añadiendo empleados...");
        // Empelados del departamento 1
        Empleado emp1 = new Empleado(new ObjectId(), "Pepe Perez", 10000.0, dep1);
        Empleado emp2 = new Empleado(new ObjectId(), "Ana Anaya", 15000.0, dep1);
        Empleado emp3 = new Empleado(new ObjectId(), "Luis Lopez", 11000.0, dep2);
        Empleado emp4 = new Empleado(new ObjectId(), "Pedro Perez", 14000.0, dep2);
        Empleado emp5 = new Empleado(new ObjectId(), "Elena Fernandez", 13000.0, dep2);

        List<Empleado> empleadosInsert = new ArrayList<>(Arrays.asList(emp1, emp2, emp3, emp4, emp5));

        System.out.println("Insertando Empleados");
        empleadosCollection.insertMany(empleadosInsert);


        // Recorremos los empleados
        System.out.println("Mostrando todos los Empleados");
        empleadosCollection.find().into(new ArrayList<>()).forEach(System.out::println);


        // Vamos a cambiar el nombre del empleado y de departamento
        // Buscando
        System.out.println("Buscando...");
        Empleado buscado = empleadosCollection.find(eq("nombre", "Luis Lopez")).first();
        System.out.println("Encontrado:\t" + buscado);

        System.out.println("Actualizando...");
        Document filtered= new Document("_id", buscado.getId()); // también puedo buscar por nombre... que es mas facil
        // Para obtener los datos una vez cambiado
        buscado.setNombre("Empleado Actualizado");
        buscado.setDepartamento(dep3);
        FindOneAndReplaceOptions returnDoc = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
        Empleado actualizado = empleadosCollection.findOneAndReplace(filtered, buscado, returnDoc);
        System.out.println("Actualizado:\t" + actualizado);

        // Eliminado
        System.out.println("Eliminando...");
        filtered= new Document("nombre", "Empleado Actualizado");
        Empleado eliminado = empleadosCollection.findOneAndDelete(filtered);
        System.out.println("Eliminado: " + eliminado);


        // Listamos los datos del departamento 2
        System.out.println("Listamos los datos del departamento 2");
        System.out.println("Buscando...");
        buscado = empleadosCollection.find().into(new ArrayList<>()).stream()
                .filter(e-> e.getDepartamento().getNombre().equals("TypeScript Departamento"))
                .findFirst()
                .get();
        System.out.println("Encontrado:\t" + buscado.getDepartamento());

        System.out.println("Imprimimos los empleados del departamento 2");
        empleadosCollection.find().into(new ArrayList<>()).stream()
                .filter(e-> e.getDepartamento().getNombre().equals("TypeScript Departamento"))
                .forEach(System.out::println);


        System.out.println("Empelados que su salario es >=14000");
        // Y así podríamos seguir con el resto....
        // Podiamos subirles el sueldo, etc, incluso podemos saber los empleados que cobran 14000 o mas
        empleadosCollection.find().into(new ArrayList<>()).stream()
                .filter(e-> e.getSalario()>=14000)
                .forEach(i-> System.out.println("Empleado: " + i.getNombre() + " Salario: " + i.getSalario() +" Departamento: " + i.getDepartamento().getNombre()));

        // O saber cuantos empleados por departamento cobran más de 1400 del departramento 1
        System.out.println("Numero de empleados por departamento 1 cuyo salario es >=14000");
        long total = empleadosCollection.find().into(new ArrayList<>()).stream()
                .filter(e->e.getSalario()>=14000 && e.getDepartamento().getNombre().equals("TypeScript Departamento"))
                .count();
        System.out.println("Número de Empleados con sueldo mayor de 14000 en el Departamento Java: " + total);

        mc.close();

    }

}

