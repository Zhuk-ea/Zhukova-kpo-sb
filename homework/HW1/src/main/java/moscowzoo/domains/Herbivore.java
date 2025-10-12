package moscowzoo.domains;

import lombok.Getter;
import lombok.Setter;


/**
 * Класс для травоядных животных.
 * Имеет дополнительное свойство - уровень доброты.
 */
@Getter
@Setter
public abstract class Herbivore extends Animal {

    private int kindness;

    /**
     * Конструктор для создания травоядного животного.
     *
     * @param name имя животного
     * @param food количество еды в кг/сутки
     * @param kindness уровень доброты (0-10)
     */
    public Herbivore(String name, int food, int kindness) {
        super(name, food);  // Вызов конструктора родителя
        this.kindness = Math.min(kindness, 10);
    }

    /**
     * Проверяет, можно ли животное в контактный зоопарк.
     * Для травоядных: доброта > 5 и животное здорово.
     *
     * @return true, если можно в контактный зоопарк
     */
    @Override
    public boolean canGoToContactZoo() {
        return isHealthy() && (kindness > 5);
    }

    @Override
    public String toString() {
        return String.format("\"%s\" [Травоядное, Доброта: %d/10] " + super.toString(),
                getName(), kindness);
    }
}

