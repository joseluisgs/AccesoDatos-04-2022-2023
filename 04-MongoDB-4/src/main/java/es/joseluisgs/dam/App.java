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
import es.joseluisgs.dam.database.MongoDBController;
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

import static com.mongodb.client.model.Aggregates.lookup;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson Nos tenemos que conectar a las colecciones sin tipos para operar con los documentos
        MongoCollection<Document> empleados = mongoController.getCollection("test", "empleados", Document.class);
        MongoCollection<Document> departamentos = mongoController.getCollection("test", "departamentos", Document.class);

        // indicamos como es e lookup: a que coleccion, en mi campo y cual es su clave externa y como lo inserto
        Bson pipeline = lookup("departamentos", "departamento_id", "_id", "mi_departamento");
        // Creamos el agregado
        List<Document> empleadosJoined = empleados.aggregate(singletonList(pipeline)).into(new ArrayList<>());
        empleadosJoined.forEach(System.out::println);

        System.out.println("Por cada Departamento, mostrar sus empleados");
        pipeline = lookup("empleados", "_id", "departamento_id", "mis_empleados");
        List<Document> departamentosJoined = departamentos.aggregate(singletonList(pipeline)).into(new ArrayList<>());
        departamentosJoined.forEach(System.out::println);

        mongoController.close();.codecs.configuration.CodecRegistries.fromRegistries;


