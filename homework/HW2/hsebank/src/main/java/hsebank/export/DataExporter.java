package hsebank.export;

import hsebank.domain.BankAccount;
import hsebank.domain.Category;
import hsebank.domain.Operation;
import hsebank.facades.BankAccountFacade;
import hsebank.facades.CategoryFacade;
import hsebank.facades.OperationFacade;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Экспортёр.
 */
public class DataExporter {
    private final BankAccountFacade accountFacade;
    private final CategoryFacade categoryFacade;
    private final OperationFacade operationFacade;

    /**
     * Конструктор.
     */
    public DataExporter(BankAccountFacade accountFacade,
                        CategoryFacade categoryFacade,
                        OperationFacade operationFacade) {
        this.accountFacade = accountFacade;
        this.categoryFacade = categoryFacade;
        this.operationFacade = operationFacade;
    }

    /**
     * Экпорт.
     */
    public void exportData(String filePath, ExportVisitor visitor) {
        System.out.println("Начало экспорта данных в файл: " + filePath);

        try {
            List<BankAccount> accounts = accountFacade.getAll();
            for (BankAccount account : accounts) {
                visitor.visit(account);
            }

            List<Category> categories = categoryFacade.getAll();
            for (Category category : categories) {
                visitor.visit(category);
            }

            List<Operation> operations = operationFacade.getAll();
            for (Operation operation : operations) {
                visitor.visit(operation);
            }

            String result = visitor.getResult();
            Files.write(Paths.get(filePath), result.getBytes());
            System.out.println("Экспорт завершен: "
                    + accounts.size() + " счетов, "
                    + categories.size() + " категорий, "
                    + operations.size() + " операций");
        } catch (Exception e) {
            System.out.println("Ошибка при экспорте: " + e.getMessage());
            throw new RuntimeException("Экспорт данных не удался", e);
        }
    }
}