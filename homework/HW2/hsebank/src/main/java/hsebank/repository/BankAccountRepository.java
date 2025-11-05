package hsebank.repository;

import hsebank.domain.BankAccount;
import hsebank.interfaces.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Репозиторий для счетов.
 */
public class BankAccountRepository implements Repository<BankAccount, Long> {
    private final Map<Long, BankAccount> accounts = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Функция для сохранения счёта.
     */
    @Override
    public BankAccount save(BankAccount account) {
        if (account.getId() == null) {
            Long newId = idCounter.getAndIncrement();
            account.setId(newId);
        }
        accounts.put(account.getId(), account);
        return account;
    }

    /**
     * Функция для поиска счёта по id.
     */
    @Override
    public Optional<BankAccount> findById(Long id) {
        return Optional.ofNullable(accounts.get(id));
    }

    /**
     * Функция для получения всех счетов.
     */
    @Override
    public List<BankAccount> findAll() {
        return new ArrayList<>(accounts.values());
    }

    /**
     * Функция для удаления аккаунта по id.
     */
    @Override
    public void delete(Long id) {
        accounts.remove(id);
    }

    /**
     * Функция для проверки существования аккаунта по id.
     */
    @Override
    public boolean existsById(Long id) {
        return accounts.containsKey(id);
    }

    /**
     * Функция для поиска счетов по части названия.
     */
    public List<BankAccount> findByNameContaining(String namePattern) {
        List<BankAccount> result = new ArrayList<>();
        for (BankAccount account : accounts.values()) {
            if (account.getName().toLowerCase().contains(namePattern.toLowerCase())) {
                result.add(account);
            }
        }
        return result;
    }
}