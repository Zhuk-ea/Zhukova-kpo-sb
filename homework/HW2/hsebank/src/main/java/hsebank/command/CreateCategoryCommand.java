package hsebank.command;

import hsebank.domain.Category;
import hsebank.domain.OperationType;
import hsebank.facades.CategoryFacade;
import hsebank.interfaces.Command;

/**
 * Создание категории.
 */
public class CreateCategoryCommand implements Command {
    private final CategoryFacade categoryFacade;
    private final OperationType type;
    private final String categoryName;
    private Category createdCategory;

    /**
     * Конструктор.
     */
    public CreateCategoryCommand(CategoryFacade categoryFacade,
                                 OperationType type, String categoryName) {
        this.categoryFacade = categoryFacade;
        this.type = type;
        this.categoryName = categoryName;
    }

    /**
     * Создание категории.
     */
    @Override
    public void execute() {
        if (type == OperationType.INCOME) {
            createdCategory = categoryFacade.createIncomeCategory(categoryName);
        } else {
            createdCategory = categoryFacade.createExpenseCategory(categoryName);
        }
        System.out.println("Создана категория: " + categoryName + " (" + type + ")");
    }

    /**
     * Отмена.
     */
    @Override
    public void undo() {
        if (createdCategory != null) {
            categoryFacade.delete(createdCategory.getId());
            System.out.println("Отменено создание категории: " + categoryName);
            createdCategory = null;
        }
    }

    /**
     * Получение описания.
     */
    @Override
    public String getDescription() {
        return "Создание категории: " + categoryName + " (" + type + ")";
    }
}