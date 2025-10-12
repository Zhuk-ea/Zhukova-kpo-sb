package moscowzoo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import moscowzoo.domains.Rabbit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VeterinaryClinicTest {

    @Autowired
    private VeterinaryClinic clinic;

    @Test
    @DisplayName("Проверка здоровья животного")
    void testHealthCheck() {
        // Arrange
        Rabbit rabbit = new Rabbit("Тестовый", 1, 5);

        // Act
        boolean isHealthy = clinic.checkHealth(rabbit);

        // Assert
        assertEquals(isHealthy, rabbit.isHealthy(), "Статус здоровья должен совпадать с результатом проверки");
    }

    @Test
    @DisplayName("Проверка существующего статуса здоровья")
    void testIsHealthyMethod() {
        // Arrange
        Rabbit rabbit = new Rabbit("Тестовый", 1, 5);
        rabbit.setHealthy(true);

        // Act & Assert
        assertTrue(clinic.isHealthy(rabbit), "Должен возвращаться текущий статус здоровья");
    }
}