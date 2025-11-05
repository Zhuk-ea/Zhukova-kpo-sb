package hsebank.command;

import hsebank.domain.Category;
import hsebank.domain.OperationType;
import hsebank.facades.CategoryFacade;
import hsebank.interfaces.Command;
import java.util.Optional;


/**
 * Команда обновлениея категории.
 */
public class UpdateCategoryCommand implements Command {
    private final CategoryFacade categoryFacade;
    private final Long categoryId;
    private final String newName;
    private final OperationType newType;
    private Category originalCategory;

    /**
     * Конструктор.
     */
    public UpdateCategoryCommand(CategoryFacade categoryFacade, Long categoryId,
                                 String newName, OperationType newType) {
        this.categoryFacade = categoryFacade;
        this.categoryId = categoryId;
        this.newName = newName;
        this.newType = newType;
    }


    /**
     * Обновление категории.
     */
    @Override
    public void execute() {
        Optional<Category> categoryOpt = categoryFacade.getById(categoryId);
        if (categoryOpt.isPresent()) {
            originalCategory = categoryOpt.get();
            Category updatedCategory = new Category(categoryId, newType, newName);
            categoryFacade.update(categoryId, updatedCategory);
            System.out.println("Обновлена категория: " + newName + " (" + newType + ")");
        }
    }

    /**
     * Отмена.
     */
    @Override
    public void undo() {
        if (originalCategory != null) {
            categoryFacade.update(categoryId, originalCategory);
            System.out.println("Отменено обновление категории, восстановлено: " + originalCategory.getName());
        }
    }

    /**
     * Описание.
     */
    @Override
    public String getDescription() {
        return "Обновление категории ID: " + categoryId + " -> имя: " + newName + ", тип: " + newType;
    }
}