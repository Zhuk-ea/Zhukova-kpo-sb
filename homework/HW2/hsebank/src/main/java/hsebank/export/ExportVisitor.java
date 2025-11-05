package hsebank.export;

import hsebank.domain.BankAccount;
import hsebank.domain.Category;
import hsebank.domain.Operation;

/**
 * Посетитель экспорта.
 */
public interface ExportVisitor {

    void visit(BankAccount account);

    void visit(Category category);

    void visit(Operation operation);

    String getResult();

    String getFileExtension();
}