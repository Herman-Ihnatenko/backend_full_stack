package de.ait.training.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer price;

    public Car(int i, String color, String model, Integer price) {
        this.color = color;
        this.model = model;
        this.price = price;
    }
}