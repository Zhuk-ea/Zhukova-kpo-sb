package moscowzoo.domains;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс для хищных животных.
 */

@Getter
@Setter
public abstract class Predator extends Animal {

    /**
     * Конструктор для создания хищного животного.
     *
     * @param name имя животного
     * @param food количество еды в кг/сутки
     */
    public Predator(String name, int food) {
        super(name, food);  // Вызов конструктора родителя
    }

    /**
     * Проверяет, можно ли животное в контактный зоопарк.
     * Хищникам туда нельзя поэтому в любом случае
     *
     * @return false
     */
    @Override
    public boolean canGoToContactZoo() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("\"%s\" [Хищник] "  + super.toString(),
                getName());
    }
}

