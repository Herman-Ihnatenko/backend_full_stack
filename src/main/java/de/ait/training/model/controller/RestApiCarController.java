package de.ait.training.model.controller;

import de.ait.training.model.Car;
import de.ait.training.reposytory.CarRepository;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Cars", description = "Operation on cars")
@Slf4j
@RestController
@RequestMapping("/api/cars")
@Tag(name = "Car Controller", description = "APIs for managing cars")
public class RestApiCarController {

    private final CarRepository carRepository;
    Car carOne = new Car(1, "black", "BMW x5", 25000);
    Car carTwo = new Car(2, "green", "Audi A4", 15000);
    Car carThree = new Car(3, "black", "Mercedes", 35000);
    Car carFour = new Car(4, "red", "Ferrari", 250000);

    List<Car> cars = new ArrayList<>();

    public RestApiCarController(CarRepository carRepository) {
        cars.add(carOne);
        cars.add(carTwo);
        cars.add(carThree);
        cars.add(carFour);
        this.carRepository = carRepository;
    }

    /**
     * GET /api/cars
     *
     * @return возвращает список всех автомобилей
     */
    @GetMapping
    @Operation(summary = "Get all cars", description = "Retrieves a list of all available cars")
    public ResponseEntity<List<Car>> getCars() {
        return ResponseEntity.ok(cars);
    }

    /**
     * Создает новый автомобиль и добавляет его в лист
     *
     * @param car
     * @return созданный автомобиль
     */
    @Operation(
            summary = "Create car",
            description = "Create a new car",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Created")
            }
    )
    @PostMapping
    Car postCar(@RequestBody Car car) {
        if (car.getId() <= 0) {
            log.error("Car id must be greater than zero");
            Car errorCar = new Car(9999, "000", "000", 9999);
            return errorCar;
        }
        cars.add(car);
        log.info("Car posted successfully");
        return car;
    }

    /**
     * Замена существующего автомобиля, если id не найден то создаем новый
     *
     * @param id
     * @param car
     * @return созданный или найденный автомобиль
     */
    @PutMapping("/{id}")
    ResponseEntity<Car> putCar(@PathVariable long id, @RequestBody Car car) {
        int carIndex = -1;
        for (Car carInList : cars) {
            if (carInList.getId() == id) {
                carIndex = cars.indexOf(carInList);
                cars.set(carIndex, car);
                log.info("Car id " + carInList.getId() + " has been updated");
            }
        }

        return (carIndex == -1)
                ? new ResponseEntity<>(postCar(car), HttpStatus.CREATED)
                : new ResponseEntity<>(car, HttpStatus.OK);
    }

    /**
     * удаляем автомобиль по id
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    void deleteCar(@PathVariable long id) {
        log.info("Delete car with id {}", id);
        cars.removeIf(car -> car.getId() == id);
    }

    @Operation(
            summary = "Find cars by price range",
            description = "Returns a list of cars with price between min and max (inclusive)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of cars"),
                    @ApiResponse(responseCode = "400", description = "Invalid price range parameters")
            }
    )
    @GetMapping("/price/between/{min}/{max}")
    public ResponseEntity<List<Car>> getCarsByPriceBetween(
            @Parameter(description = "Minimum price of the car", required = true) @PathVariable Integer min,
            @Parameter(description = "Maximum price of the car", required = true) @PathVariable Integer max) {

        log.info("Searching for cars with price between {} and {}", min, max);

        if (max < min) {
            log.info("Max price is less than min price, returning empty list");
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<Car> result = new ArrayList<>();
        for (Car car : cars) {
            if (car.getPrice() >= min && car.getPrice() <= max) {
                result.add(car);
            }
        }
        log.info("Found {} cars in price range {} - {}", result.size(), min, max);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Find cars cheaper than or equal to max price",
            description = "Returns a list of cars with price less than or equal to max",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of cars")
            }
    )
    @GetMapping("/price/under/{max}")
    public ResponseEntity<List<Car>> getCarsCheaperThan(
            @Parameter(description = "Maximum price of the car", required = true) @PathVariable Integer max) {

        log.info("Searching for cars with price under {}", max);
        List<Car> result = new ArrayList<>();
        for (Car car : cars) {
            if (car.getPrice() <= max) {
                result.add(car);
            }
        }
        log.info("Found {} cars with price under {}", result.size(), max);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Find cars more expensive than or equal to min price",
            description = "Returns a list of cars with price greater than or equal to min",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of cars")
            }
    )
    @GetMapping("/price/over/{min}")
    public ResponseEntity<List<Car>> getCarsMoreExpensiveThan(
            @Parameter(description = "Minimum price of the car", required = true) @PathVariable Integer min) {

        log.info("Searching for cars with price over {}", min);
        List<Car> result = new ArrayList<>();
        for (Car car : cars) {
            if (car.getPrice() >= min) {
                result.add(car);
            }
        }
        log.info("Found {} cars with price over {}", result.size(), min);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Get cars by color",
            description = "Returns a list of cars filtered by the specified color. The search is case-insensitive.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved cars by color"),
                    @ApiResponse(responseCode = "404", description = "No cars found with the specified color")
            }
    )
    @GetMapping("/color/{color}")
    public ResponseEntity<List<Car>> getCarsByColor(@PathVariable String color,
                                                    @RequestParam(required = false) Integer minPrice,
                                                    @RequestParam(required = false) Integer maxPrice) {
        // TODO: реализовать фильтрацию по цвету
        log.info("Searching for cars with color: {}", color);
        List<Car> filteredCars = carRepository.findCarByColorIgnoreCase(color);

        if (filteredCars.isEmpty()) {
            log.warn("No cars found for color: {}", color);
            return ResponseEntity.ok(filteredCars);
        }
        if (color != null && minPrice != null && maxPrice != null) {
            filteredCars=carRepository.findCarByColorIgnoreCase(color);
        } else if (minPrice != null && maxPrice != null) {
            filteredCars = carRepository.findByPriceBetween(minPrice, maxPrice);
        } else if (minPrice != null) {
            filteredCars = carRepository.findByPriceGreaterThanEqual(minPrice);
        } else if (maxPrice != null) {
            filteredCars = carRepository.findByPriceLessThanEqual(maxPrice);
        } else {
            return ResponseEntity.ok(cars);
        }

        log.info("Found {} cars with color: {}", filteredCars.size(), color, maxPrice, maxPrice);
        return ResponseEntity.ok(filteredCars);
    }
}


