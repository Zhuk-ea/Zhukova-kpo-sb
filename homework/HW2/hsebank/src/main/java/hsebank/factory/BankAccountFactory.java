package hsebank.factory;

import hsebank.domain.BankAccount;
import java.math.BigDecimal;


/**
 * Фабрика для создания банковских аккаунтов.
 */
public class BankAccountFactory {

    /**
     * Создание аккаунта с начальным балансом.
     */
    public BankAccount createAccount(String name, BigDecimal initialBalance) {
        validateName(name);
        validateBalance(initialBalance);

        return new BankAccount(null, name, initialBalance);
    }

    /**
     * Создание аккаунта только с именем.
     */
    public BankAccount createAccount(String name) {
        return createAccount(name, BigDecimal.ZERO);
    }

    /**
     * Проверка имени счёта.
     */
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название счета не может быть пустым");
        }
    }

    /**
     * Создание аккаунта только с именем.
     */
    private void validateBalance(BigDecimal balance) {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Баланс не может быть отрицательным");
        }
    }
}