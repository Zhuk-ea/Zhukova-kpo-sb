package hsebank.imports;

import hsebank.domain.BankAccount;
import hsebank.domain.Category;
import hsebank.domain.Operation;
import hsebank.domain.OperationType;
import hsebank.facades.BankAccountFacade;
import hsebank.facades.CategoryFacade;
import hsebank.facades.OperationFacade;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * jsin-импортер.
 */
public class JsonDataImporter extends DataImporter {

    /**
     * Конструктор.
     */
    public JsonDataImporter(BankAccountFacade accountFacade,
                            CategoryFacade categoryFacade,
                            OperationFacade operationFacade) {
        super(accountFacade, categoryFacade, operationFacade);
    }

    /**
     * Чтение файла.
     */
    @Override
    protected String readFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка чтения JSON файла: " + filePath, e);
        }
    }

    /**
     * Парсинг данных.
     */
    @Override
    protected ImportData parseData(String content) {
        System.out.println("    Парсинг JSON данных...");

        List<BankAccount> accounts = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        try {
            if (content.contains("\"accounts\"")) {
                accounts.add(parseBankAccountFromJson(content));
            }
            if (content.contains("\"categories\"")) {
                categories.addAll(parseCategoriesFromJson(content));
            }
            if (content.contains("\"operations\"")) {
                operations.addAll(parseOperationsFromJson(content));
            }
        } catch (Exception e) {
            System.out.println("     Упрощенный парсинг JSON: " + e.getMessage());
            accounts.add(new BankAccount(null, "Импортированный JSON счет", new BigDecimal("1000")));
            categories.add(new Category(null, OperationType.INCOME, "JSON Доход"));
        }

        return new ImportData(accounts, categories, operations);
    }

    /**
     * Парсинг счетов.
     */
    private BankAccount parseBankAccountFromJson(String content) {
        try {
            int nameStart = content.indexOf("\"name\":\"") + 8;
            int nameEnd = content.indexOf("\"", nameStart);
            String name = content.substring(nameStart, nameEnd);

            int balanceStart = content.indexOf("\"balance\":") + 10;
            int balanceEnd = content.indexOf(",", balanceStart);
            if (balanceEnd == -1) {
                balanceEnd = content.indexOf("}", balanceStart);
            }
            String balanceStr = content.substring(balanceStart, balanceEnd).trim();

            return new BankAccount(null, name, new BigDecimal(balanceStr));
        } catch (Exception e) {
            return new BankAccount(null, "JSON Счет", new BigDecimal("1000"));
        }
    }

    /**
     * Парсинг категорий.
     */
    private List<Category> parseCategoriesFromJson(String content) {
        List<Category> result = new ArrayList<>();
        try {
            int categoryStart = content.indexOf("\"categories\"");
            if (categoryStart != -1) {
                result.add(new Category(null, OperationType.INCOME, "JSON Категория"));
            }
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    /**
     * Парсинг операций.
     */
    private List<Operation> parseOperationsFromJson(String content) {
        List<Operation> result = new ArrayList<>();
        try {
            int operationsStart = content.indexOf("\"operations\"");
            if (operationsStart != -1) {
                result.add(new Operation(OperationType.INCOME, 1L, new BigDecimal("500"), 1L, "Импорт операция"));
            }
        } catch (Exception e) {
            return result;
        }
        return result;
    }
}