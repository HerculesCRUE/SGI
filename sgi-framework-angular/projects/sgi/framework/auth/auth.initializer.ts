import { SgiAuthService } from './auth.service';
import { SgiAuthConfig } from './auth.config';

/**
 * Application initializer function to initialize AuthService during application bootstrap
 *
 * @param authService AuthService to initialize
 */
export function authInitializer(authService: SgiAuthService, authConfig: SgiAuthConfig): () => Promise<boolean> {
  return () => authService.init(authConfig).toPromise();
}
