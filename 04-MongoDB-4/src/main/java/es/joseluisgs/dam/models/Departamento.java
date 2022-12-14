package es.joseluisgs.dam.models;

import lombok.*;
import org.bson.types.ObjectId;

import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Departamento {
    private ObjectId id;
    private String nombre;

    // Me hago los setter para interfaz fluida

    public Departamento setId(ObjectId id) {
        this.id = id;
        return this;
    }

    public Departamento setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }


    @Override
    public String toString() {
        return "Departamento{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
