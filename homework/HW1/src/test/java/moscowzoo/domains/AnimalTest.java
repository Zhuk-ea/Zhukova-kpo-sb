package moscowzoo.domains;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AnimalTest {

    @Test
    @DisplayName("Проверка травоядного животного для контактного зоопарка")
    void testHerbivoreContactZooEligibility() {
        // Arrange
        Rabbit kindRabbit = new Rabbit("Добрый", 1, 7); // Доброта > 5
        Rabbit angryRabbit = new Rabbit("Злой", 1, 3);  // Доброта < 5

        // Act & Assert
        assertTrue(kindRabbit.canGoToContactZoo(), "Добрый кролик должен подходить для контактного зоопарка");
        assertFalse(angryRabbit.canGoToContactZoo(), "Злой кролик не должен подходить для контактного зоопарка");

        // Проверка с больным животным
        angryRabbit.setHealthy(false);
        assertFalse(angryRabbit.canGoToContactZoo(), "Больное животное не должно подходить для контактного зоопарка");
    }

    @Test
    @DisplayName("Проверка хищника для контактного зоопарка")
    void testPredatorContactZooEligibility() {
        // Arrange
        Tiger tiger = new Tiger("Тигр", 10);

        // Act & Assert
        assertFalse(tiger.canGoToContactZoo(), "Хищник никогда не должен подходить для контактного зоопарка");
    }

    @Test
    @DisplayName("Проверка наследования интерфейсов")
    void testInterfaceImplementation() {
        // Arrange
        Rabbit rabbit = new Rabbit("Тест", 1, 5);

        // Act & Assert
        assertInstanceOf(moscowzoo.interfaces.Alive.class, rabbit, "Кролик должен реализовывать Alive");
        assertInstanceOf(moscowzoo.interfaces.Inventory.class, rabbit, "Кролик должен реализовывать Inventory");
        assertEquals(1, rabbit.getFood(), "Должен возвращаться установленный уровень еды");
    }
}