package hsebank.facades;

import hsebank.domain.BankAccount;
import hsebank.factory.BankAccountFactory;
import hsebank.interfaces.Facade;
import hsebank.repository.BankAccountRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Фасад счетов.
 */
public class BankAccountFacade implements Facade<BankAccount, Long> {
    private final BankAccountRepository accountRepository;
    private final BankAccountFactory accountFactory;

    /**
     * Конструктор.
     */
    public BankAccountFacade(BankAccountRepository accountRepository,
                             BankAccountFactory accountFactory) {
        this.accountRepository = accountRepository;
        this.accountFactory = accountFactory;
    }

    /**
     * Создание.
     */
    @Override
    public BankAccount create(BankAccount account) {
        return accountRepository.save(account);
    }

    /**
     * Получение счёта по id.
     */
    @Override
    public Optional<BankAccount> getById(Long id) {
        return accountRepository.findById(id);
    }

    /**
     * Получение всех счетов.
     */
    @Override
    public List<BankAccount> getAll() {
        return accountRepository.findAll();
    }

    /**
     * Изменение счёта.
     */
    @Override
    public Optional<BankAccount> update(Long id, BankAccount entity) {
        if (accountRepository.existsById(id)) {
            entity.setId(id);
            return Optional.of(accountRepository.save(entity));
        }
        return Optional.empty();
    }

    /**
     * Удаление счёта.
     */
    @Override
    public boolean delete(Long id) {
        if (accountRepository.existsById(id)) {
            accountRepository.delete(id);
            return true;
        }
        return false;
    }

    /**
     * Проверка существования.
     */
    @Override
    public boolean exists(Long id) {
        return accountRepository.existsById(id);
    }

    /**
     * Создание счёта через фабрику.
     */
    public BankAccount createAccount(String name, BigDecimal initialBalance) {
        BankAccount account = accountFactory.createAccount(name, initialBalance);
        return accountRepository.save(account);
    }

    /**
     * Создание счёта через фабрику.
     */
    public BankAccount createAccount(String name) {
        return createAccount(name, BigDecimal.ZERO);
    }

    /**
     * Обновление аккаунта по имени.
     */
    public Optional<BankAccount> updateAccountName(Long id, String newName) {
        Optional<BankAccount> accountOpt = accountRepository.findById(id);
        if (accountOpt.isPresent()) {
            BankAccount account = accountOpt.get();
            account.setName(newName);
            return Optional.of(accountRepository.save(account));
        }
        return Optional.empty();
    }

    /**
     * Пополнение счёта.
     */
    public Optional<BankAccount> deposit(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной");
        }

        Optional<BankAccount> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isPresent()) {
            BankAccount account = accountOpt.get();
            BigDecimal newBalance = account.getBalance().add(amount);
            account.setBalance(newBalance);
            return Optional.of(accountRepository.save(account));
        }
        return Optional.empty();
    }

    /**
     * Найти счета по имени.
     */
    public List<BankAccount> findAccountsByName(String namePattern) {
        return accountRepository.findByNameContaining(namePattern);
    }

    /**
     * Получить баланс.
     */
    public BigDecimal getTotalBalance() {
        return accountRepository.findAll().stream()
                .map(BankAccount::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}