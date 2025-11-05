package hsebank.command;

import hsebank.domain.BankAccount;
import hsebank.facades.BankAccountFacade;
import hsebank.interfaces.Command;
import java.math.BigDecimal;

/**
 * Команда создания счёта.
 */
public class CreateAccountCommand implements Command {
    private final BankAccountFacade accountFacade;
    private final String accountName;
    private final BigDecimal initialBalance;
    private BankAccount createdAccount;

    /**
     * Конструктор.
     */
    public CreateAccountCommand(BankAccountFacade accountFacade,
                                String accountName, BigDecimal initialBalance) {
        this.accountFacade = accountFacade;
        this.accountName = accountName;
        this.initialBalance = initialBalance;
    }

    /**
     * Создание счёта.
     */
    @Override
    public void execute() {
        createdAccount = accountFacade.createAccount(accountName, initialBalance);
        System.out.println("Создан счет: " + accountName + " с балансом: " + initialBalance);
    }

    /**
     * Отмена.
     */
    @Override
    public void undo() {
        if (createdAccount != null) {
            accountFacade.delete(createdAccount.getId());
            System.out.println("Отменено создание счета: " + accountName);
            createdAccount = null;
        }
    }

    /**
     * Получить описание.
     */
    @Override
    public String getDescription() {
        return "Создание счета: " + accountName + " (баланс: " + initialBalance + ")";
    }
}