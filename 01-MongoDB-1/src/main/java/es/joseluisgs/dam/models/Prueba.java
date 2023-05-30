package es.joseluisgs.dam.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Prueba {
    int id;
    String name;

    @Override
    public String toString() {
        return "Prueba{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
