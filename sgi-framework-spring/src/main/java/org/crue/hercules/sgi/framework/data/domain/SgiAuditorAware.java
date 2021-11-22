package org.crue.hercules.sgi.framework.data.domain;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom {@link AuditorAware} using the current {@link Authentication} name.
 */
@Slf4j
public class SgiAuditorAware implements AuditorAware<String> {
  @Override
  public Optional<String> getCurrentAuditor() {
    log.debug("getCurrentAuditor() - start");
    // Use Spring Security to return currently logged in user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      log.debug("getCurrentAuditor() - end");
      return Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
    } else {
      log.info("No Authentication info available");
      log.debug("getCurrentAuditor() - end");
      return Optional.empty();
    }
  }
}