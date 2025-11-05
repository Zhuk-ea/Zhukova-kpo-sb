package hsebank.factory;

import hsebank.domain.Category;
import hsebank.domain.OperationType;

/**
 * Фабрика для создания категорий.
 */
public class CategoryFactory {

    /**
     * Создание категории поступления.
     */
    public Category createIncomeCategory(String name) {
        validateName(name);
        return new Category(OperationType.INCOME, name);
    }

    /**
     * Создание категории траты.
     */
    public Category createExpenseCategory(String name) {
        validateName(name);
        return new Category(OperationType.EXPENSE, name);
    }

    /**
     * Проверка имени категории.
     */
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название категории не может быть пустым");
        }
    }
}