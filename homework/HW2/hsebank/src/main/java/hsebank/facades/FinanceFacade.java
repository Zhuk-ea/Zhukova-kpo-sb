package hsebank.facades;

import hsebank.command.AddOperationCommand;
import hsebank.command.CreateAccountCommand;
import hsebank.command.CreateCategoryCommand;
import hsebank.command.DeleteAccountCommand;
import hsebank.command.DeleteCategoryCommand;
import hsebank.command.DeleteOperationCommand;
import hsebank.command.RecalculateBalanceCommand;
import hsebank.decorator.TimingDecorator;
import hsebank.domain.BankAccount;
import hsebank.domain.Category;
import hsebank.domain.Operation;
import hsebank.domain.OperationType;
import hsebank.export.DataExporter;
import hsebank.export.ExportVisitor;
import hsebank.imports.DataImporter;
import hsebank.interfaces.Command;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Главный фасад.
 */
public class FinanceFacade {
    private final BankAccountFacade accountFacade;
    private final CategoryFacade categoryFacade;
    private final OperationFacade operationFacade;
    private final DataExporter dataExporter;

    /**
     * Конструктор.
     */
    public FinanceFacade(BankAccountFacade accountFacade,
                         CategoryFacade categoryFacade,
                         OperationFacade operationFacade,
                         DataExporter dataExporter) {
        this.accountFacade = accountFacade;
        this.categoryFacade = categoryFacade;
        this.operationFacade = operationFacade;
        this.dataExporter = dataExporter;
    }

    /**
     * Создание нового счета.
     */
    public BankAccount createAccount(String name, BigDecimal initialBalance) {
        return accountFacade.createAccount(name, initialBalance);
    }

    public BankAccount createAccount(String name) {
        return createAccount(name, BigDecimal.ZERO);
    }

    /**
     * Создание категории.
     */
    public Category createIncomeCategory(String name) {
        return categoryFacade.createIncomeCategory(name);
    }

    public Category createExpenseCategory(String name) {
        return categoryFacade.createExpenseCategory(name);
    }

    /**
     * Добавление операции поступления.
     */
    public Operation addIncome(Long accountId, BigDecimal amount, Long categoryId, String description) {
        return operationFacade.createIncomeOperation(accountId, amount, categoryId, description);
    }

    /**
     * Добавление операции траты.
     */
    public Operation addExpense(Long accountId, BigDecimal amount, Long categoryId, String description) {
        return operationFacade.createExpenseOperation(accountId, amount, categoryId, description);
    }

    /**
     * Получение баланса счета.
     */
    public Optional<BigDecimal> getAccountBalance(Long accountId) {
        return accountFacade.getById(accountId).map(BankAccount::getBalance);
    }

    /**
     * Общий баланс всех счетов.
     */
    public BigDecimal getTotalBalance() {
        return accountFacade.getTotalBalance();
    }

    /**
     * Суммарные постепления.
     */
    public BigDecimal getTotalIncome() {
        return operationFacade.getTotalIncome();
    }

    /**
     * Суммарные траты.
     */
    public BigDecimal getTotalExpenses() {
        return operationFacade.getTotalExpenses();
    }

    /**
     * Суммарный баланс.
     */
    public BigDecimal getNetBalance() {
        return operationFacade.getBalance();
    }

    /**
     * Получение операций по счету.
     */
    public List<Operation> getAccountOperations(Long accountId) {
        return operationFacade.getOperationsByBankAccount(accountId);
    }

    /**
     * Получение категорий поступлений.
     */
    public List<Category> getIncomeCategories() {
        return categoryFacade.getCategoriesByType(OperationType.INCOME);
    }

    /**
     * Получение категорий трат.
     */
    public List<Category> getExpenseCategories() {
        return categoryFacade.getCategoriesByType(OperationType.EXPENSE);
    }


    /**
     * Создание команды для добавления дохода (с таймингом).
     */
    public Command createAddIncomeCommand(Long accountId, BigDecimal amount, Long categoryId, String description) {
        AddOperationCommand baseCommand = new AddOperationCommand(
                operationFacade, OperationType.INCOME, accountId, amount, categoryId, description
        );
        return new TimingDecorator(baseCommand);
    }

    /**
     * Создание команды для добавления расхода (с таймингом).
     */
    public Command createAddExpenseCommand(Long accountId, BigDecimal amount, Long categoryId, String description) {
        AddOperationCommand baseCommand = new AddOperationCommand(
                operationFacade, OperationType.EXPENSE, accountId, amount, categoryId, description
        );
        return new TimingDecorator(baseCommand);
    }

