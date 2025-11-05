package hsebank.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория.
 */
public interface Repository<T, I> {

    T save(T entity);

    Optional<T> findById(I id);

    List<T> findAll();

    void delete(I id);

    boolean existsById(I id);
}