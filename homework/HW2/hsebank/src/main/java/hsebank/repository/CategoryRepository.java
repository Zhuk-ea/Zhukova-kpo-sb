package hsebank.repository;

import hsebank.domain.Category;
import hsebank.domain.OperationType;
import hsebank.interfaces.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Репозиторий для категорий.
 */
public class CategoryRepository implements Repository<Category, Long> {
    private final Map<Long, Category> categories = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Функция для сохранения категории.
     */
    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            Long newId = idCounter.getAndIncrement();
            category.setId(newId);
        }
        categories.put(category.getId(), category);
        return category;
    }

    /**
     * Функция поиска операции по id.
     */
    @Override
    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(categories.get(id));
    }

    /**
     * Функция для поиска всех операций.
     */
    @Override
    public List<Category> findAll() {
        return new ArrayList<>(categories.values());
    }

    /**
     * Функция для удаления операции по id.
     */
    @Override
    public void delete(Long id) {
        categories.remove(id);
    }

    /**
     * Функция для проверки существования операции по id.
     */
    @Override
    public boolean existsById(Long id) {
        return categories.containsKey(id);
    }

    /**
     * Функция для поиска операции данного типа.
     */
    public List<Category> findByType(OperationType type) {
        List<Category> result = new ArrayList<>();
        for (Category category : categories.values()) {
            if (category.getType() == type) {
                result.add(category);
            }
        }
        return result;
    }

    /**
     * Функция поиска всех категории по части имени.
     */
    public List<Category> findByNameContaining(String namePattern) {
        List<Category> result = new ArrayList<>();
        for (Category category : categories.values()) {
            if (category.getName().toLowerCase().contains(namePattern.toLowerCase())) {
                result.add(category);
            }
        }
        return result;
    }
}