/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("MongoDB Empleados->Departamentos. Pero cada uno en una colección");
        System.out.println("MongoDB Empleados->Departamentos. Joins");

        // Cadena de conexion
        // Creo el Controlador
        MongoDBController mongoController = MongoDBController.getInstance();
        mongoController.open();


        // Obtenemos las bases de datos
        Optional<List<Document>> databases = mongoController.getDataBases();
        System.out.println("Todos las bases de datos existentes");
        databases.ifPresent(documents -> documents.forEach(db -> System.out.println(db.toJson())));

        // A partir de aquí Las consultas serán mapeadas a POJOS!!!

        // Me conecto a la colección de departamentos
        MongoCollection<Empleado> empleadosCollection = mongoController
                .getCollection("test", "empleados", Empleado.class);
        MongoCollection<Departamento> departamentosCollection = mongoController
                .getCollection("test", "departamentos", Departamento.class);

        // La borro para tenerla limpia cada colección
        mongoController.removeCollection("test", "empleados");
        mongoController.removeCollection("test", "departamentos");

        // incluso la BD completa
        mongoController.removeDataBase("test");

        // Debemos tener en cuenta que la base de datos y las colecciones se crean cuando se necesitan...



        // Vamos a crearnos un Departamentos, objectID no es obligatorio crearlo, pero lo necesitamos
        System.out.println("Añadiendo Departamentos...");
        Departamento dep1 = new Departamento(new ObjectId(), "Java Departamento");
        Departamento dep2 = new Departamento(new ObjectId(), "TypeScript Departamento");
        Departamento dep3 = new Departamento(new ObjectId(), "Prueba Departamento");
        Departamento dep4 = new Departamento(new ObjectId(), "Inserto en Solitario");

        List<Departamento> departamentosInsert = new ArrayList<>(Arrays.asList(dep1, dep2, dep3));

        System.out.println("Insertando Departamentos todos en uno");
        departamentosCollection.insertMany(departamentosInsert);
        System.out.println("Insertando Departamentos solo uno");
        departamentosCollection.insertOne(dep4);


        // Vamos con los empleados
        System.out.println("Añadiendo empleados...");
        // Empelados del departamento 1
        Empleado emp1 = new Empleado(new ObjectId(), "Pepe Perez", 10000.0, dep1.getId());
        Empleado emp2 = new Empleado(new ObjectId(), "Ana Anaya", 15000.0, dep1.getId());
        Empleado emp3 = new Empleado(new ObjectId(), "Luis Lopez", 11000.0, dep2.getId());
        Empleado emp4 = new Empleado(new ObjectId(), "Pedro Perez", 14000.0, dep2.getId());
        Empleado emp5 = new Empleado(new ObjectId(), "Elena Fernandez", 13000.0, dep2.getId());

        List<Empleado> empleadosInsert = new ArrayList<>(Arrays.asList(emp1, emp2, emp3, emp4, emp5));

        System.out.println("Insertando Empleados");
        empleadosCollection.insertMany(empleadosInsert);

        // Aquí ya podriamos hacer los CRUD que quiseramos como los ejemplos anteriores

        // Recorremos los empleados
        System.out.println("Mostrando todos los Departamentos");
        departamentosCollection.find().into(new ArrayList<>()).forEach(System.out::println);
        System.out.println("Mostrando todos los Empleados");
        empleadosCollection.find().into(new ArrayList<>()).forEach(System.out::println);


        // Listamos los datos del departamento 2
        System.out.println("Listamos los datos del departamento TypeScript");
        System.out.println("Buscando...");
        Departamento departamentoBuscado = departamentosCollection.find(eq("nombre", "TypeScript Departamento")).first();
        System.out.println("Encontrado:\t" + departamentoBuscado);

        ObjectId id = departamentosCollection.find().into(new ArrayList<>()).get(0).getId();
        System.out.println("Listamos los datos del departamento " + id);
        System.out.println("Buscando...");
        departamentoBuscado = departamentosCollection.find(eq("_id", id)).first();
        System.out.println("Encontrado:\t" + departamentoBuscado);

        System.out.println("Imprimimos los empleados del departamento: " + departamentoBuscado.getNombre());
        Departamento finalDepartamentoBuscado = departamentoBuscado;
        empleadosCollection.find().into(new ArrayList<>()).stream()
                .filter(e-> e.getDepartamentoId().equals(finalDepartamentoBuscado.getId()))
                .forEach(System.out::println);


        System.out.println("Empleados que su salario es >=14000");
        // Y así podríamos seguir con el resto....
        // Podiamos subirles el sueldo, etc, incluso podemos saber los empleados que cobran 14000 o mas
        empleadosCollection.find().into(new ArrayList<>()).stream()
                .filter(e-> e.getSalario()>=14000)
                .forEach(i-> {
                    // Voy a sacareles del departamento
                    Departamento dep = departamentosCollection.find(eq("_id", i.getDepartamentoId())).first();
                    System.out.println("Empleado: " + i.getNombre() + " Salario: " + i.getSalario() + " Departamento: " + dep.getNombre());
                });

        // O saber cuantos empleados por departamento cobran más de 13000 del departamento 2
        System.out.println("Numero de empleados por departamento TypeScript cuyo salario es >=13000");
        Departamento dep = departamentosCollection.find(eq("nombre", "TypeScript Departamento")).first();
        long total = empleadosCollection.find().into(new ArrayList<>()).stream()
                .filter(e-> e.getSalario()>=13000 && e.getDepartamentoId().equals(dep.getId())).count();
        System.out.println("Número de Empleados con sueldo mayor de 13000 en el Departamento TypeScript: " + total);

        // O saber cuantos empleados por departamento cobran más de 13000 d
        System.out.println("Numero de empleados por departamento cuyo salario es >=13000");
        departamentosCollection.find().into(new ArrayList<>()).forEach(d-> {
            long totalEmpleados = empleadosCollection.find().into(new ArrayList<>()).stream()
                    .filter(e-> e.getSalario()>=13000 && e.getDepartamentoId().equals(d.getId()))
                    .count();
            System.out.println("Departamento: " + d.getNombre() + " Total Empleados salario >= 13000: " + totalEmpleados);
        });

        // Listar todos los empleados de los departamentos
        System.out.println("Por cada Departamento listar sus empleados");
        departamentosCollection.find().into(new ArrayList<>()).forEach(d-> {
            System.out.println("Lista de empleados del departamento: " + d.getNombre());
            empleadosCollection.find().into(new ArrayList<>()).stream()
                    .filter(e-> e.getDepartamentoId().equals(d.getId()))
                    .forEach(e-> {
                         System.out.println("\tNombre: " + e.getNombre() + " - Salario: " + e.getSalario());
                    });
        });

        // Listar em empleado y su información de departamento
        System.out.println("Por cada empleado mostrar su departamento");
        empleadosCollection.find().into(new ArrayList<>()).forEach(e-> {
            Departamento d = departamentosCollection.find(eq("_id", e.getDepartamentoId())).first();
            System.out.println(e + "\n\t" + d.toString());
        });

        // Ahora vamos a jugar con las lookup y las agregaciones o los joins en Mongo
        System.out.println("Por cada empleado mostrar su departamento usando joins");
        // Nos tenemos que conectar a las colecciones sin tipos para operar con los documentos
        MongoCollection<Document> empleados = mongoController.getCollection("test", "empleados", Document.class);
        MongoCollection<Document> departamentos = mongoController.getCollection("test", "departamentos", Document.class);

        // indicamos como es e lookup: a que coleccion, en mi campo y cual es su clave externa y como lo inserto
        Bson pipeline = lookup("departamentos", "departamento_id", "_id", "mi_departamento");
        // Creamos el agregado
        List<Document> empleadosJoined = empleados.aggregate(singletonList(pipeline)).into(new ArrayList<>());
        empleadosJoined.forEach(System.out::println);

        System.out.println("Por cada Departamento, mostrar sus empleados");
        pipeline = lookup("empleados", "_id", "departamento_id", "mis_empleados");
        List<Document> departamentosJoined = departamentos.aggregate(singletonList(pipeline)).into(new ArrayList<>());
        departamentosJoined.forEach(System.out::println);

        mongoController.close();

    }


}

