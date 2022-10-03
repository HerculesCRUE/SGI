import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { Module } from '@core/module';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { AutorizacionDataResolver, AUTORIZACION_DATA_KEY } from './autorizacion-data.resolver';
import { AutorizacionEditarComponent } from './autorizacion-editar/autorizacion-editar.component';
import { AutorizacionCertificadosComponent } from './autorizacion-formulario/autorizacion-certificados/autorizacion-certificados.component';
import { AutorizacionDatosGeneralesComponent } from './autorizacion-formulario/autorizacion-datos-generales/autorizacion-datos-generales.component';
import { AutorizacionHistoricoEstadosComponent } from './autorizacion-formulario/autorizacion-historico-estados/autorizacion-historico-estados.component';
import { AutorizacionListadoComponent } from './autorizacion-listado/autorizacion-listado.component';
import { AUTORIZACION_ROUTE_NAMES } from './autorizacion-route-names';
import { AUTORIZACION_ROUTE_PARAMS } from './autorizacion-route-params';

const AUTORIZACION_TITLE_KEY = marker('csp.autorizacion-proyecto-externo');
const routes: SgiRoutes = [
  {
    path: '',
    component: AutorizacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: AUTORIZACION_TITLE_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['CSP-AUT-E', 'CSP-AUT-V', 'CSP-AUT-B'],
      module: Module.CSP
    }
  },
  {
    path: `:${AUTORIZACION_ROUTE_PARAMS.ID}`,
    component: AutorizacionEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      [AUTORIZACION_DATA_KEY]: AutorizacionDataResolver
    },
    data: {
      title: AUTORIZACION_TITLE_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['CSP-AUT-E', 'CSP-AUT-V'],
      module: Module.CSP
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: AUTORIZACION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: AUTORIZACION_ROUTE_NAMES.DATOS_GENERALES,
        component: AutorizacionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard],
      },
      {
        path: AUTORIZACION_ROUTE_NAMES.HISTORICO_ESTADOS,
        component: AutorizacionHistoricoEstadosComponent,
        canDeactivate: [FragmentGuard],
      },
      {
        path: AUTORIZACION_ROUTE_NAMES.CERTIFICADOS,
        component: AutorizacionCertificadosComponent,
        canDeactivate: [FragmentGuard],
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AutorizacionRoutingModule { }
