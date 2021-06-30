import { hasModuleAccess } from './auth.authority';

/*
 * Public API Surface of auth
 */
export * from './auth.module';
export * from './auth-http-interceptor';
export * from './auth.config';
export * from './auth.enum';
export { SgiAuthService, IAuthStatus } from './auth.service';
export { SgiAuthRoute, SgiAuthRoutes } from './auth.route';
export * from './auth.guard';
export * from './directives/has-any-authority-for-any-uo.directive';
export * from './directives/has-any-authority.directive';
export * from './directives/has-authority-for-any-uo.directive';
export * from './directives/has-authority.directive';
export * from './directives/if-authenticated.directive';
export * from './directives/has-module-access.directive';
export * from './directives/has-any-module-access.directive';
