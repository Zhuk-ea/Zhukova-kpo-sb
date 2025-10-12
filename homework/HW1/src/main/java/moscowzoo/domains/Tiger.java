package moscowzoo.domains;

/**
 * Класс для тигров.
 */

public class Tiger extends Predator {

    public Tiger(String name, int food) {
        super(name, food);
    }

    @Override
    public String toString() {
        return "Тигр " + super.toString();
    }
}