package hsebank.export;

import hsebank.domain.BankAccount;
import hsebank.domain.Category;
import hsebank.domain.Operation;
import hsebank.domain.OperationType;
import java.util.ArrayList;
import java.util.List;

/**
 * Посетитель YamlExport.
 */
public class YamlExportVisitor implements ExportVisitor {
    private final List<String> accountsYaml = new ArrayList<>();
    private final List<String> categoriesYaml = new ArrayList<>();
    private final List<String> operationsYaml = new ArrayList<>();

    /**
     * Посетить счёт.
     */
    @Override
    public void visit(BankAccount account) {
        String yaml = String.format(
                "  - id: %d\n    name: \"%s\"\n    balance: %s",
                account.getId(), account.getName(), account.getBalance()
        );
        accountsYaml.add(yaml);
    }

    /**
     * Посетить категорию.
     */
    @Override
    public void visit(Category category) {
        String yaml = String.format(
                "  - id: %d\n    type: \"%s\"\n    name: \"%s\"",
                category.getId(), category.getType(), category.getName()
        );
        categoriesYaml.add(yaml);
    }

    /**
     * Посетить операцию.
     */
    @Override
    public void visit(Operation operation) {
        String yaml = String.format(
                "  - id: %d\n    type: \"%s\"\n    accountId: %d\n    amount: %s\n"
                        + "    date: \"%s\"\n    description: \"%s\"\n    categoryId: %d",
                operation.getId(), operation.getType(), operation.getBankAccountId(),
                operation.getAmount(), operation.getDate(),
                operation.getDescription() != null ? operation.getDescription() : "",
                operation.getCategoryId()
        );
        operationsYaml.add(yaml);
    }

    /**
     * Экспорт.
     */
    @Override
    public String getResult() {
        StringBuilder yaml = new StringBuilder();

        if (!accountsYaml.isEmpty()) {
            yaml.append("accounts:\n");
            accountsYaml.forEach(account -> yaml.append(account).append("\n"));
            yaml.append("\n");
        }

        if (!categoriesYaml.isEmpty()) {
            yaml.append("categories:\n");
            categoriesYaml.forEach(category -> yaml.append(category).append("\n"));
            yaml.append("\n");
        }

        if (!operationsYaml.isEmpty()) {
            yaml.append("operations:\n");
            operationsYaml.forEach(operation -> yaml.append(operation).append("\n"));
        }

        return yaml.toString();
    }

    /**
     * Тип файла.
     */
    @Override
    public String getFileExtension() {
        return "yaml";
    }
}