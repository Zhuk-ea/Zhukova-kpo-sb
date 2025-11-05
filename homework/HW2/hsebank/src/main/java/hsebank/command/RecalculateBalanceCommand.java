package hsebank.command;

import hsebank.domain.BankAccount;
import hsebank.domain.Operation;
import hsebank.domain.OperationType;
import hsebank.facades.BankAccountFacade;
import hsebank.facades.OperationFacade;
import hsebank.interfaces.Command;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Команда перерасчёта баланса.
 */
public class RecalculateBalanceCommand implements Command {
    private final BankAccountFacade accountFacade;
    private final OperationFacade operationFacade;
    private final Long accountId;
    private BigDecimal previousBalance;

    /**
     * Конструктор.
     */
    public RecalculateBalanceCommand(BankAccountFacade accountFacade,
                                     OperationFacade operationFacade,
                                     Long accountId) {
        this.accountFacade = accountFacade;
        this.operationFacade = operationFacade;
        this.accountId = accountId;
    }

    /**
     * Перерасчёт баланса счёта по id.
     */
    @Override
    public void execute() {
        Optional<BankAccount> accountOpt = accountFacade.getById(accountId);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Счет с ID " + accountId + " не найден");
        }

        BankAccount account = accountOpt.get();
        previousBalance = account.getBalance();

        BigDecimal correctBalance = calculateBalanceFromOperations();

        account.setBalance(correctBalance);
        accountFacade.update(accountId, account);
    }


    /**
     * Команда отмены.
     */
    @Override
    public void undo() {
        if (previousBalance != null) {
            Optional<BankAccount> accountOpt = accountFacade.getById(accountId);
            if (accountOpt.isPresent()) {
                BankAccount account = accountOpt.get();
                account.setBalance(previousBalance);
                accountFacade.update(accountId, account);
            }
        }
    }

    /**
     * Описание.
     */
    @Override
    public String getDescription() {
        return "Пересчет баланса для счета ID: " + accountId;
    }

    /**
     * Вычисление корректного баланса на основе всех операций по счету.
     */
    private BigDecimal calculateBalanceFromOperations() {
        List<Operation> operations = operationFacade.getOperationsByBankAccount(accountId);

        BigDecimal balance = BigDecimal.ZERO;
        for (Operation operation : operations) {
            if (operation.getType() == OperationType.INCOME) {
                balance = balance.add(operation.getAmount());
            } else {
                balance = balance.subtract(operation.getAmount());
            }
        }

        return balance;
    }

    /**
     * Проверка наличия расхождений между текущим и расчетным балансом.
     */
    public boolean hasDiscrepancy() {
        Optional<BankAccount> accountOpt = accountFacade.getById(accountId);
        if (accountOpt.isEmpty()) {
            return false;
        }

        BigDecimal currentBalance = accountOpt.get().getBalance();
        BigDecimal calculatedBalance = calculateBalanceFromOperations();

        return !currentBalance.equals(calculatedBalance);
    }

    /**
     * Возвращает величину расхождения.
     */
    public BigDecimal getDiscrepancyAmount() {
        Optional<BankAccount> accountOpt = accountFacade.getById(accountId);
        if (accountOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal currentBalance = accountOpt.get().getBalance();
        BigDecimal calculatedBalance = calculateBalanceFromOperations();

        return currentBalance.subtract(calculatedBalance);
    }
}