package hsebank.facades;

import hsebank.domain.Category;
import hsebank.domain.OperationType;
import hsebank.factory.CategoryFactory;
import hsebank.interfaces.Facade;
import hsebank.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;

/**
 * Фасад категорий.
 */
public class CategoryFacade implements Facade<Category, Long> {
    private final CategoryRepository categoryRepository;
    private final CategoryFactory categoryFactory;

    /**
     * Конструктор.
     */
    public CategoryFacade(CategoryRepository categoryRepository,
                          CategoryFactory categoryFactory) {
        this.categoryRepository = categoryRepository;
        this.categoryFactory = categoryFactory;
    }

    /**
     * Создание категории.
     */
    @Override
    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Колучение категории по id.
     */
    @Override
    public Optional<Category> getById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Получение всех категорий.
     */
    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    /**
     * Обновление категори.
     */
    @Override
    public Optional<Category> update(Long id, Category entity) {
        if (categoryRepository.existsById(id)) {
            entity.setId(id);
            return Optional.of(categoryRepository.save(entity));
        }
        return Optional.empty();
    }

    /**
     * Удаление.
     */
    @Override
    public boolean delete(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.delete(id);
            return true;
        }
        return false;
    }

    /**
     * Проверка существования.
     */
    @Override
    public boolean exists(Long id) {
        return categoryRepository.existsById(id);
    }

    /**
     * Создание категории поступлений.
     */
    public Category createIncomeCategory(String name) {
        Category category = categoryFactory.createIncomeCategory(name);
        return categoryRepository.save(category);
    }

    /**
     * Создание категории трат.
     */
    public Category createExpenseCategory(String name) {
        Category category = categoryFactory.createExpenseCategory(name);
        return categoryRepository.save(category);
    }

    /**
     * Получить категории данного типа.
     */
    public List<Category> getCategoriesByType(OperationType type) {
        return categoryRepository.findByType(type);
    }

    /**
     * Найти категории по имени.
     */
    public List<Category> findCategoriesByName(String namePattern) {
        return categoryRepository.findByNameContaining(namePattern);
    }
}