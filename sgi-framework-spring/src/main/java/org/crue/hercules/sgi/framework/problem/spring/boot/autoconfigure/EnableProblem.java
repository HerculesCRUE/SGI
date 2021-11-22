package org.crue.hercules.sgi.framework.problem.spring.boot.autoconfigure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Enables {@link org.crue.hercules.sgi.framework.problem.Problem} auto
 * configuration.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ProblemAutoConfiguration.class)
public @interface EnableProblem {

}
