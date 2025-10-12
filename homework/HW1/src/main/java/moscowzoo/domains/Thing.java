package moscowzoo.domains;

import lombok.Getter;
import lombok.Setter;
import moscowzoo.interfaces.Inventory;

/**
 * Базовый класс для всех вещей в зоопарке.
 * Реализует интерфейс Inventory.
 */
@Getter
@Setter
public class Thing implements Inventory {

    private String name;
    private int number;

    /**
     * Конструктор для создания вещи.
     *
     * @param name название вещи
     */
    public Thing(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s (№%d)", name, number);
    }
}