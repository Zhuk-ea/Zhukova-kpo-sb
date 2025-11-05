package hsebank.facades;

import hsebank.domain.Operation;
import hsebank.domain.OperationType;
import hsebank.factory.OperationFactory;
import hsebank.interfaces.Facade;
import hsebank.repository.OperationRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Фасад операций.
 */
public class OperationFacade implements Facade<Operation, Long> {
    private final OperationRepository operationRepository;
    private final OperationFactory operationFactory;
    private final BankAccountFacade accountFacade; // Для обновления баланса счетов

    /**
     * Конструктор.
     */
    public OperationFacade(OperationRepository operationRepository,
                           OperationFactory operationFactory,
                           BankAccountFacade accountFacade) {
        this.operationRepository = operationRepository;
        this.operationFactory = operationFactory;
        this.accountFacade = accountFacade;
    }

    /**
     * Создание операции.
     */
    @Override
    public Operation create(Operation operation) {
        Operation savedOperation = operationRepository.save(operation);
        updateAccountBalance(savedOperation);
        return savedOperation;
    }

    /**
     * Получение операции по id.
     */
    @Override
    public Optional<Operation> getById(Long id) {
        return operationRepository.findById(id);
    }

    /**
     * Получение всех операций.
     */
    @Override
    public List<Operation> getAll() {
        return operationRepository.findAll();
    }

    /**
     * Обновление операции.
     */
    @Override
    public Optional<Operation> update(Long id, Operation entity) {
        if (operationRepository.existsById(id)) {
            Optional<Operation> oldOperation = operationRepository.findById(id);

            entity.setId(id);
            Operation updatedOperation = operationRepository.save(entity);
            updateAccountBalance(updatedOperation);

            return Optional.of(updatedOperation);
        }
        return Optional.empty();
    }

    /**
     * Удаление.
     */
    @Override
    public boolean delete(Long id) {
        Optional<Operation> operationOpt = operationRepository.findById(id);
        if (operationOpt.isPresent()) {
            Operation operation = operationOpt.get();
            operationRepository.delete(id);

            rollbackAccountBalance(operation);
            return true;
        }
        return false;
    }

    /**
     * Проверка существования.
     */
    @Override
    public boolean exists(Long id) {
        return operationRepository.existsById(id);
    }

    /**
     * Создать операцию поступления.
     */
    public Operation createIncomeOperation(Long bankAccountId, BigDecimal amount,
                                           Long categoryId, String description) {
        Operation operation = operationFactory.createIncomeOperation(
                bankAccountId, amount, categoryId, description
        );
        Operation savedOperation = operationRepository.save(operation);
        updateAccountBalance(savedOperation);
        return savedOperation;
    }

    /**
     * Создать операцию траты.
     */
    public Operation createExpenseOperation(Long bankAccountId, BigDecimal amount,
                                            Long categoryId, String description) {
        Operation operation = operationFactory.createExpenseOperation(
                bankAccountId, amount, categoryId, description
        );
        Operation savedOperation = operationRepository.save(operation);
        updateAccountBalance(savedOperation);
        return savedOperation;
    }

    /**
     * Получить операции по id счёта.
     */
    public List<Operation> getOperationsByBankAccount(Long bankAccountId) {
        return operationRepository.findByBankAccountId(bankAccountId);
    }

    /**
     * Получить операции по id категории.
     */
    public List<Operation> getOperationsByCategory(Long categoryId) {
        return operationRepository.findByCategoryId(categoryId);
    }

    /**
     * Получить операцию по типу.
     */
    public List<Operation> getOperationsByType(OperationType type) {
        return operationRepository.findByType(type);
    }

    /**
     * Получить все операции поступления.
     */
    public BigDecimal getTotalIncome() {
        return operationRepository.findByType(OperationType.INCOME).stream()
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Получить операции трат.
     */
    public BigDecimal getTotalExpenses() {
        return operationRepository.findByType(OperationType.EXPENSE).stream()
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Получить баланс.
     */
    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpenses());
    }

    /**
     * Обновить баланс счёта.
     */
    private void updateAccountBalance(Operation operation) {
        Optional<hsebank.domain.BankAccount> accountOpt = accountFacade.getById(operation.getBankAccountId());
        if (accountOpt.isPresent()) {
            hsebank.domain.BankAccount account = accountOpt.get();
            BigDecimal newBalance = calculateNewBalance(account, operation);
            account.setBalance(newBalance);
            accountFacade.update(account.getId(), account);
        }
    }

    /**
     * Отменить пересчёт баланса.
     */
    private void rollbackAccountBalance(Operation operation) {
        Optional<hsebank.domain.BankAccount> accountOpt = accountFacade.getById(operation.getBankAccountId());
        if (accountOpt.isPresent()) {
            hsebank.domain.BankAccount account = accountOpt.get();
            BigDecimal newBalance = calculateRollbackBalance(account, operation);
            account.setBalance(newBalance);
            accountFacade.update(account.getId(), account);
        }
    }

    /**
     * Расчитать новый баланс.
     */
    private BigDecimal calculateNewBalance(hsebank.domain.BankAccount account, Operation operation) {
        if (operation.getType() == OperationType.INCOME) {
            return account.getBalance().add(operation.getAmount());
        } else {
            return account.getBalance().subtract(operation.getAmount());
        }
    }

    /**
     * Рассчитать старый баланс.
     */
    private BigDecimal calculateRollbackBalance(hsebank.domain.BankAccount account, Operation operation) {
        if (operation.getType() == OperationType.INCOME) {
            return account.getBalance().subtract(operation.getAmount());
        } else {
            return account.getBalance().add(operation.getAmount());
        }
    }
}