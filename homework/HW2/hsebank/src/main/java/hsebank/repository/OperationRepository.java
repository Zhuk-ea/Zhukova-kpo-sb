package hsebank.repository;

import hsebank.domain.Operation;
import hsebank.domain.OperationType;
import hsebank.interfaces.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Репозиторий для операций.
 */
public class OperationRepository implements Repository<Operation, Long> {
    private final Map<Long, Operation> operations = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Функция для сохранения операции.
     */
    @Override
    public Operation save(Operation operation) {
        if (operation.getId() == null) {
            Long newId = idCounter.getAndIncrement();
            operation.setId(newId);
        }
        operations.put(operation.getId(), operation);
        return operation;
    }

    /**
     * Функция для нахождения операции по id.
     */
    @Override
    public Optional<Operation> findById(Long id) {
        return Optional.ofNullable(operations.get(id));
    }

    /**
     * Функция для нахождения всех операций.
     */
    @Override
    public List<Operation> findAll() {
        return new ArrayList<>(operations.values());
    }

    /**
     * Функция для удаления операции по id.
     */
    @Override
    public void delete(Long id) {
        operations.remove(id);
    }

    /**
     * Функция для проверки существования операции по id.
     */
    @Override
    public boolean existsById(Long id) {
        return operations.containsKey(id);
    }

    /**
     * Функция для поиска всех операций с банковским аккаунтом по его id.
     */
    public List<Operation> findByBankAccountId(Long bankAccountId) {
        List<Operation> result = new ArrayList<>();
        for (Operation operation : operations.values()) {
            if (operation.getBankAccountId().equals(bankAccountId)) {
                result.add(operation);
            }
        }
        return result;
    }

    /**
     * Функция для поиска всех операций определённой категорией по id категории.
     */
    public List<Operation> findByCategoryId(Long categoryId) {
        List<Operation> result = new ArrayList<>();
        for (Operation operation : operations.values()) {
            if (operation.getCategoryId().equals(categoryId)) {
                result.add(operation);
            }
        }
        return result;
    }

    /**
     * Функция для поиска всех операций данного типа.
     */
    public List<Operation> findByType(OperationType type) {
        List<Operation> result = new ArrayList<>();
        for (Operation operation : operations.values()) {
            if (operation.getType() == type) {
                result.add(operation);
            }
        }
        return result;
    }
}