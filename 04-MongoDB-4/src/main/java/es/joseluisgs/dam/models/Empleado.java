package es.joseluisgs.dam.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Empleado {
    private ObjectId id;
    private String nombre;
    private Double salario;
    @BsonProperty(value = "departamento_id")
    private  ObjectId departamentoId;

    // Me creo los setter con interfaz fluida
    public Empleado setId(ObjectId id) {
        this.id = id;
        return this;
    }

    public Empleado setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public Empleado setSalario(Double salario) {
        this.salario = salario;
        return this;
    }

    public Empleado setDepartamentoId(ObjectId departamentoId) {
        this.departamentoId = departamentoId;
        return this;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", salario=" + salario +
                ", departamento_id=" + departamentoId +
                '}';
    }
}
