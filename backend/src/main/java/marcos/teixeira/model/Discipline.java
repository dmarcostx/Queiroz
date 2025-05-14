package marcos.teixeira.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Discipline extends PanacheEntity {
    public String name;
    public String course;
    public byte semester;
}