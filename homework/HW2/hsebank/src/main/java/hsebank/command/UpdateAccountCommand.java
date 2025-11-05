package hsebank.command;

import hsebank.domain.BankAccount;
import hsebank.facades.BankAccountFacade;
import hsebank.interfaces.Command;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Команда обновления счёта.
 */
public class UpdateAccountCommand implements Command {
    private final BankAccountFacade accountFacade;
    private final Long accountId;
    private final String newName;
    private final BigDecimal newBalance;
    private BankAccount originalAccount;

    /**
     * Конструктор.
     */
    public UpdateAccountCommand(BankAccountFacade accountFacade, Long accountId,
                                String newName, BigDecimal newBalance) {
        this.accountFacade = accountFacade;
        this.accountId = accountId;
        this.newName = newName;
        this.newBalance = newBalance;
    }

    /**
     * Обновление счёта.
     */
    @Override
    public void execute() {
        Optional<BankAccount> accountOpt = accountFacade.getById(accountId);
        if (accountOpt.isPresent()) {
            originalAccount = accountOpt.get();
            BankAccount updatedAccount = new BankAccount(accountId, newName, newBalance);
            accountFacade.update(accountId, updatedAccount);
            System.out.println("Обновлен счет: " + newName + " (баланс: " + newBalance + ")");
        }
    }

    /**
     * Отмена.
     */
    @Override
    public void undo() {
        if (originalAccount != null) {
            accountFacade.update(accountId, originalAccount);
            System.out.println("Отменено обновление счета, восстановлено: " + originalAccount.getName());
        }
    }

    /**
     * Описание.
     */
    @Override
    public String getDescription() {
        return "Обновление счета ID: " + accountId + " -> имя: " + newName + ", баланс: " + newBalance;
    }
}