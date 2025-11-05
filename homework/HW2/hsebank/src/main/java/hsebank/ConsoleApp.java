package hsebank;

import hsebank.decorator.LoggingDecorator;
import hsebank.decorator.TimingDecorator;
import hsebank.di.DiContainer;
import hsebank.domain.BankAccount;
import hsebank.domain.Category;
import hsebank.domain.Operation;
import hsebank.domain.OperationType;
import hsebank.facades.FinanceFacade;
import hsebank.interfaces.Command;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Класс консольного приложения.
 */
public class ConsoleApp {
    private final FinanceFacade finance;
    private final Scanner scanner;
    private final DiContainer container;

    /**
     * Конструктор по умолчанию.
     */
    public ConsoleApp() {

        this.container = new DiContainer();
        this.finance = container.createFinanceFacade();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Функция для запуска консольного приложения.
     */
    public void start() {
        System.out.println("Добро пожаловать в HSE Bank Finance System!");
        System.out.println("=============================================");

        while (true) {
            showMainMenu();
            int choice = readIntInput("Выберите действие: ");

            switch (choice) {
                case 1 -> manageAccounts();
                case 2 -> manageCategories();
                case 3 -> manageOperations();
                case 4 -> showAnalytics();
                case 5 -> showPatternsDemo();
                case 6 -> importExportData();
                case 7 -> showSystemInfo();
                case 0 -> {
                    System.out.println("Приложение остановлено");
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    /**
     * Функция для показа меню.
     */
    private void showMainMenu() {
        System.out.println("\nГЛАВНОЕ МЕНЮ:");
        System.out.println("1. Управление счетами");
        System.out.println("2. Управление категориями");
        System.out.println("3. Управление операциями");
        System.out.println("4. Аналитика и отчеты");
        System.out.println("5. Демонстрация функциональности паттернов");
        System.out.println("6. Импорт/Экспорт данных");
        System.out.println("7. Информация о системе");
        System.out.println("0. Выход");
    }

    /**
     * Функция управления счетами.
     */
    private void manageAccounts() {
        while (true) {
            System.out.println("\nУПРАВЛЕНИЕ СЧЕТАМИ:");
            System.out.println("1. Создать счет");
            System.out.println("2. Просмотреть все счета");
            System.out.println("3. Найти счет по имени");
            System.out.println("4. Удалить счет");
            System.out.println("0. Назад");

            int choice = readIntInput("Выберите действие: ");
            switch (choice) {
                case 1 -> createAccount();
                case 2 -> showAllAccounts();
                case 3 -> findAccountByName();
                case 4 -> deleteAccount();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    /**
     * Функция создания аккаунта.
     */
    private void createAccount() {
        System.out.print("Введите название счета: ");
        String name = scanner.nextLine();
        BigDecimal balance = readBigDecimalInput("Введите начальный баланс: ");

        BankAccount account = finance.createAccount(name, balance);
        System.out.println("Счет создан: " + account.getName() + " (баланс: " + account.getBalance() + ")");
    }

    /**
     * Функция отображения всех счетов.
     */
    private void showAllAccounts() {
        List<BankAccount> accounts = finance.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("Счетов нет");
            return;
        }

        System.out.println("\n СПИСОК СЧЕТОВ:");
        for (BankAccount account : accounts) {
            System.out.printf("   ID: %d | %s | Баланс: %s%n",
                    account.getId(), account.getName(), account.getBalance());
        }
    }

    /**
     * Функция найти счёт по названию.
     */
    private void findAccountByName() {
        System.out.print("Введите часть названия для поиска: ");
        String pattern = scanner.nextLine();

        List<BankAccount> accounts = finance.findAccountsByName(pattern);
        if (accounts.isEmpty()) {
            System.out.println("Счетов не найдено");
            return;
        }

        System.out.println("\nРЕЗУЛЬТАТЫ ПОИСКА:");
        for (BankAccount account : accounts) {
            System.out.printf("   ID: %d | %s | Баланс: %s%n",
                    account.getId(), account.getName(), account.getBalance());
        }
    }

    /**
     * Функция удаления аккаунта.
     */
    private void deleteAccount() {
        showAllAccounts();
        Long accountId = readLongInput("Введите ID счета для удаления: ");

        if (!finance.accountExists(accountId)) {
            System.out.println(" Счет с ID " + accountId + " не найден");
            return;
        }

        Command deleteCommand = finance.createDeleteAccountCommand(accountId);
        System.out.println("  Выполняется удаление счета...");
        deleteCommand.execute();

        System.out.print("Отменить удаление? (y/n): ");
        String answer = scanner.nextLine();
        if ("y".equalsIgnoreCase(answer)) {
            deleteCommand.undo();
            System.out.println("Удаление отменено! Счет восстановлен.");
        } else {
            System.out.println("Удаление завершено.");
        }
    }

    /**
     * Функция удаления категории.
     */
    private void deleteCategory() {
        showAllCategories();
        Long categoryId = readLongInput("Введите ID категории для удаления: ");

        if (!finance.categoryExists(categoryId)) {
            System.out.println("Категория с ID " + categoryId + " не найден");
            return;
        }

        Command deleteCommand = finance.createDeleteCategoryCommand(categoryId);
        System.out.println("Выполняется удаление категории...");
        deleteCommand.execute();

        System.out.print("Отменить удаление? (y/n): ");
        String answer = scanner.nextLine();
        if ("y".equalsIgnoreCase(answer)) {
            deleteCommand.undo();
            System.out.println("Удаление отменено! Категория восстановлена.");
        } else {
            System.out.println("Удаление завершено.");
        }
    }

    /**
     * Функция удаления операции.
     */
    private void deleteOperation() {
        showAllOperations();
        Long operationId = readLongInput("Введите ID операции для удаления: ");

        if (!finance.operationExists(operationId)) {
            System.out.println("Операция с ID " + operationId + " не найдена");
            return;
        }

        var operationOpt = finance.getAllOperations().stream()
                .filter(op -> op.getId().equals(operationId))
                .findFirst();

        if (operationOpt.isEmpty()) {
            System.out.println("Не удалось найти операцию");
            return;
        }

        Command deleteCommand = finance.createDeleteOperationCommand(operationId);
        System.out.println(" Выполняется удаление операции...");
        deleteCommand.execute();
        System.out.println("Пересчет баланса счета после удаления операции...");
        Operation operation = operationOpt.get();
        Long accountId = operation.getBankAccountId();
        Command recalcCommand = finance.createRecalculateBalanceCommand(accountId);
        recalcCommand.execute();

        System.out.print("Отменить удаление? (y/n): ");
        String answer = scanner.nextLine();
        if ("y".equalsIgnoreCase(answer)) {
            deleteCommand.undo();
            recalcCommand = finance.createRecalculateBalanceCommand(accountId);
            recalcCommand.execute();
            System.out.println("Удаление отменено! Операция восстановлена, баланс пересчитан.");
        } else {
            System.out.println("Удаление завершено. Баланс счета пересчитан.");
        }
    }


    /**
     * Функция управления категориями.
     */
    private void manageCategories() {
        while (true) {
            System.out.println("\n УПРАВЛЕНИЕ КАТЕГОРИЯМИ:");
            System.out.println("1. Создать категорию дохода");
            System.out.println("2. Создать категорию расхода");
            System.out.println("3. Просмотреть все категории");
            System.out.println("4. Просмотреть категории доходов");
            System.out.println("5. Просмотреть категории расходов");
            System.out.println("6. Удалить категорию");
            System.out.println("0. Назад");

            int choice = readIntInput("Выберите действие: ");
            switch (choice) {
                case 1 -> createCategory(OperationType.INCOME);
                case 2 -> createCategory(OperationType.EXPENSE);
                case 3 -> showAllCategories();
                case 4 -> showCategoriesByType(OperationType.INCOME);
                case 5 -> showCategoriesByType(OperationType.EXPENSE);
                case 6 -> deleteCategory();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    /**
     * Функция создания категории.
     */
    private void createCategory(OperationType type) {
        System.out.print("Введите название категории: ");
        String name = scanner.nextLine();

        Category category = type == OperationType.INCOME
                ? finance.createIncomeCategory(name) : finance.createExpenseCategory(name);
        System.out.println("Категория создана: " + category.getName() + " (" + type + ")");
    }

    /**
     * Функция отображения всех категорий.
     */
    private void showAllCategories() {
        List<Category> categories = finance.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("Категорий нет");
            return;
        }

        System.out.println("\nВСЕ КАТЕГОРИИ:");
        for (Category category : categories) {
            System.out.printf("   ID: %d | %s | Тип: %s%n",
                    category.getId(), category.getName(), category.getType());
        }
    }

    /**
     * Функция отобразить категории определённого типа.
     */
    private void showCategoriesByType(OperationType type) {
        List<Category> categories = type == OperationType.INCOME
                ? finance.getIncomeCategories() : finance.getExpenseCategories();

        if (categories.isEmpty()) {
            System.out.println("Категорий " + type + " нет");
            return;
        }

        System.out.println("\nКАТЕГОРИИ " + type + ":");
        for (Category category : categories) {
            System.out.printf("   ID: %d | %s%n", category.getId(), category.getName());
        }
    }

    /**
     * Функция управления операциями.
     */
    private void manageOperations() {
        while (true) {
            System.out.println("\n УПРАВЛЕНИЕ ОПЕРАЦИЯМИ:");
            System.out.println("1. Добавить доход");
            System.out.println("2. Добавить расход");
            System.out.println("3. Просмотреть все операции");
            System.out.println("4. Просмотреть операции по счету");
            System.out.println("5. Удалить операцию");
            System.out.println("0. Назад");

            int choice = readIntInput("Выберите действие: ");
            switch (choice) {
                case 1 -> addOperation(OperationType.INCOME);
                case 2 -> addOperation(OperationType.EXPENSE);
                case 3 -> showAllOperations();
                case 4 -> showAccountOperations();
                case 5 -> deleteOperation();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    /**
     * Функция добавления операции.
     */
    private void addOperation(OperationType type) {
        showAllAccounts();
        Long accountId = readLongInput("Введите ID счета: ");

        showCategoriesByType(type);
        Long categoryId = readLongInput("Введите ID категории: ");

        BigDecimal amount = readBigDecimalInput("Введите сумму: ");
        System.out.print("Введите описание: ");
        String description = scanner.nextLine();

        Operation operation = type == OperationType.INCOME
                ? finance.addIncome(accountId, amount, categoryId, description) :
                finance.addExpense(accountId, amount, categoryId, description);

        System.out.println("Операция добавлена: " + type + " на сумму " + amount);
    }

    /**
     * Функция отображения всех операций.
     */
    private void showAllOperations() {
        List<Operation> operations = finance.getAllOperations();
        if (operations.isEmpty()) {
            System.out.println("Операций нет");
            return;
        }

        System.out.println("\nВСЕ ОПЕРАЦИИ:");
        for (Operation operation : operations) {
            System.out.printf("   ID: %d | %s | Сумма: %s | Дата: %s | Описание: %s%n",
                    operation.getId(), operation.getType(), operation.getAmount(),
                    operation.getDate(), operation.getDescription());
        }
    }

    /**
     * Функция отображения всех операций с определённым счётом.
     */
    private void showAccountOperations() {
        showAllAccounts();
        Long accountId = readLongInput("Введите ID счета: ");

        List<Operation> operations = finance.getAccountOperations(accountId);
        if (operations.isEmpty()) {
            System.out.println("Операций по этому счету нет");
            return;
        }

        System.out.println("\nОПЕРАЦИИ ПО СЧЕТУ:");
        for (Operation operation : operations) {
            System.out.printf("   %s | Сумма: %s | Дата: %s | Описание: %s%n",
                    operation.getType(), operation.getAmount(),
                    operation.getDate(), operation.getDescription());
        }
    }

    /**
     * Функция управления финансовой аналитикой.
     */
    private void showAnalytics() {
        System.out.println("\n ФИНАНСОВАЯ АНАЛИТИКА:");
        System.out.println("   Общий доход: " + finance.getTotalIncome());
        System.out.println("   Общие расходы: " + finance.getTotalExpenses());
        System.out.println("   Чистый баланс: " + finance.getNetBalance());
        System.out.println("   Общий баланс счетов: " + finance.getTotalBalance());

        System.out.println("\n ДЕТАЛИ ПО СЧЕТАМ:");
        List<BankAccount> accounts = finance.getAllAccounts();
        for (BankAccount account : accounts) {
            System.out.printf("   %s: %s%n", account.getName(), account.getBalance());
        }
    }

    /**
     * Функция для демонстрации функциональности паттернов.
     */
    private void showPatternsDemo() {
        System.out.println("\n ДЕМОНСТРАЦИЯ ФУНКЦИОНАЛЬНОСТИ ПАТТЕРНОВ:");

        List<BankAccount> accounts = finance.getAllAccounts();
        List<Category> categories = finance.getAllCategories();

        if (accounts.isEmpty() || categories.isEmpty()) {
            System.out.println("  Для демонстрации создайте хотя бы один счет и одну категорию");
            System.out.println("   Перейдите в разделы 1 и 2 главного меню");
            return;
        }

        Long accountId = accounts.get(0).getId();
        Long categoryId = categories.get(0).getId();

        System.out.println("\n  Паттерн КОМАНДА + ДЕКОРАТОР (автоматическое измерение времени):");
        System.out.println("   Создание операции дохода с замером времени выполнения...");

        Command timedCommand = finance.createAddIncomeCommand(
                accountId, new BigDecimal("1500"), categoryId, "Демо паттернов"
        );
        timedCommand.execute();
        System.out.println("\n  Паттерн КОМАНДА (функция отмены действия):");
        System.out.println("   Отмена предыдущей операции...");
        timedCommand.undo();

        System.out.println("\n Паттерн ДЕКОРАТОР (комбинация функциональности):");
        System.out.println("   Создание операции с логированием и повторением...");

        Command baseCommand = new hsebank.command.AddOperationCommand(
                container.getInstance(hsebank.facades.OperationFacade.class),
                OperationType.INCOME, accountId, new BigDecimal("2000"), categoryId, "Комбинированная демо"
        );

        Command decoratedCommand = new TimingDecorator(
                new LoggingDecorator(baseCommand)
        );

        decoratedCommand.execute();

        System.out.println("\n Демонстрация завершена!");
        System.out.println("   Показаны паттерны: Команда, Декоратор, Автоматическое измерение времени");
    }

    /**
     * Функция для импорта и экспорта.
     */
    private void importExportData() {
        while (true) {
            System.out.println("\n ИМПОРТ/ЭКСПОРТ ДАННЫХ:");
            System.out.println("1. Экспорт в JSON");
            System.out.println("2. Экспорт в CSV");
            System.out.println("3. Экспорт в YAML");
            System.out.println("4. Импорт из JSON");
            System.out.println("5. Импорт из CSV");
            System.out.println("0. Назад");

            int choice = readIntInput("Выберите действие: ");
            switch (choice) {
                case 1 -> finance.exportToJson("export_data.json");
                case 2 -> finance.exportToCsv("export_data.csv");
                case 3 -> finance.exportToYaml("export_data.yaml");
                case 4 -> finance.importData(container.createJsonImporter(), "export_data.json");
                case 5 -> finance.importData(container.createCsvImporter(), "export_data.csv");
                case 0 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор!");
            }
            System.out.println("Операция завершена!");
        }
    }

    /**
     * Функция для отображения информации о системе.
     */
    private void showSystemInfo() {
        System.out.println(finance.getSystemInfo());
    }

    /**
     * Функция для считывания чисел.
     */
    private int readIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число!");
            }
        }
    }

    /**
     * Функция для считывания id.
     */
    private Long readLongInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Введите корректный ID!");
            }
        }
    }

    /**
     * Функция для считывания BigDecimal.
     */
    private BigDecimal readBigDecimalInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Введите корректную сумму!");
            }
        }
    }


}