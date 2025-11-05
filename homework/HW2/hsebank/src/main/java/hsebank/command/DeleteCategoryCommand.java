package hsebank.command;

import hsebank.domain.Category;
import hsebank.facades.CategoryFacade;
import hsebank.interfaces.Command;
import java.util.Optional;



/**
 * Команда удаления категории.
 */
public class DeleteCategoryCommand implements Command {
    private final CategoryFacade categoryFacade;
    private final Long categoryId;
    private Category deletedCategory;

    /**
     * Конструктор.
     */
    public DeleteCategoryCommand(CategoryFacade categoryFacade, Long categoryId) {
        this.categoryFacade = categoryFacade;
        this.categoryId = categoryId;
    }

    /**
     * Удаление категории.
     */
    @Override
    public void execute() {
        Optional<Category> categoryOpt = categoryFacade.getById(categoryId);
        if (categoryOpt.isPresent()) {
            deletedCategory = categoryOpt.get();
            categoryFacade.delete(categoryId);
            System.out.println("Удалена категория: " + deletedCategory.getName());
        }
    }

    /**
     * Отмена.
     */
    @Override
    public void undo() {
        if (deletedCategory != null) {
            categoryFacade.create(deletedCategory);
            System.out.println("Восстановлена категория: " + deletedCategory.getName());
            deletedCategory = null;
        }
    }

    /**
     * Описание.
     */
    @Override
    public String getDescription() {
        return "Удаление категории ID: " + categoryId;
    }
}