package moscowzoo.domains;

/**
 * Класс для обезьян.
 */

public class Monkey extends Herbivore {

    public Monkey(String name, int food, int kindness) {
        super(name, food, kindness);
    }

    @Override
    public String toString() {
        return "Обезьяна " + super.toString();
    }
}