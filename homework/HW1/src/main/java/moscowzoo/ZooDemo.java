package moscowzoo;

import lombok.RequiredArgsConstructor;
import moscowzoo.domains.Computer;
import moscowzoo.domains.Monkey;
import moscowzoo.domains.Rabbit;
import moscowzoo.domains.Table;
import moscowzoo.domains.Tiger;
import moscowzoo.domains.Wolf;
import moscowzoo.services.Zoo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Демонстрационный класс для тестирования функциональности зоопарка.
 */
@Component
@RequiredArgsConstructor
public class ZooDemo implements CommandLineRunner {

    private final Zoo zoo;

    @Override
    public void run(String... args) {
        System.out.println("ДЕМОНСТРАЦИЯ РАБОТЫ ПРОГРАММЫ\n");

        // Добавляем животных
        addAnimals();

        // Добавляем вещи
        addThings();

        // Демонстрируем функциональность
        demonstrateFunctionality();

        System.out.println("\nДемонстрация завершена");
    }

    private void addAnimals() {
        System.out.println("\nДОБАВЛЕНИЕ ЖИВОТНЫХ");

        // Травоядные с разным уровнем доброты
        Rabbit rabbit = new Rabbit("Кролик1", 1, 8);
        Monkey monkey = new Monkey("Обезьяна1", 3, 4);
        Rabbit rabbit2 = new Rabbit("Кролик2", 1, 6);

        // Хищники
        Tiger tiger = new Tiger("Тигр1", 10);
        Wolf wolf = new Wolf("Волк1", 8);

        // Пытаемся добавить животных в зоопарк
        System.out.println("Добавлен кролик: " + zoo.addAnimal(rabbit));
        System.out.println("Добавлена обезьяна: " + zoo.addAnimal(monkey));
        System.out.println("Добавлен кролик: " + zoo.addAnimal(rabbit2));
        System.out.println("Добавлен тигр: " + zoo.addAnimal(tiger));
        System.out.println("Добавлен волк: " + zoo.addAnimal(wolf));

        System.out.println();
    }

    private void addThings() {
        System.out.println("\nДОБАВЛЕНИЕ ВЕЩЕЙ");

        zoo.addThing(new Table("Стол1"));
        zoo.addThing(new Computer("Компьютер1"));
        zoo.addThing(new Table("Стол2"));

        System.out.println("Вещи добавлены\n");
    }

    private void demonstrateFunctionality() {
        // 1. Полная инвентаризация
        zoo.printFullInventory();

        System.out.println();

        // 2. Животные для контактного зоопарка
        System.out.println("ЖИВОТНЫЕ ДЛЯ КОНТАКТНОГО ЗООПАРКА");
        var contactAnimals = zoo.getAnimalsForContactZoo();
        if (contactAnimals.isEmpty()) {
            System.out.println("Нет животных для контактного зоопарка");
        } else {
            contactAnimals.forEach(animal ->
                    System.out.println("  - " + animal.getName())
            );
        }

        System.out.println();

        // 3. Общее количество еды
        System.out.println("\nЕДА");
        System.out.println("Всего нужно еды: " + zoo.getFoodForAll() + " кг/день");

        System.out.println();

        // 4. Демонстрация удаления
        System.out.println("\nТЕСТ УДАЛЕНИЯ");
        System.out.println("Удален объект с номером 3: " + zoo.removeByNumber(3));

        System.out.println();

        // 5. Итоговая инвентаризация
        zoo.printFullInventory();
    }
}