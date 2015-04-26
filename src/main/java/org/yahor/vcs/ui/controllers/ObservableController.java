package org.yahor.vcs.ui.controllers;

import org.yahor.vcs.ui.events.RepoEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author yahor-filipchyk
 */
public class ObservableController {

    private Map<Class<? extends RepoEvent>, List<Consumer>> listeners = new HashMap<>();

    public <T extends RepoEvent> void addListener(Class<T> eventClass, Consumer<T> listener) {
        if (listeners.containsKey(eventClass)) {
            listeners.get(eventClass).add(listener);
        } else {
            List<Consumer> listenersList = new LinkedList<>();
            listenersList.add(listener);
            listeners.put(eventClass, listenersList);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends RepoEvent> void notifyListeners(T event) {
        List<Consumer> listeners = this.listeners.get(event.getClass());
        if (listeners != null) {
            listeners.forEach(listener -> listener.accept(event));
        }
    }
}
