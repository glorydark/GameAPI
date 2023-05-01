package gameapi.listener.base.annotations;

import cn.nukkit.event.EventPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GameEventHandler {
    EventPriority priority() default EventPriority.NORMAL;

    boolean ignoreCancelled() default false;
}
