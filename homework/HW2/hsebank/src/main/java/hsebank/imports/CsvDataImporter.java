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
 * Csv-ипортёр.
 */
public class CsvDataImporter extends DataImporter {


    /**
     * конструктор.
     */
    public CsvDataImporter(BankAccountFacade accountFacade,
                           CategoryFacade categoryFacade,
                           OperationFacade operationFacade) {
        super(accountFacade, categoryFacade, operationFacade);
    }


    /**
     * Прочитать файл.
     */
    @Override
    protected String readFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка чтения CSV файла: " + filePath, e);
        }
    }


    /**
     * Парсинг данных.
     */
    @Override
    protected ImportData parseData(String content) {
        System.out.println("   Парсинг CSV данных...");

        List<BankAccount> accounts = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        try {
            String[] lines = content.split("\n");
            for (String line : lines) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (line.startsWith("ACCOUNT_ID")) {
                    continue;
                } else if (line.startsWith("CATEGORY_ID")) {
                    continue;
                } else if (line.startsWith("OPERATION_ID")) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    if (isNumeric(parts[0])) {
                        if (parts.length == 3) {
                            try {
                                Long id = Long.parseLong(parts[0]);
                                String name = parts[1];
                                BigDecimal balance = new BigDecimal(parts[2]);
                                accounts.add(new BankAccount(id, name, balance));
                            } catch (NumberFormatException e) {
                                System.out.println("Ошибка: " + e.getMessage());
                            }
                        } else if (parts.length >= 4) {
                            try {
                                Long id = Long.parseLong(parts[0]);
                                if ("INCOME".equals(parts[1]) || "EXPENSE".equals(parts[1])) {
                                    OperationType type = "INCOME".equals(parts[1])
                                            ? OperationType.INCOME : OperationType.EXPENSE;
                                    categories.add(new Category(id, type, parts[2]));
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Ошибка: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("   Упрощенный парсинг CSV: " + e.getMessage());
            accounts.add(new BankAccount(null, "Импортированный CSV счет", new BigDecimal("2000")));
            categories.add(new Category(null, OperationType.EXPENSE, "CSV Расход"));
        }

        System.out.println("  Распарсено: " + accounts.size() + " счетов, "
                + categories.size() + " категорий");
        return new ImportData(accounts, categories, operations);
    }


    /**
     * Проверка является ли числом.
     */
    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}