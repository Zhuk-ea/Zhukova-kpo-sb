package hsebank.imports;

import hsebank.domain.BankAccount;
import hsebank.domain.Category;
import hsebank.domain.Operation;
import hsebank.facades.BankAccountFacade;
import hsebank.facades.CategoryFacade;
import hsebank.facades.OperationFacade;
import java.util.List;

/**
 * Шаблонный класс ипортёра.
 */
public abstract class DataImporter {
    protected final BankAccountFacade accountFacade;
    protected final CategoryFacade categoryFacade;
    protected final OperationFacade operationFacade;

    /**
     * Конструктор.
     */
    public DataImporter(BankAccountFacade accountFacade,
                        CategoryFacade categoryFacade,
                        OperationFacade operationFacade) {
        this.accountFacade = accountFacade;
        this.categoryFacade = categoryFacade;
        this.operationFacade = operationFacade;
    }

    /**
     * Шаблонный метод импорта.
     */
    public final void importData(String filePath) {
        System.out.println("Начало импорта данных из файла: " + filePath);

        try {
            String fileContent = readFile(filePath);
            System.out.println("   Файл прочитан, размер: " + fileContent.length() + " символов");

            ImportData data = parseData(fileContent);
            System.out.println("   Данные распарсены: "
                   + data.accounts.size() + " счетов, "
                   + data.categories.size() + " категорий, "
                   + data.operations.size() + " операций");

            saveData(data);
            System.out.println("   Данные успешно сохранены в систему");

        } catch (Exception e) {
            System.out.println("Ошибка при импорте: " + e.getMessage());
            throw new RuntimeException("Импорт данных не удался", e);
        }

        System.out.println("Импорт завершен успешно!");
    }

    /**
     * Абстрактыный метод чтения файла.
     */
    protected abstract String readFile(String filePath);

    /**
     * Абстрактыный метод парса данных.
     */
    protected abstract ImportData parseData(String content);

    /**
     * Сохранение данных.
     */
    private void saveData(ImportData data) {
        for (BankAccount account : data.accounts) {
            accountFacade.create(account);
        }

        for (Category category : data.categories) {
            categoryFacade.create(category);
        }

        for (Operation operation : data.operations) {
            operationFacade.create(operation);
        }
    }

    /**
     * Класс для хранения распаршеных данных.
     */
    public static class ImportData {
        public List<BankAccount> accounts;
        public List<Category> categories;
        public List<Operation> operations;

        /**
         * Конструктор.
         */
        public ImportData(List<BankAccount> accounts, List<Category> categories, List<Operation> operations) {
            this.accounts = accounts;
            this.categories = categories;
            this.operations = operations;
        }
    }
}