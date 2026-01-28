package dev.quatern.marketplace.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public abstract class BaseService<T> {

    @SuppressWarnings("unchecked")
    protected JpaRepository<T, String> getRepository() {
        Class<T> entityClass = resolveEntityClass();
        String entityName = entityClass.getSimpleName();
        String fieldName = Character.toLowerCase(entityName.charAt(0)) + entityName.substring(1) + "Repository";
        try {
            Field field = getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (JpaRepository<T, String>) field.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(
                "Repository field \"" + fieldName + "\" was not found in class " + getClass().getSimpleName()
            );
        }
    }

    @SuppressWarnings("unchecked")
    private Class<T> resolveEntityClass() {
        Class<?> clazz = getClass();
        while (clazz.getName().contains("$$"))
            clazz = clazz.getSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    public T findById(String id) {
        return getRepository().findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Entity with id " + id + " was not found"
            ));
    }

    public T save(T entity) {
        return getRepository().save(entity);
    }

}
