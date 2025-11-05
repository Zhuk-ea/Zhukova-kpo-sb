package hsebank.factory;

import hsebank.domain.Operation;
import hsebank.domain.OperationType;
import java.math.BigDecimal;


/**
 * Фабрика для создания операций.
 */
public class OperationFactory {

    /**
     * Создание операции поступления.
     */
    public Operation createIncomeOperation(Long bankAccountId, BigDecimal amount,
                                           Long categoryId, String description) {
        validateAmount(amount);
        return new Operation(
                OperationType.INCOME,
                bankAccountId,
                amount,
                categoryId,
                description
        );
    }

    /**
     * Создание операции трата.
     */
    public Operation createExpenseOperation(Long bankAccountId, BigDecimal amount,
                                            Long categoryId, String description) {
        validateAmount(amount);
        return new Operation(
                OperationType.EXPENSE,
                bankAccountId,
                amount,
                categoryId,
                description
        );
    }

    /**
     * Проверка суммы операции.
     */
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма операции должна быть положительной");
        }
    }
}