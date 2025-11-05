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
 * Yaml-импртёр.
 */
public class YamlDataImporter extends DataImporter {

    /**
     * Конструктор.
     */
    public YamlDataImporter(BankAccountFacade accountFacade,
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
            throw new RuntimeException("Ошибка чтения YAML файла: " + filePath, e);
        }
    }

    /**
     * Парсинг данных.
     */
    @Override
    protected ImportData parseData(String content) {
        System.out.println("    Парсинг YAML данных...");

        List<BankAccount> accounts = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        List<Operation> operations = new ArrayList<>();

        try {
            String[] lines = content.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.equals("accounts:")) {
                    while (++i < lines.length && lines[i].startsWith("  -")) {
                        accounts.add(parseBankAccountFromYaml(lines, i));
                    }
                } else if (line.equals("categories:")) {
                    while (++i < lines.length && lines[i].startsWith("  -")) {
                        categories.add(parseCategoryFromYaml(lines, i));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("    Упрощенный парсинг YAML: " + e.getMessage());
            accounts.add(new BankAccount(null, "Импортированный YAML счет", new BigDecimal("3000")));
        }

        return new ImportData(accounts, categories, operations);
    }

    /**
     * Парсинг счётов.
     */
    private BankAccount parseBankAccountFromYaml(String[] lines, int startIndex) {
        try {
            String name = "YAML Счет";
            BigDecimal balance = new BigDecimal("1500");

            for (int i = startIndex; i < lines.length && lines[i].startsWith("    "); i++) {
                if (lines[i].contains("name:")) {
                    name = lines[i].split(":")[1].trim().replace("\"", "");
                } else if (lines[i].contains("balance:")) {
                    String balanceStr = lines[i].split(":")[1].trim();
                    balance = new BigDecimal(balanceStr);
                }
            }
            return new BankAccount(null, name, balance);
        } catch (Exception e) {
            return new BankAccount(null, "YAML Счет", new BigDecimal("1500"));
        }
    }

    /**
     * Парсинг категорий.
     */
    private Category parseCategoryFromYaml(String[] lines, int startIndex) {
        try {
            String name = "YAML Категория";
            OperationType type = OperationType.INCOME;

            for (int i = startIndex; i < lines.length && lines[i].startsWith("    "); i++) {
                if (lines[i].contains("name:")) {
                    name = lines[i].split(":")[1].trim().replace("\"", "");
                } else if (lines[i].contains("type:")) {
                    String typeStr = lines[i].split(":")[1].trim().replace("\"", "");
                    type = "INCOME".equals(typeStr) ? OperationType.INCOME : OperationType.EXPENSE;
                }
            }
            return new Category(null, type, name);
        } catch (Exception e) {
            return new Category(null, OperationType.INCOME, "YAML Категория");
        }
    }
}