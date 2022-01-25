import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { AutorizacionCrearComponent } from './autorizacion-crear/autorizacion-crear.component';
import { AutorizacionDataResolver, AUTORIZACION_DATA_KEY } from './autorizacion-data.resolver';
import { AutorizacionEditarComponent } from './autorizacion-editar/autorizacion-editar.component';
import { AutorizacionDatosGeneralesComponent } from './autorizacion-formulario/autorizacion-datos-generales/autorizacion-datos-generales.component';
import { AutorizacionHistoricoEstadosComponent } from './autorizacion-formulario/autorizacion-historico-estados/autorizacion-historico-estados.component';
import { AutorizacionListadoComponent } from './autorizacion-listado/autorizacion-listado.component';
import { AUTORIZACION_ROUTE_NAMES } from './autorizacion-route-names';
import { AUTORIZACION_ROUTE_PARAMS } from './autorizacion-route-params';

const AUTORIZACION_TITLE_KEY = marker('csp.autorizacion-proyecto-externo');
const MSG_NEW_TITLE = marker('title.new.entity');
const routes: SgiRoutes = [
  {
    path: '',
    component: AutorizacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: AUTORIZACION_TITLE_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['CSP-AUT-E', 'CSP-AUT-V', 'CSP-AUT-B', 'CSP-AUT-INV-C', 'CSP-AUT-INV-ER', 'CSP-AUT-INV-BR'],
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: AutorizacionCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: AUTORIZACION_TITLE_KEY, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR,
      },
      hasAnyAuthorityForAnyUO: ['CSP-AUT-INV-C', 'CSP-AUT-INV-ER', 'CSP-AUT-INV-BR']
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
    ]
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
      hasAnyAuthorityForAnyUO: ['CSP-AUT-E', 'CSP-AUT-INV-C', 'CSP-AUT-INV-ER', 'CSP-AUT-INV-BR'],
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
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AutorizacionRoutingModule { }
