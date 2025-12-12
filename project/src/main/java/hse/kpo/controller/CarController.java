package hse.kpo.controller;

import hse.kpo.domains.cars.Car;
import hse.kpo.dto.CarRequest;
import hse.kpo.facade.Hse;
import hse.kpo.storages.CarStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@Tag(name = "Автомобили", description = "Управление транспортными средствами")
public class CarController {

    private final Hse hse;
    private final CarStorage carStorage;

    @GetMapping("/{vin}")
    @Operation(summary = "Получить автомобиль по VIN")
    public ResponseEntity<Car> getCarByVin(@PathVariable int vin) {
        Car car = carStorage.getCars().stream()
                .filter(c -> c.getVin() == vin)
                .findFirst()
                .orElse(null);

        if (car != null) {
            return ResponseEntity.ok(car);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Создать новый автомобиль")
    public ResponseEntity<String> createCar(@Valid @RequestBody CarRequest request,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce("", (a, b) -> a + "\n" + b);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        try {
            switch (request.engineType()) {
                case "PEDAL" -> hse.addPedalCar(request.pedalSize());
                case "HAND" -> hse.addHandCar();
                case "LEVITATION" -> hse.addLevitationCar();
                default -> throw new IllegalArgumentException("Неизвестный тип двигателя");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Автомобиль создан успешно");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Получить все автомобили")
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carStorage.getCars();
        return ResponseEntity.ok(cars);
    }

}