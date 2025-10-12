package moscowzoo.domains;

/**
 * Класс для волков.
 */

public class Wolf extends Predator {

    public Wolf(String name, int food) {
        super(name, food);
    }

    @Override
    public String toString() {
        return "Волк " + super.toString();
    }
}