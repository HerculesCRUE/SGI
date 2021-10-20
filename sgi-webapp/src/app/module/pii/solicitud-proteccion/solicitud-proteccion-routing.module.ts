import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SolicitudProteccionCrearComponent } from './solicitud-proteccion-crear/solicitud-proteccion-crear.component';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SolicitudProteccionDataResolver, SOLICITUD_PROTECCION_DATA_KEY } from './solicitud-proteccion-data.resolver';
import { SOLICITUD_PROTECCION_ROUTE_NAMES } from './solicitud-proteccion-route-names';
import { SolicitudProteccionDatosGeneralesComponent } from './solicitud-proteccion-formulario/solicitud-proteccion-datos-generales/solicitud-proteccion-datos-generales.component';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { SOLICITUD_PROTECCION_ROUTE_PARAMS } from './solicitud-proteccion-route-params';
import { SolicitudProteccionEditarComponent } from './solicitud-proteccion-editar/solicitud-proteccion-editar.component';
import { RouterModule } from '@angular/router';

const MSG_NEW_TITLE = marker('title.new.entity');
const SOLICITUD_PROTECCION_KEY = marker('pii.solicitud-proteccion');

const routes: SgiRoutes = [
  {
    path: `${ROUTE_NAMES.NEW}`,
    component: SolicitudProteccionCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: SOLICITUD_PROTECCION_KEY, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthority: 'PII-INV-C'
    },
    resolve: {
      [SOLICITUD_PROTECCION_DATA_KEY]: SolicitudProteccionDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SOLICITUD_PROTECCION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: SOLICITUD_PROTECCION_ROUTE_NAMES.DATOS_GENERALES,
        component: SolicitudProteccionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${SOLICITUD_PROTECCION_ROUTE_PARAMS.ID}`,
    component: SolicitudProteccionEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: SOLICITUD_PROTECCION_KEY,
      hasAuthority: 'PII-INV-E'
    },
    resolve: {
      [SOLICITUD_PROTECCION_DATA_KEY]: SolicitudProteccionDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SOLICITUD_PROTECCION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: SOLICITUD_PROTECCION_ROUTE_NAMES.DATOS_GENERALES,
        component: SolicitudProteccionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SolicitudProteccionRoutingModule { }
