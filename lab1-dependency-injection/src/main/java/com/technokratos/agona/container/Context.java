package com.technokratos.agona.container;

import com.technokratos.agona.annotations.Component;
import com.technokratos.agona.annotations.Endpoint;
import com.technokratos.agona.annotations.Get;
import com.technokratos.agona.annotations.Wire;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class Context {

    private final Map<Class<?>, Object> componentContainer;
    private final Map<Class<?>, Object> endpointsContrainer;
    private final String packageScan;

    public Context(String packageScan) {
        this.componentContainer = new HashMap<>();
        this.endpointsContrainer = new HashMap<>();
        this.packageScan = packageScan;
    }

    public Object takeComponent(final String className) {
        return componentContainer.keySet().stream()
                .filter(c -> c.getSimpleName().equals(className))
                .map(componentContainer::get)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("There is no component with provided className=%s".formatted(className)));
    }

    public Object takeComponent(final Class<?> clazz) {
        return componentContainer.get(clazz);
    }

    public void componentScan() {
        val reflections = new Reflections(packageScan);
        reflections.getTypesAnnotatedWith(Component.class)
                   .forEach(c -> this.componentContainer.put(c, firstPhaseConstructor(c)));
    }

    @SneakyThrows
    public Object firstPhaseConstructor(final Class<?> componentClass) {
        val constructor = componentClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    public void setupComponents() {
        this.componentContainer.keySet()
                .forEach(c -> {
                    List.of(c.getDeclaredFields()).forEach(f -> {
                        if (f.isAnnotationPresent(Wire.class)) {
                            if (this.componentContainer.containsKey(f.getType())) {
                                try {
                                    f.setAccessible(true);
                                    f.set(takeComponent(c), takeComponent(f.getType()));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });
                });

        fillEndpoints();
    }

    private void fillEndpoints() {
        this.componentContainer.keySet()
                .forEach(c -> {
                    if (c.isAnnotationPresent(Endpoint.class)) {
                        this.endpointsContrainer.put(c, takeComponent(c));
                    }
                });
    }

    public void clear() {
        this.componentContainer.clear();
    }
}
