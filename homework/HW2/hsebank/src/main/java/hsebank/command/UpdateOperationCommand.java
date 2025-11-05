package hsebank.command;

import hsebank.domain.Operation;
import hsebank.domain.OperationType;
import hsebank.facades.OperationFacade;
import hsebank.interfaces.Command;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Команда обновления операции.
 */
public class UpdateOperationCommand implements Command {
    private final OperationFacade operationFacade;
    private final Long operationId;
    private final OperationType newType;
    private final BigDecimal newAmount;
    private final String newDescription;
    private Operation originalOperation;

    /**
     * Конструктор.
     */
    public UpdateOperationCommand(OperationFacade operationFacade, Long operationId,
                                  OperationType newType, BigDecimal newAmount, String newDescription) {
        this.operationFacade = operationFacade;
        this.operationId = operationId;
        this.newType = newType;
        this.newAmount = newAmount;
        this.newDescription = newDescription;
    }

    /**
     * Обновление операции.
     */
    @Override
    public void execute() {
        Optional<Operation> operationOpt = operationFacade.getById(operationId);
        if (operationOpt.isPresent()) {
            originalOperation = operationOpt.get();
            Operation updatedOperation = new Operation(operationId, newType,
                    originalOperation.getBankAccountId(), newAmount,
                    originalOperation.getDate(), newDescription,
                    originalOperation.getCategoryId());
            operationFacade.update(operationId, updatedOperation);
            System.out.println("Обновлена операция: " + newType + " на сумму " + newAmount);
        }
    }

    /**
     * Отмена.
     */
    @Override
    public void undo() {
        if (originalOperation != null) {
            operationFacade.update(operationId, originalOperation);
            System.out.println("Отменено обновление операции, восстановлена сумма: " + originalOperation.getAmount());
        }
    }

    /**
     * Описание.
     */
    @Override
    public String getDescription() {
        return "Обновление операции ID: " + operationId + " -> тип: " + newType + ", сумма: " + newAmount;
    }
}