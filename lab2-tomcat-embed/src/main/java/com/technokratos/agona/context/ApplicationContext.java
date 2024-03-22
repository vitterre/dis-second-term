package com.technokratos.agona.context;

import com.technokratos.agona.annotations.Component;
import com.technokratos.agona.annotations.Inject;
import lombok.val;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ApplicationContext {

    private Map<Class<?>, Object> container;
    private String packageScanPath;

    public ApplicationContext(final Map<Class<?>, Object> container, String packageScanPath) {
        this.container = container;
        this.packageScanPath = packageScanPath;
    }

    public Set<Class<?>> componentScan() {
        val reflections = new Reflections(packageScanPath, new SubTypesScanner(false));

        return reflections.getSubTypesOf(Object.class);
    }

    public void createComponentInstances(final Set<Class<?>> componentClasses) {
        componentClasses.stream()
                .filter(clazz -> Objects.nonNull(clazz.getAnnotation(Component.class)))
                .forEach(clazz -> {
                    try {
                        val instance = createInstanceByComponentClass(clazz);
                        container.put(clazz, instance);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void setupComponents() {
        val classes = container.keySet();
        classes.forEach(clazz -> {
                    val fields = List.of(clazz.getDeclaredFields());
                    fields.forEach(field -> {
                        if (field.isAnnotationPresent(Inject.class)) {
                            val dependency = field.getType();

                            if (container.containsKey(dependency)) {
                                try {
                                    field.setAccessible(true);
                                    field.set(container.get(clazz), container.get(dependency));
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });
                });
    }

    private Object createInstanceByComponentClass(final Class<?> componentClass) throws Exception {
        val constructor = componentClass.getConstructor();
        constructor.setAccessible(true);

        return constructor.newInstance();
    }


}
