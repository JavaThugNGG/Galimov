package hms.interfaces;

import java.util.List;

public interface ManagementService<T, ID> {
    // Basic CRUD operations
    boolean add(T entity);

    T getById(ID id);

    List<T> getAll();

    boolean update(T entity);

    boolean delete(ID id);

    List<T> search(String query);
}
