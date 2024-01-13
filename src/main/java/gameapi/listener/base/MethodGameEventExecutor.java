package gameapi.listener.base;

import gameapi.event.RoomEvent;
import gameapi.listener.base.exceptions.GameEventException;
import gameapi.listener.base.interfaces.GameEventExecutor;
import gameapi.listener.base.interfaces.GameListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodGameEventExecutor implements GameEventExecutor {
    private final Method method;

    public MethodGameEventExecutor(Method method) {
        this.method = method;
    }

    public void execute(GameListener listener, RoomEvent event) throws GameEventException {
        try {
            this.method.setAccessible(true);
            if (this.method.getParameterTypes().length == 1) {
                if (event.getClass().isAssignableFrom(this.method.getParameterTypes()[0])) {
                    this.method.invoke(listener, event);
                }
            }
        } catch (InvocationTargetException | ClassCastException | IllegalArgumentException ite) {
            throw new GameEventException(ite.getCause() + "/" + listener.getClass().getName() + "/" + event.getEventName() + "/" + this.method.getParameterTypes()[0].getName());
        } catch (Throwable throwable) {
            throw new GameEventException(throwable);
        }

    }

    public Method getMethod() {
        return this.method;
    }
}