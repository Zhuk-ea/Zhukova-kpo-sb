package hsebank.export;

import hsebank.domain.BankAccount;
import hsebank.domain.Category;
import hsebank.domain.Operation;
import hsebank.domain.OperationType;
import java.util.ArrayList;
import java.util.List;

/**
 * Посетитель JsonExport.
 */
public class JsonExportVisitor implements ExportVisitor {
    private final List<String> accountsJson = new ArrayList<>();
    private final List<String> categoriesJson = new ArrayList<>();
    private final List<String> operationsJson = new ArrayList<>();

    /**
     * Посетить счёт.
     */
    @Override
    public void visit(BankAccount account) {
        String json = String.format(
                "  {\"id\": %d, \"name\": \"%s\", \"balance\": %s}",
                account.getId(), account.getName(), account.getBalance()
        );
        accountsJson.add(json);
    }

    /**
     * Посетитель категорию.
     */
    @Override
    public void visit(Category category) {
        String json = String.format(
                "  {\"id\": %d, \"type\": \"%s\", \"name\": \"%s\"}",
                category.getId(), category.getType(), category.getName()
        );
        categoriesJson.add(json);
    }

    /**
     * Посетитель операцию.
     */
    @Override
    public void visit(Operation operation) {
        String json = String.format(
                "  {\"id\": %d, \"type\": \"%s\", \"accountId\": %d, \"amount\": %s,"
                        + " \"date\": \"%s\", \"description\": \"%s\", \"categoryId\": %d}",
                operation.getId(), operation.getType(), operation.getBankAccountId(),
                operation.getAmount(), operation.getDate(),
                operation.getDescription() != null ? operation.getDescription() : "",
                operation.getCategoryId()
        );
        operationsJson.add(json);
    }

    /**
     * Экспорт.
     */
    @Override
    public String getResult() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");

        if (!accountsJson.isEmpty()) {
            json.append("  \"accounts\": [\n");
            json.append(String.join(",\n", accountsJson));
            json.append("\n  ],\n");
        }

        if (!categoriesJson.isEmpty()) {
            json.append("  \"categories\": [\n");
            json.append(String.join(",\n", categoriesJson));
            json.append("\n  ],\n");
        }

        if (!operationsJson.isEmpty()) {
            json.append("  \"operations\": [\n");
            json.append(String.join(",\n", operationsJson));
            json.append("\n  ]\n");
        }

        json.append("}");
        return json.toString();
    }

    /**
     * Тип файла.
     */
    @Override
    public String getFileExtension() {
        return "json";
    }
}