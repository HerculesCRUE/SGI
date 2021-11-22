package org.crue.hercules.sgi.framework.test.context.support;

import java.util.Map;

import org.springframework.test.context.support.DefaultActiveProfilesResolver;

import lombok.extern.slf4j.Slf4j;

/**
 * An {@link org.springframework.test.context.ActiveProfilesResolver} that looks
 * up for SPRING_PROFILES_ACTIVE and spring.profiles.active environment variable
 * or spring.profiles.active system property to set the active profile. If not
 * found, the "test" profile is set.
 */
@Slf4j
public class SgiTestProfileResolver extends DefaultActiveProfilesResolver {
  /**
   * SPRING_PROFILES_ACTIVE
   */
  public static final String SPRING_PROFILES_ACTIVE_ENV_VAR = "SPRING_PROFILES_ACTIVE";
  /**
   * spring.profiles.active
   */
  public static final String SPRING_PROFILES_ACTIVE_KEY = "spring.profiles.active";

  private static final String MESSAGE_FOUND = "Found {}={}!";

  @Override
  public String[] resolve(Class<?> testClass) {
    log.debug("resolve(Class<?> testClass) - start");
    String[] profiles = super.resolve(testClass);
    if (profiles.length == 0) {
      Map<String, String> env = System.getenv();
      log.info("Looking up active spring profiles in environment...");
      String profile;
      if (env.containsKey(SPRING_PROFILES_ACTIVE_ENV_VAR)) {
        profile = env.get(SPRING_PROFILES_ACTIVE_ENV_VAR);
        log.info(MESSAGE_FOUND, SPRING_PROFILES_ACTIVE_ENV_VAR, profile);
      } else {
        if (env.containsKey(SPRING_PROFILES_ACTIVE_KEY)) {
          profile = env.get(SPRING_PROFILES_ACTIVE_KEY);
          log.info(MESSAGE_FOUND, SPRING_PROFILES_ACTIVE_KEY, profile);
        } else {
          log.info("Looking up active spring profiles in system...");
          profile = System.getProperty(SPRING_PROFILES_ACTIVE_KEY);
          if (profile == null) {
            profile = "test";
            log.info("Not found.  Setting default active profile to test");
          } else {
            log.info(MESSAGE_FOUND, SPRING_PROFILES_ACTIVE_KEY, profile);
          }
        }
      }
      profiles = new String[] { profile };
    }
    log.debug("resolve(Class<?> testClass) - end");
    return profiles;
  }

}
