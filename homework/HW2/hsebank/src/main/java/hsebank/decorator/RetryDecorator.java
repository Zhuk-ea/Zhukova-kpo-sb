package hsebank.decorator;

import hsebank.interfaces.Command;

/**
 * Декоратор повторения.
 */
public class RetryDecorator implements Command {
    private final Command decoratedCommand;
    private final int maxRetries;

    /**
     * Конструктор.
     */
    public RetryDecorator(Command decoratedCommand, int maxRetries) {
        this.decoratedCommand = decoratedCommand;
        this.maxRetries = maxRetries;
    }

    /**
     * Повторение.
     */
    @Override
    public void execute() {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                System.out.println("Попытка " + attempt + " из " + maxRetries);
                decoratedCommand.execute();
                return;
            } catch (Exception e) {
                System.out.println("Ошибка при выполнении: " + e.getMessage());
                if (attempt == maxRetries) {
                    throw new RuntimeException("Команда не выполнена после " + maxRetries + " попыток", e);
                }
            }
        }
    }

    /**
     * Отмена.
     */
    @Override
    public void undo() {
        decoratedCommand.undo();
    }

    /**
     * Описание.
     */
    @Override
    public String getDescription() {
        return decoratedCommand.getDescription() + " (с повторением)";
    }
}