import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SgiAuthGuard } from '@sgi/framework/auth';
import { RootComponent } from '@shared/root/root.component';
import { APP_ROUTE_NAMES } from './app-route-names';
import { SgiRoutes } from '@core/route';
import { AllowModuleGuard } from '@core/guards/allow-module.guard';
import { HomeComponent } from './home/home.component';

/**
 * Definimos las urls de la aplicación
 */
const routes: SgiRoutes = [
  {
    path: '',
    pathMatch: 'full',
    component: HomeComponent,
    canActivate: [SgiAuthGuard]
  },
  {
    path: APP_ROUTE_NAMES.CSP,
    loadChildren: () =>
      import('./module/csp/csp.module').then(
        (m) => m.CspModule
      ),
    canActivate: [SgiAuthGuard, AllowModuleGuard]
  },
  {
    path: APP_ROUTE_NAMES.ETI,
    loadChildren: () =>
      import('./module/eti/eti.module').then(
        (m) => m.EtiModule
      ),
    canActivate: [SgiAuthGuard, AllowModuleGuard]
  },
  {
    path: APP_ROUTE_NAMES.INV,
    loadChildren: () =>
      import('./module/inv/inv.module').then(
        (m) => m.InvModule
      ),
    canActivate: [SgiAuthGuard, AllowModuleGuard]
  },
  {
    path: APP_ROUTE_NAMES.PII,
    loadChildren: () =>
      import('./module/pii/pii.module').then(
        (m) => m.PiiModule
      ),
    canActivate: [SgiAuthGuard, AllowModuleGuard]
  },
  {
    path: '**',
    component: RootComponent
  },
];

/**
 * Módulo para definir las url de entrada de la aplicación
 */
@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
