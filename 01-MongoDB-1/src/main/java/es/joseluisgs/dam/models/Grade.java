package es.joseluisgs.dam.models;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.List;

@Data
public class Grade {
    private ObjectId id;
    @BsonProperty(value = "student_id")
    private Double studentId;
    @BsonProperty(value = "class_id")
    private Double classId;
    private String name;
    private String email;
    private List<Score> scores;

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", classId=" + classId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", scores=" + scores +
                '}';
    }
}