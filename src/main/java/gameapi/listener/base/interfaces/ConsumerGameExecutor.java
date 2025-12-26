package gameapi.listener.base.interfaces;

import cn.nukkit.event.EventPriority;

import java.util.function.Consumer;

public class ConsumerGameExecutor {

    private final Consumer<?> consumer;

    private final EventPriority priority;

    public ConsumerGameExecutor(Consumer<?> consumer, EventPriority priority) {
        this.consumer = consumer;
        this.priority = priority;
    }

    public EventPriority getPriority() {
        return priority;
    }

    public Consumer<?> getConsumer() {
        return consumer;
    }
}