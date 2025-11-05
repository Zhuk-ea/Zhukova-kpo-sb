package hsebank.di;

import hsebank.decorator.LoggingDecorator;
import hsebank.decorator.RetryDecorator;
import hsebank.decorator.TimingDecorator;
import hsebank.export.DataExporter;
import hsebank.facades.BankAccountFacade;
import hsebank.facades.CategoryFacade;
import hsebank.facades.FinanceFacade;
import hsebank.facades.OperationFacade;
import hsebank.factory.BankAccountFactory;
import hsebank.factory.CategoryFactory;
import hsebank.factory.OperationFactory;
import hsebank.imports.CsvDataImporter;
import hsebank.imports.JsonDataImporter;
import hsebank.imports.YamlDataImporter;
import hsebank.interfaces.Command;
import hsebank.repository.BankAccountRepository;
import hsebank.repository.CategoryRepository;
import hsebank.repository.OperationRepository;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * DI контейнер.
 */
public class DiContainer {
    private final Map<Class<?>, Object> instances = new HashMap<>();

    /**
     * Конструктор по умолчанию.
     */
    public DiContainer() {
        initializeContainer();
    }

    /**
     * Инициализация контейнера.
     */
    private void initializeContainer() {

        register(BankAccountRepository.class, new BankAccountRepository());
        register(CategoryRepository.class, new CategoryRepository());
        register(OperationRepository.class, new OperationRepository());

        register(BankAccountFactory.class, new BankAccountFactory());
        register(CategoryFactory.class, new CategoryFactory());
        register(OperationFactory.class, new OperationFactory());

        register(BankAccountFacade.class, new BankAccountFacade(
                getInstance(BankAccountRepository.class),
                getInstance(BankAccountFactory.class)
        ));

        register(CategoryFacade.class, new CategoryFacade(
                getInstance(CategoryRepository.class),
                getInstance(CategoryFactory.class)
        ));

        register(OperationFacade.class, new OperationFacade(
                getInstance(OperationRepository.class),
                getInstance(OperationFactory.class),
                getInstance(BankAccountFacade.class)
        ));

        register(DataExporter.class, new DataExporter(
                getInstance(BankAccountFacade.class),
                getInstance(CategoryFacade.class),
                getInstance(OperationFacade.class)
        ));
    }

    /**
     * Регистрация.
     */
    public <T> void register(Class<T> type, T instance) {
        instances.put(type, instance);
    }

    /**
     * Поиск объекта.
     */
    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> type) {
        T instance = (T) instances.get(type);
        if (instance == null) {
            throw new IllegalArgumentException("No instance registered for type: " + type.getName());
        }
        return instance;
    }

    /**
     * Создание json-импортёра.
     */
    public JsonDataImporter createJsonImporter() {
        return new JsonDataImporter(
                getInstance(BankAccountFacade.class),
                getInstance(CategoryFacade.class),
                getInstance(OperationFacade.class)
        );
    }

    /**
     * Создание csv-импортёра.
     */
    public CsvDataImporter createCsvImporter() {
        return new CsvDataImporter(
                getInstance(BankAccountFacade.class),
                getInstance(CategoryFacade.class),
                getInstance(OperationFacade.class)
        );
    }

    /**
     * Создание yaml-импортёра.
     */
    public YamlDataImporter createYamlImporter() {
        return new YamlDataImporter(
                getInstance(BankAccountFacade.class),
                getInstance(CategoryFacade.class),
                getInstance(OperationFacade.class)
        );
    }

    /**
     * Команда создания счёта.
     */
    public hsebank.command.CreateAccountCommand createCreateAccountCommand(String name, BigDecimal balance) {
        return new hsebank.command.CreateAccountCommand(
                getInstance(BankAccountFacade.class), name, balance
        );
    }

    /**
     * Команда создания категории.
     */
    public hsebank.command.CreateCategoryCommand createCreateCategoryCommand(hsebank.domain.OperationType type,
                                                                             String name) {
        return new hsebank.command.CreateCategoryCommand(
                getInstance(CategoryFacade.class), type, name
        );
    }

    /**
     * Команда создания операции.
     */
    public hsebank.command.AddOperationCommand createAddOperationCommand(
            hsebank.domain.OperationType type, Long accountId,
            BigDecimal amount, Long categoryId, String description) {
        return new hsebank.command.AddOperationCommand(
                getInstance(OperationFacade.class), type, accountId, amount, categoryId, description
        );
    }

    /**
     * Команда добавления к команде счётчика времени.
     */
    public Command createTimedCommand(Command command) {
        return new TimingDecorator(command);
    }

    /**
     * Команда добавления к команде логирования.
     */
    public Command createLoggedCommand(Command command) {
        return new LoggingDecorator(command);
    }

    /**
     * Команда добавления к команде возможности отмены.
     */
    public Command createRetryCommand(Command command, int maxRetries) {
        return new RetryDecorator(command, maxRetries);
    }

    /**
     * Создание посетителя json-импортёра.
     */
    public hsebank.export.JsonExportVisitor createJsonExportVisitor() {
        return new hsebank.export.JsonExportVisitor();
    }

    /**
     * Создание посетителя csv-импортёра.
     */
    public hsebank.export.CsvExportVisitor createCsvExportVisitor() {
        return new hsebank.export.CsvExportVisitor();
    }

    /**
     * Создание посетителя yaml-импортёра.
     */
    public hsebank.export.YamlExportVisitor createYamlExportVisitor() {
        return new hsebank.export.YamlExportVisitor();
    }

    /**
     * Создание финансового фасада.
     */
    public FinanceFacade createFinanceFacade() {
        return new FinanceFacade(
                getInstance(BankAccountFacade.class),
                getInstance(CategoryFacade.class),
                getInstance(OperationFacade.class),
                getInstance(DataExporter.class)
        );
    }
}