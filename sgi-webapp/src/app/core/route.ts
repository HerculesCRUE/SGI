import { SgiAuthRoute } from '@sgi/framework/auth';
import { SgiAuthRouteData } from '@sgi/framework/auth/auth.route';

/**
 * SGI route data with autorization and title
 */
export declare type SgiRouteData = SgiAuthRouteData & {
  /** Title of the route */
  title?: string
};

/**
 * Route with authorization and title support
 */
export interface SgiRoute extends SgiAuthRoute {
  data?: SgiRouteData;
  children?: SgiRoutes;
}

/**
 * Routes with authorization and title support
 */
export declare type SgiRoutes = SgiRoute[];
