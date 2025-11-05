package hsebank.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Операция.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operation {
    private Long id;
    private OperationType type;
    private Long bankAccountId;
    private BigDecimal amount;
    private LocalDateTime date;
    private String description;
    private Long categoryId;

    /**
     * Конструктор.
     */
    public Operation(OperationType type, Long bankAccountId, BigDecimal amount,
                     Long categoryId, String description) {
        this.type = type;
        this.bankAccountId = bankAccountId;
        this.amount = amount;
        this.categoryId = categoryId;
        this.description = description;
        this.date = LocalDateTime.now();
    }
}