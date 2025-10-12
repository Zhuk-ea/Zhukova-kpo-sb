package moscowzoo.domains;

import lombok.Getter;
import lombok.Setter;
import moscowzoo.interfaces.Alive;
import moscowzoo.interfaces.Inventory;

/**
 * Абстрактный базовый класс для всех животных в зоопарке.
 * Реализует интерфейсы Alive и Inventory.
 */
@Getter
@Setter
public abstract class Animal implements Alive, Inventory {

    private String name;
    private int food;
    private int number;
    private boolean healthy;

    /**
     * Конструктор для создания животного.
     *
     * @param name имя животного
     * @param food количество еды в кг/сутки
     */
    protected Animal(String name, int food) {
        this.name = name;
        this.food = food;
        this.healthy = true; // По умолчанию животное считается здоровым
    }

    /**
     * Получить информацию о том, может ли животное быть в контактном зоопарке.
     *
     * @return true, если животное можно поместить в контактный зоопарк
     */
    public abstract boolean canGoToContactZoo();

    @Override
    public String toString() {
        return String.format("(№%d) - Еда: %dкг/день, Здоров: %s, Контактный: %s",
                number, food, healthy ? "да" : "нет", canGoToContactZoo() ? "да" : "нет");
    }
}