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
            for (Class param : this.method.getParameterTypes()) {
                if (event.getClass().isAssignableFrom(param)) {
                    this.method.invoke(listener, event);
                }
            }
        } catch (InvocationTargetException ite) {
            throw new GameEventException(ite.getCause());
        } catch (ClassCastException ignored) {
        } catch (Throwable throwable) {
            throw new GameEventException(throwable);
        }

    }

    public Method getMethod() {
        return this.method;
    }
}