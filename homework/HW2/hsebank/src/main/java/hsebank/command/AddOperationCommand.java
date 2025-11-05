package hsebank.command;

import hsebank.domain.Operation;
import hsebank.domain.OperationType;
import hsebank.facades.OperationFacade;
import hsebank.interfaces.Command;
import java.math.BigDecimal;

/**
 * Команда добавления операции.
 */
public class AddOperationCommand implements Command {
    private final OperationFacade operationFacade;
    private final OperationType type;
    private final Long accountId;
    private final BigDecimal amount;
    private final Long categoryId;
    private final String description;
    private Operation createdOperation;

    /**
     * Конструктор.
     */
    public AddOperationCommand(OperationFacade operationFacade, OperationType type,
                               Long accountId, BigDecimal amount, Long categoryId, String description) {
        this.operationFacade = operationFacade;
        this.type = type;
        this.accountId = accountId;
        this.amount = amount;
        this.categoryId = categoryId;
        this.description = description;
    }

    /**
     * Добавление операции.
     */
    @Override
    public void execute() {
        if (type == OperationType.INCOME) {
            createdOperation = operationFacade.createIncomeOperation(accountId, amount, categoryId, description);
        } else {
            createdOperation = operationFacade.createExpenseOperation(accountId, amount, categoryId, description);
        }
        System.out.println("Добавлена операция: " + type + " на сумму " + amount);
    }

    /**
     * Отменить операцию.
     */
    @Override
    public void undo() {
        if (createdOperation != null) {
            operationFacade.delete(createdOperation.getId());
            System.out.println("Отменена операция: " + type + " на сумму " + amount);
            createdOperation = null;
        }
    }

    /**
     * Получить описание.
     */
    @Override
    public String getDescription() {
        return "Добавление операции: " + type + " (сумма: " + amount + ", описание: " + description + ")";
    }
}