package com.github.derrop.simplecommand.annotation;

import com.github.derrop.simplecommand.argument.ArgumentRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubCommand {

    String[] args();

    String[] preHandlers() default {};

    String[] postHandlers() default {};

    String extendedUsage() default "";

    String permission() default "";

    String description() default "";

    boolean consoleOnly() default false;

    boolean enableProperties() default false;

    boolean async() default false;

    /**
     * Whether a "..." should be shown at the end or not.
     * It is only shown when {@link #requirement()} is also {@link ArgumentRequirement#EXACT}.
     */
    boolean showMinArgsIndicator() default true;

    ArgumentRequirement requirement() default ArgumentRequirement.EXACT;

    int maxArgs() default -1;

}
