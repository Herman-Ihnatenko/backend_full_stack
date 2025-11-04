package de.ait.training.repository;

import de.ait.training.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository  extends JpaRepository<Car, Long> {
    List<Car> findCarByColorIgnoreCase(String color);
    List<Car> findCarByPriceBetween(Double min, Double max);
    List<Car> findCarByPriceLessThanEqual(Double max);
    List<Car> findCarByPriceGreaterThanEqual(Double min);
}
