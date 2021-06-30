import { PlatformLocation } from '@angular/common';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { APP_INITIALIZER, ModuleWithProviders, NgModule } from '@angular/core';
import { Router } from '@angular/router';
import { SgiAuthHttpInterceptor } from './auth-http-interceptor';
import { SGI_AUTH_CONFIG } from './auth.config';
import { SgiAuthMode } from './auth.enum';
import { authFactory } from './auth.factory';
import { authInitializer } from './auth.initializer';
import { SgiAuthService } from './auth.service';
import { HasAnyAuthorityForAnyUODirective } from './directives/has-any-authority-for-any-uo.directive';
import { HasAnyAuthorityDirective } from './directives/has-any-authority.directive';
import { HasAnyModuleAccessDirective } from './directives/has-any-module-access.directive';
import { HasAuthorityForAnyUODirective } from './directives/has-authority-for-any-uo.directive';
import { HasAuthorityDirective } from './directives/has-authority.directive';
import { HasModuleAccessDirective } from './directives/has-module-access.directive';
import { IfAuthenticatedDirective } from './directives/if-authenticated.directive';


@NgModule({
  declarations: [
    HasAuthorityForAnyUODirective,
    IfAuthenticatedDirective,
    HasAnyAuthorityForAnyUODirective,
    HasAuthorityDirective,
    HasAnyAuthorityDirective,
    HasModuleAccessDirective,
    HasAnyModuleAccessDirective
  ],
  exports: [
    HasAuthorityForAnyUODirective,
    IfAuthenticatedDirective,
    HasAnyAuthorityForAnyUODirective,
    HasAuthorityDirective,
    HasAnyAuthorityDirective,
    HasModuleAccessDirective,
    HasAnyModuleAccessDirective
  ]
})
export class SgiAuthModule {
  static forRoot(): ModuleWithProviders<SgiAuthModule> {
    return {
      ngModule: SgiAuthModule,
      providers: [
        {
          provide: SgiAuthService,
          useFactory: authFactory,
          deps: [SgiAuthMode, Router, PlatformLocation],
        },
        {
          provide: APP_INITIALIZER,
          useFactory: authInitializer,
          multi: true,
          deps: [SgiAuthService, SGI_AUTH_CONFIG],
        },
        {
          provide: HTTP_INTERCEPTORS,
          useClass: SgiAuthHttpInterceptor,
          multi: true
        }
      ]
    };
  }
}
