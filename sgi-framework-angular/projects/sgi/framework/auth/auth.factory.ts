import { PlatformLocation } from '@angular/common';
import { Router } from '@angular/router';
import { SgiAuthMode } from './auth.enum';
import { AuthInMemoryService } from './auth.inmemory.service';
import { AuthKeycloakService } from './auth.keycloak.service';
import { SgiAuthService } from './auth.service';

/**
 * Factory for SgiAuthService. Buils the implementation based on configured SgiAuthMode
 *
 * @param router The router to use
 * @param platformLocation The platformLocation to use
 */
export function authFactory(mode: SgiAuthMode, router: Router, platformLocation: PlatformLocation): SgiAuthService {
  switch (mode) {
    case SgiAuthMode.Keycloak:
      return new AuthKeycloakService(router, platformLocation);
    case SgiAuthMode.InMemory:
      return new AuthInMemoryService(router);
  }
}
