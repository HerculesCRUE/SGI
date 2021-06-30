import { Route } from '@angular/router';

/**
 * Authorization route data
 */
export declare type SgiAuthRouteData = {
  /** Validates that the user has the authority */
  hasAuthority?: string;
  /** Validates that the user has at least one of the authorities */
  hasAnyAuthority?: string[];
  /** Validates that the user has the authority, ignoring the UO suffix */
  hasAuthorityForAnyUO?: string;
  /** Validates that the user hast at least one of the authorities, ignoring the UO suffix */
  hasAnyAuthorityForAnyUO?: string[];
  [name: string]: any;
};

/**
 * Route with authorization support
 */
export interface SgiAuthRoute extends Route {
  data?: SgiAuthRouteData;
  children?: SgiAuthRoutes;
}

/**
 * Routes with authorization support
 */
export declare type SgiAuthRoutes = SgiAuthRoute[];
