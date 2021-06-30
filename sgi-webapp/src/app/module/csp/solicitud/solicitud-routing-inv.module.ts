import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { SolicitudCrearComponent } from './solicitud-crear/solicitud-crear.component';
import { SolicitudCrearGuard } from './solicitud-crear/solicitud-crear.guard';
import { SolicitudDataResolver, SOLICITUD_DATA_KEY } from './solicitud-data.resolver';
import { SolicitudEditarComponent } from './solicitud-editar/solicitud-editar.component';
import { SolicitudDatosGeneralesComponent } from './solicitud-formulario/solicitud-datos-generales/solicitud-datos-generales.component';
import { SolicitudListadoInvComponent } from './solicitud-listado-inv/solicitud-listado-inv.component';
import { SOLICITUD_ROUTE_NAMES } from './solicitud-route-names';
import { SOLICITUD_ROUTE_PARAMS } from './solicitud-route-params';

const SOLICITUD_KEY = marker('csp.solicitud');
const MSG_NEW_TITLE = marker('title.new.entity');
const MSG_SOLICITUD_TITLE = marker('inv.solicitud.listado.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: SolicitudListadoInvComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_SOLICITUD_TITLE,
      hasAnyAuthority: ['CSP-SOL-INV-ER', 'CSP-SOL-INV-BR'],
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: SolicitudCrearComponent,
    canActivate: [SgiAuthGuard, SolicitudCrearGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: SOLICITUD_KEY, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SOLICITUD_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: SOLICITUD_ROUTE_NAMES.DATOS_GENERALES,
        component: SolicitudDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${SOLICITUD_ROUTE_PARAMS.ID}`,
    component: SolicitudEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      [SOLICITUD_DATA_KEY]: SolicitudDataResolver
    },
    data: {
      title: SOLICITUD_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthority: ['CSP-SOL-INV-ER', 'CSP-SOL-INV-BR'],
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SOLICITUD_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: SOLICITUD_ROUTE_NAMES.DATOS_GENERALES,
        component: SolicitudDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SolicitudRoutingInvModule {
}
