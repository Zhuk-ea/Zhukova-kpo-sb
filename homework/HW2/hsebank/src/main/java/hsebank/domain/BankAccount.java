package hsebank.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Счёт.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    private Long id;
    private String name;
    private BigDecimal balance;
}
