package moscowzoo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import moscowzoo.domains.Animal;
import moscowzoo.domains.Monkey;
import moscowzoo.domains.Rabbit;
import moscowzoo.domains.Table;
import moscowzoo.domains.Tiger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;





@SpringBootTest
class ZooServiceTest {

    @Autowired
    private Zoo zoo;

    @Autowired
    private VeterinaryClinic clinic;

    private Rabbit testRabbit;
    private Tiger testTiger;
    private Table testTable;

    private void clearZooState() {
        // Удаляем всех животных
        zoo.getAnimals().forEach(animal -> zoo.removeByNumber(animal.getNumber()));
        // Удаляем все вещи
        zoo.getThings().forEach(thing -> zoo.removeByNumber(thing.getNumber()));
    }

    @BeforeEach
    void setUp() {
        clearZooState();
        testRabbit = new Rabbit("ТестовыйКролик", 2, 8);
        testTiger = new Tiger("ТестовыйТигр", 10);
        testTable = new Table("ТестовыйСтол");
    }

    @Test
    @DisplayName("Добавление здорового животного в зоопарк")
    void testAddHealthyAnimal() {
        // Act
        boolean added = zoo.addAnimal(testRabbit);

        // Assert
        assertTrue(added, "Здоровое животное должно быть добавлено");
        assertTrue(testRabbit.isHealthy(), "Животное должно быть здоровым");
        assertTrue(testRabbit.getNumber() > 0, "Должен быть назначен инвентарный номер");
    }

    @Test
    @DisplayName("Расчет общего количества еды")
    void testGetFoodForAll() {
        // Arrange
        zoo.addAnimal(testRabbit);
        zoo.addAnimal(testTiger);

        // Act
        int totalFood = zoo.getFoodForAll();

        // Assert
        assertEquals(12, totalFood, "Общее количество еды должно быть 2 + 10 = 12 кг");
    }

    @Test
    @DisplayName("Получение животных для контактного зоопарка, когда таких нет")
    void testGetAnimalsForContactZooWhenEmpty() {
        // Act & Assert
        zoo.getAnimals().forEach(animal -> zoo.removeByNumber(animal.getNumber()));
        List<Animal> contactAnimals = zoo.getAnimalsForContactZoo();
        assertEquals(0, contactAnimals.size(), "Нет животных подходящих для зоопарка для контактного зоопарка");
    }

    @Test
    @DisplayName("Получение животных для контактного зоопарка")
    void testGetAnimalsForContactZoo() {
        // Arrange
        Rabbit kindRabbit = new Rabbit("ДобрыйКролик", 1, 7); // Доброта > 5
        Monkey angryMonkey = new Monkey("ЗлаяОбезьяна", 3, 3); // Доброта < 5

        zoo.addAnimal(kindRabbit);
        zoo.addAnimal(angryMonkey);
        zoo.addAnimal(testTiger);

        // Act
        List<Animal> contactAnimals = zoo.getAnimalsForContactZoo();

        // Assert
        assertEquals(1, contactAnimals.size(), "Только одно животное должно подходить для контактного зоопарка");
        assertEquals("ДобрыйКролик", contactAnimals.get(0).getName());
    }

    @Test
    @DisplayName("Добавление и удаление вещей")
    void testAddAndRemoveThing() {
        // Act
        zoo.addThing(testTable);

        // Assert
        assertTrue(testTable.getNumber() > 0, "Вещь должна получить инвентарный номер");

        // Act & Assert
        assertTrue(zoo.removeByNumber(testTable.getNumber()), "Вещь должна быть удалена по номеру");
    }

    @Test
    @DisplayName("Удаление несуществующего объекта")
    void testRemoveNonExistentObject() {
        // Act & Assert
        assertFalse(zoo.removeByNumber(999), "Удаление несуществующего объекта должно вернуть false");
    }

}