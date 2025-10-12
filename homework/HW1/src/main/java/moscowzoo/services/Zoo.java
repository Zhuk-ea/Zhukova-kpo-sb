package moscowzoo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import moscowzoo.domains.Animal;
import moscowzoo.domains.Thing;
import org.springframework.stereotype.Component;

/**
 * Основной класс зоопарка для управления животными и вещами.
 */
@Component
@RequiredArgsConstructor
public class Zoo {

    private final VeterinaryClinic veterinaryClinic;

    private final List<Animal> animals = new ArrayList<>();
    private final List<Thing> things = new ArrayList<>();
    private final AtomicInteger inventoryCounter = new AtomicInteger(1);

    /**
     * Попытаться поставить животное на содержание.
     * Проверяет здоровье через вет.клинику.
     *
     * @param animal животное для добавления
     * @return true, если животное принято в зоопарк
     */
    public boolean addAnimal(Animal animal) {
        if (veterinaryClinic.checkHealth(animal)) {
            animal.setNumber(inventoryCounter.getAndIncrement());
            animals.add(animal);
            return true;
        }
        return false;
    }

    /**
     * Добавить вещь в зоопарк.
     *
     * @param thing вещь для добавления
     */
    public void addThing(Thing thing) {
        thing.setNumber(inventoryCounter.getAndIncrement());
        things.add(thing);
    }

    /**
     * Получить общее количество еды, необходимое в сутки.
     *
     * @return количество еды в кг/сутки
     */
    public int getFoodForAll() {
        return animals.stream()
                .mapToInt(Animal::getFood)
                .sum();
    }

    /**
     * Получить животных для контактного зоопарка.
     *
     * @return список животных, которые могут быть в контактном зоопарке
     */
    public List<Animal> getAnimalsForContactZoo() {
        return animals.stream()
                .filter(Animal::canGoToContactZoo)
                .toList();
    }

    /**
     * Удалить объект по инвентарному номеру.
     *
     * @param number инвентарный номер
     * @return true, если объект был найден и удален
     */
    public boolean removeByNumber(int number) {
        boolean animalRemoved = animals.removeIf(animal -> animal.getNumber() == number);
        boolean thingRemoved = things.removeIf(thing -> thing.getNumber() == number);
        return animalRemoved || thingRemoved;
    }

    /**
     * Вывести полную инвентаризацию (животные + вещи).
     */
    public void printFullInventory() {
        System.out.println("ПОЛНАЯ ИНВЕНТАРИЗАЦИЯ ЗООПАРКА");
        printAnimalsInventory();
        printThingsInventory();
        System.out.println("\n");
    }

    /**
     * Вывести инвентаризацию только животных.
     */
    public void printAnimalsInventory() {
        System.out.println("\n- ЖИВОТНЫЕ:");
        if (animals.isEmpty()) {
            System.out.println("Животных нет");
        } else {
            animals.forEach(animal ->
                    System.out.println("  " + animal)
            );
        }
        System.out.println("Всего животных: " + animals.size());
        System.out.println("Необходимо еды: " + getFoodForAll() + " кг/день");
    }

    /**
     * Вывести инвентаризацию только вещей.
     */
    public void printThingsInventory() {
        System.out.println("\n- ВЕЩИ:");
        if (things.isEmpty()) {
            System.out.println("Вещей нет");
        } else {
            things.forEach(thing ->
                    System.out.println("  " + thing)
            );
        }
        System.out.println("Всего вещей: " + things.size());
    }

    /**
     * Получить список всех животных (только для чтения).
     *
     * @return список животных
     */
    public List<Animal> getAnimals() {
        return List.copyOf(animals);
    }

    /**
     * Получить список всех вещей (только для чтения).
     *
     * @return список вещей
     */
    public List<Thing> getThings() {
        return List.copyOf(things);
    }

}