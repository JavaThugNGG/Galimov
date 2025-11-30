package hms.interfaces;

import java.util.List;

public interface DataAccessObject<T, ID> {

    boolean save(T entity);

    T findById(ID id);

    List<T> findAll();

    List<T> findByProperty(String propertyName, Object value);

    boolean update(T entity);

    boolean delete(ID id);

    boolean exists(ID id);

    long count();
}

