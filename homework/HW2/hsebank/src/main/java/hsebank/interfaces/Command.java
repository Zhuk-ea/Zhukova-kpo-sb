package hsebank.interfaces;

/**
 * Интерфейс Команды.
 */
public interface Command {

    void execute();

    void undo();

    String getDescription();

}