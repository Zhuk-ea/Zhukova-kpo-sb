package hsebank.export;

import hsebank.domain.BankAccount;
import hsebank.domain.Category;
import hsebank.domain.Operation;
import hsebank.domain.OperationType;
import java.util.ArrayList;
import java.util.List;

/**
 * Посетитель CsvExport.
 */
public class CsvExportVisitor implements ExportVisitor {
    private final List<String> accountsCsv = new ArrayList<>();
    private final List<String> categoriesCsv = new ArrayList<>();
    private final List<String> operationsCsv = new ArrayList<>();

    /**
     * Конструктор.
     */
    public CsvExportVisitor() {
        accountsCsv.add("ACCOUNT_ID,NAME,BALANCE");
        categoriesCsv.add("CATEGORY_ID,TYPE,NAME");
        operationsCsv.add("OPERATION_ID,TYPE,ACCOUNT_ID,AMOUNT,DATE,DESCRIPTION,CATEGORY_ID");
    }

    /**
     * Посетить счёт.
     */
    @Override
    public void visit(BankAccount account) {
        String csv = String.format("%d,%s,%s",
                account.getId(), account.getName(), account.getBalance()
        );
        accountsCsv.add(csv);
    }

    /**
     * Посетить категорию.
     */
    @Override
    public void visit(Category category) {
        String csv = String.format("%d,%s,%s",
                category.getId(), category.getType(), category.getName()
        );
        categoriesCsv.add(csv);
    }

    /**
     * Посетить операции.
     */
    @Override
    public void visit(Operation operation) {
        String csv = String.format("%d,%s,%d,%s,%s,%s,%d",
                operation.getId(), operation.getType(), operation.getBankAccountId(),
                operation.getAmount(), operation.getDate(),
                operation.getDescription() != null ? operation.getDescription().replace(",", ";") : "",
                operation.getCategoryId()
        );
        operationsCsv.add(csv);
    }

    /**
     * Получить результат.
     */
    @Override
    public String getResult() {
        StringBuilder csv = new StringBuilder();

        if (!accountsCsv.isEmpty()) {
            csv.append("# ACCOUNTS\n");
            accountsCsv.forEach(line -> csv.append(line).append("\n"));
            csv.append("\n");
        }

        if (!categoriesCsv.isEmpty()) {
            csv.append("# CATEGORIES\n");
            categoriesCsv.forEach(line -> csv.append(line).append("\n"));
            csv.append("\n");
        }

        if (!operationsCsv.isEmpty()) {
            csv.append("# OPERATIONS\n");
            operationsCsv.forEach(line -> csv.append(line).append("\n"));
        }

        return csv.toString();
    }

    /**
     * Тип файла.
     */
    @Override
    public String getFileExtension() {
        return "csv";
    }
}