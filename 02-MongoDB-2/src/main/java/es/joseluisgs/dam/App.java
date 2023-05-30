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
        System.out.println("MongoDB Departamentos->Empleados");

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
        MongoCollection<Departamento> departamentosCollection = db.getCollection("departamentos", Departamento.class);
        // La borro para tenerla limpia
        departamentosCollection.drop();

        // Vamos a crearnos un Departamentos, objectID no es obligatorio crearlo, pero lo necesitamos
        Departamento dep1 = new Departamento(new ObjectId(), "Java Departamento", new HashSet<Empleado>());
        Departamento dep2 = new Departamento(new ObjectId(), "TypeScript Departamento", new HashSet<Empleado>());
        Departamento dep3 = new Departamento(new ObjectId(), "Prueba Departamento", new HashSet<Empleado>());

        List<Departamento> departamentosInsert = new ArrayList<>(Arrays.asList(dep1, dep2, dep3));

        System.out.println("Insertando Departamentos");
        departamentosCollection.insertMany(departamentosInsert);

        // Recorremos los departamentos
        System.out.println("Mostrando todos los departamentos");
        departamentosCollection.find().into(new ArrayList<>()).forEach(System.out::println);

        // Vamos a cambiar el nombre del departamento 3
        // Buscando
        System.out.println("Buscando...");
        Departamento buscado = departamentosCollection.find(eq("nombre", "Prueba Departamento")).first();
        System.out.println("Encontrado:\t" + buscado);

        System.out.println("Actualizando...");
        Document filtered= new Document("_id", buscado.getId()); // también puedo buscar por nombre... que es mas facil
        // Para obtener los datos una vez cambiado
        buscado.setNombre("Actualizado Departamento");
        FindOneAndReplaceOptions returnDoc = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
        Departamento actualizado = departamentosCollection.findOneAndReplace(filtered, buscado, returnDoc);
        System.out.println("Actualizado:\t" + actualizado);

        // Eliminado
        System.out.println("Eliminando...");
        filtered= new Document("nombre", "Actualizado Departamento");
        Departamento eliminado = departamentosCollection.findOneAndDelete(filtered);
        System.out.println("Eliminado: " + eliminado);

        // Vamos con los empleados
        System.out.println("Añadiendo empleados...");
        // Empelados del departamento 1
        Empleado emp1 = new Empleado(new ObjectId(), "Pepe Perez", 10000.0, dep1.getId());
        dep1.getEmpleados().add(emp1);

        Empleado emp2 = new Empleado(new ObjectId(), "Ana Anaya", 15000.0, dep1.getId());
        dep1.getEmpleados().add(emp2);

        Empleado emp3 = new Empleado(new ObjectId(), "Luis Lopez", 11000.0, dep2.getId());
        dep2.getEmpleados().add(emp3);

        Empleado emp4 = new Empleado(new ObjectId(), "Pedro Perez", 14000.0, dep2.getId());
        dep2.getEmpleados().add(emp4);

        Empleado emp5 = new Empleado(new ObjectId(), "Elena Fernandez", 13000.0, dep2.getId());
        dep2.getEmpleados().add(emp5);

        // Actualizamos
        filtered= new Document("nombre", "Java Departamento"); // también puedo buscar por nombre... que es mas facil
        returnDoc = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
        actualizado = departamentosCollection.findOneAndReplace(filtered, dep1, returnDoc);
        System.out.println("Actualizados los Empleados Dep1: " + actualizado);

        filtered= new Document("nombre", "TypeScript Departamento"); // también puedo buscar por nombre... que es mas facil
        returnDoc = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
        actualizado = departamentosCollection.findOneAndReplace(filtered, dep2, returnDoc);
        System.out.println("Actualizados los Empleados Dep2: " + actualizado);

        // Listamos los datos del departamento 2
        System.out.println("Listamos los datos del departamento 2");
        System.out.println("Buscando...");
        buscado = departamentosCollection.find(eq("nombre", "TypeScript Departamento")).first();
        System.out.println("Encontrado:\t" + buscado);

        System.out.println("Imprimimos los empleados del departamento 2");
        buscado.getEmpleados().forEach(System.out::println);

        System.out.println("Eliminamos el empleado Elena Fernandez");
        buscado.getEmpleados().removeIf(e -> e.getNombre().equals("Elena Fernandez"));
        System.out.println(buscado.getEmpleados());
        // Actualizamos departamento
        filtered= new Document("nombre", "TypeScript Departamento"); // también puedo buscar por nombre... que es mas facil
        returnDoc = new FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER);
        actualizado = departamentosCollection.findOneAndReplace(filtered, buscado, returnDoc);
        System.out.println("Actualizados los Empleados Dep2: " + actualizado);

        System.out.println("Empelados que su salario es >=14000");
        // Y así podríamos seguir con el resto....
        // Podiamos subirles el sueldo, etc, incluso podemos saber los empleados que cobran 14000 o mas
        departamentosCollection.find().into(new ArrayList<>()).forEach(d-> {
            d.getEmpleados().stream().filter(e->e.getSalario()>=14000)
                    .forEach(e-> System.out.println("Departamento: " + d.getNombre() + " - " + e));
        });

        // O saber cuantos empleados por departamento cobran más de 15000
        System.out.println("Numero de empleados por departamento cuyo salario es >=15000");
        departamentosCollection.find().into(new ArrayList<>()).forEach(d-> {
            long total =
            d.getEmpleados().stream().filter(e->e.getSalario()>=15000).count();
            System.out.println("Departamento: " + d.getNombre() + " - " + total);
        });

        mc.close();

    }

}

