package hsebank.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Категория.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Long id;
    private OperationType type;
    private String name;

    /**
     * Конструктор.
     */
    public Category(OperationType type, String name) {
        this.type = type;
        this.name = name;
    }
}