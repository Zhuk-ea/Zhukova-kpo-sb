package hsebank.command;

import hsebank.domain.Operation;
import hsebank.facades.OperationFacade;
import hsebank.interfaces.Command;
import java.util.Optional;

/**
 * Команда удаления операции.
 */
public class DeleteOperationCommand implements Command {
    private final OperationFacade operationFacade;
    private final Long operationId;
    private Operation deletedOperation;

    /**
     * Конструктор.
     */
    public DeleteOperationCommand(OperationFacade operationFacade, Long operationId) {
        this.operationFacade = operationFacade;
        this.operationId = operationId;
    }

    /**
     * Удаление операции.
     */
    @Override
    public void execute() {
        Optional<Operation> operationOpt = operationFacade.getById(operationId);
        if (operationOpt.isPresent()) {
            deletedOperation = operationOpt.get();
            operationFacade.delete(operationId);
            System.out.println("Удалена операция ID: " + operationId
                    + " (сумма: " + deletedOperation.getAmount() + ")");
        }
    }

    /**
     * Отмена.
     */
    @Override
    public void undo() {
        if (deletedOperation != null) {
            operationFacade.create(deletedOperation);
            System.out.println("Восстановлена операция ID: " + operationId);
            deletedOperation = null;
        }
    }

    /**
     * Описание.
     */
    @Override
    public String getDescription() {
        return "Удаление операции ID: " + operationId;
    }
}