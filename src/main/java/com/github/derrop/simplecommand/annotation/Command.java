package com.github.derrop.simplecommand.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    String[] aliases();

    String permission() default "";

    String description() default "";

    String prefix() default "";

    boolean consoleOnly() default false;

}
