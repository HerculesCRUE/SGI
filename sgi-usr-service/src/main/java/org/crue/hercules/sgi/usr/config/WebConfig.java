package org.crue.hercules.sgi.usr.config;

import org.crue.hercules.sgi.framework.problem.spring.boot.autoconfigure.EnableProblem;
import org.crue.hercules.sgi.framework.spring.context.support.boot.autoconfigure.EnableApplicationContextSupport;
import org.crue.hercules.sgi.framework.web.config.SgiWebConfig;
import org.springframework.context.annotation.Configuration;

/**
 * SgiWebConfig Fw
 * 
 * Framework Web configuration.
 */
@Configuration
@EnableProblem
@EnableApplicationContextSupport
public class WebConfig extends SgiWebConfig {

}