package moscowzoo.interfaces;

/**
 * Интерфейс для инвентаризационных вещей.
 */

public interface Inventory {

    /**
     * Получить инвентарный номер предмета.
     *
     * @return инвентарный номер
     */
    int getNumber();

    /**
     * Установить инвентарный номер предмета.
     *
     * @param number инвентарный номер
     */
    void setNumber(int number);
}