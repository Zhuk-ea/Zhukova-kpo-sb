package hsebank.decorator;

import hsebank.interfaces.Command;

/**
 * Декоратор логирования.
 */
public class LoggingDecorator implements Command {
    private final Command decoratedCommand;

    /**
     * Конструктор.
     */
    public LoggingDecorator(Command decoratedCommand) {
        this.decoratedCommand = decoratedCommand;
    }

    /**
     * Выполнение декоратора.
     */
    @Override
    public void execute() {
        System.out.println("Начало выполнения: " + decoratedCommand.getDescription());
        decoratedCommand.execute();
        System.out.println("Завершение выполнения: " + decoratedCommand.getDescription());
    }

    /**
     * Отмена.
     */
    @Override
    public void undo() {
        System.out.println("Начало отмены: " + decoratedCommand.getDescription());
        decoratedCommand.undo();
        System.out.println("Завершение отмены: " + decoratedCommand.getDescription());
    }

    /**
     * Описание.
     */
    @Override
    public String getDescription() {
        return decoratedCommand.getDescription() + " (с логированием)";
    }
}