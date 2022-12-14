package es.joseluisgs.dam.models;

import lombok.Data;

@Data
public class Score {
    private String type;
    private Double score;

    @Override
    public String toString() {
        return "Score{" +
                "type='" + type + '\'' +
                ", score=" + score +
                '}';
    }
}