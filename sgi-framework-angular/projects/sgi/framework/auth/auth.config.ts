import { SgiAuthMode } from './auth.enum';
import { IAuthStatus } from './auth.service';
import { InjectionToken } from '@angular/core';

export const SGI_AUTH_CONFIG = new InjectionToken<SgiAuthConfig>('sgi.auth.config');

export interface SgiAuthConfig {
  mode: SgiAuthMode;
  ssoRealm: string;
  ssoClientId: string;
  ssoUrl: string;
  inMemoryConfig: IAuthStatus;
  protectedResources: RegExp[];
}
