package hsebank.decorator;

import hsebank.interfaces.Command;

/**
 * Декоратор времени выполнения.
 */
public class TimingDecorator implements Command {
    private final Command decoratedCommand;

    public TimingDecorator(Command decoratedCommand) {
        this.decoratedCommand = decoratedCommand;
    }

    /**
     * Выполнения подсчёта времени.
     */
    @Override
    public void execute() {
        long startTime = System.currentTimeMillis();
        decoratedCommand.execute();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Команда '" + decoratedCommand.getDescription()
               + "' выполнена за " + duration + " мс");
    }

    /**
     * Отмена.
     */
    @Override
    public void undo() {
        long startTime = System.currentTimeMillis();
        decoratedCommand.undo();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Отмена команды '" + decoratedCommand.getDescription()
                + "' выполнена за " + duration + " мс");
    }

    /**
     * Описание.
     */
    @Override
    public String getDescription() {
        return decoratedCommand.getDescription() + " (с таймингом)";
    }
}