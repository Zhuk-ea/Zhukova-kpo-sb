package hsebank.command;

import hsebank.domain.BankAccount;
import hsebank.facades.BankAccountFacade;
import hsebank.interfaces.Command;
import java.util.Optional;

/**
 * Команда удаления счёта.
 */
public class DeleteAccountCommand implements Command {
    private final BankAccountFacade accountFacade;
    private final Long accountId;
    private BankAccount deletedAccount;

    /**
     * Конструктор.
     */
    public DeleteAccountCommand(BankAccountFacade accountFacade, Long accountId) {
        this.accountFacade = accountFacade;
        this.accountId = accountId;
    }

    /**
     * Удаление счёта.
     */
    @Override
    public void execute() {
        Optional<BankAccount> accountOpt = accountFacade.getById(accountId);
        if (accountOpt.isPresent()) {
            deletedAccount = accountOpt.get();
            accountFacade.delete(accountId);
            System.out.println("Удален счет: " + deletedAccount.getName());
        }
    }

    /**
     * Отмена.
     */
    @Override
    public void undo() {
        if (deletedAccount != null) {
            accountFacade.create(deletedAccount);
            System.out.println("Восстановлен счет: " + deletedAccount.getName());
            deletedAccount = null;
        }
    }

    /**
     * Описание.
     */
    @Override
    public String getDescription() {
        return "Удаление счета ID: " + accountId;
    }
}