package moscowzoo.domains;

/**
 * Класс для кроликов.
 */

public class Rabbit extends Herbivore {

    public Rabbit(String name, int food, int kindness) {
        super(name, food, kindness);
    }

    @Override
    public String toString() {
        return "Кролик " + super.toString();
    }
}