package hsebank.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс фасада.
 */
public interface Facade<T, I> {

    T create(T entity);

    Optional<T> getById(I id);

    List<T> getAll();

    Optional<T> update(I id, T entity);

    boolean delete(I id);

    boolean exists(I id);

}