    /**
     * Создание команды для создания счета (с таймингом).
     */
    public Command createCreateAccountCommand(String name, BigDecimal initialBalance) {
        CreateAccountCommand baseCommand = new CreateAccountCommand(accountFacade, name, initialBalance);
        return new TimingDecorator(baseCommand);
    }

    /**
     * Создание команды для создания категории (с таймингом).
     */
    public Command createCreateCategoryCommand(OperationType type, String name) {
        CreateCategoryCommand baseCommand = new CreateCategoryCommand(categoryFacade, type, name);
        return new TimingDecorator(baseCommand);
    }


    /**
     * Экспорт всех данных в указанном формате.
     */
    public void exportData(String filePath, ExportVisitor visitor) {
        dataExporter.exportData(filePath, visitor);
    }

    /**
     * Экспорт данных в JSON.
     */
    public void exportToJson(String filePath) {
        dataExporter.exportData(filePath, new hsebank.export.JsonExportVisitor());
    }

    /**
     * Экспорт данных в CSV.
     */
    public void exportToCsv(String filePath) {
        dataExporter.exportData(filePath, new hsebank.export.CsvExportVisitor());
    }

    /**
     * Экспорт данных в YAML.
     */
    public void exportToYaml(String filePath) {
        dataExporter.exportData(filePath, new hsebank.export.YamlExportVisitor());
    }


    /**
     * Импорт данных через указанный импортер.
     */
    public void importData(DataImporter importer, String filePath) {
        importer.importData(filePath);
    }


    /**
     * Поиск счетов по имени.
     */
    public List<BankAccount> findAccountsByName(String namePattern) {
        return accountFacade.findAccountsByName(namePattern);
    }

    /**
     * Поиск категорий по имени.
     */
    public List<Category> findCategoriesByName(String namePattern) {
        return categoryFacade.findCategoriesByName(namePattern);
    }

    /**
     * Получение всех счетов.
     */
    public List<BankAccount> getAllAccounts() {
        return accountFacade.getAll();
    }

    /**
     * Получение всех категорий.
     */
    public List<Category> getAllCategories() {
        return categoryFacade.getAll();
    }

    /**
     * Получение всех операций.
     */
    public List<Operation> getAllOperations() {
        return operationFacade.getAll();
    }

    /**
     * Проверка существования счета.
     */
    public boolean accountExists(Long accountId) {
        return accountFacade.exists(accountId);
    }

    /**
     * Проверка существования категории.
     */
    public boolean categoryExists(Long categoryId) {
        return categoryFacade.exists(categoryId);
    }

    /**
     * Проверка существования операции.
     */
    public boolean operationExists(Long operationId) {
        return operationFacade.exists(operationId);
    }

    /**
     * Получение информации о системе.
     */
    public String getSystemInfo() {
        long accountsCount = accountFacade.getAll().size();
        long categoriesCount = categoryFacade.getAll().size();
        long operationsCount = operationFacade.getAll().size();
        BigDecimal totalBalance = getTotalBalance();

        return String.format(
                "Финансовая система:\n"
                        + "   - Счетов: %d\n"
                        + "   - Категорий: %d\n"
                        + "   - Операций: %d\n"
                        + "   - Общий баланс: %s",
                accountsCount, categoriesCount, operationsCount, totalBalance
        );
    }

    /**
     * Создание команды для удаления счета.
     */
    public Command createDeleteAccountCommand(Long accountId) {
        DeleteAccountCommand baseCommand = new DeleteAccountCommand(accountFacade, accountId);
        return new TimingDecorator(baseCommand);
    }

    /**
     * Создание команды для удаления категории.
     */
    public Command createDeleteCategoryCommand(Long categoryId) {
        DeleteCategoryCommand baseCommand = new DeleteCategoryCommand(categoryFacade, categoryId);
        return new TimingDecorator(baseCommand);
    }

    /**
     * Создание команды для удаления операции.
     */
    public Command createDeleteOperationCommand(Long operationId) {
        DeleteOperationCommand baseCommand = new DeleteOperationCommand(operationFacade, operationId);
        return new TimingDecorator(baseCommand);
    }

    /**
     * Создание команды для пересчета баланса счета.
     */
    public Command createRecalculateBalanceCommand(Long accountId) {
        RecalculateBalanceCommand baseCommand = new RecalculateBalanceCommand(
                accountFacade, operationFacade, accountId
        );
        return new TimingDecorator(baseCommand);
    }
}