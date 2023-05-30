conn = new Mongo();
// Nos vamos a la base de datos test,
// si no lo haría por defecto en la BD definida en el docker-compose.yml MONGO_INITDB_DATABASE
db = conn.getDB("test");
// Borramos todas las colecciones
// db.collection.drop();

// Y en la colección prueba creamos un índice e insertamos
db.prueba.createIndex({ "address.zip": 1 }, { unique: false });

db.prueba.insert({ "address": { "city": "Paris", "zip": "123" }, "name": "Mike", "phone": "1234" });
db.prueba.insert({ "address": { "city": "Marsel", "zip": "321" }, "name": "Helga", "phone": "4321" });