import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { SeguimientoJustificacionRequerimientoCrearComponent } from './seguimiento-justificacion-requerimiento-crear/seguimiento-justificacion-requerimiento-crear.component';
import { SeguimientoJustificacionRequerimientoDataResolver, REQUERIMIENTO_JUSTIFICACION_DATA_KEY } from './seguimiento-justificacion-requerimiento-data.resolver';
import { SeguimientoJustificacionRequerimientoEditarComponent } from './seguimiento-justificacion-requerimiento-editar/seguimiento-justificacion-requerimiento-editar.component';
import { SeguimientoJustificacionRequerimientoDatosGeneralesComponent } from './seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-datos-generales/seguimiento-justificacion-requerimiento-datos-generales.component';
import { SeguimientoJustificacionRequerimientoGastosComponent } from './seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-gastos/seguimiento-justificacion-requerimiento-gastos.component';
import { SeguimientoJustificacionRequerimientoRespuestaAlegacionComponent } from './seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-respuesta-alegacion/seguimiento-justificacion-requerimiento-respuesta-alegacion.component';
import { SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_NAMES } from './seguimiento-justificacion-requerimiento-route-names';
import { SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_PARAMS } from './seguimiento-justificacion-requerimiento-route-params';

const MSG_NEW_TITLE = marker('title.new.entity');
const REQUERIMIENTO_JUSTIFICACION_KEY = marker('csp.requerimiento-justificacion');

const routes: SgiRoutes = [
  {
    path: `${ROUTE_NAMES.NEW}`,
    component: SeguimientoJustificacionRequerimientoCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: REQUERIMIENTO_JUSTIFICACION_KEY, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAnyAuthorityForAnyUO: ['CSP-SJUS-E', 'CSP-SJUS-V']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_NAMES.DATOS_GENERALES,
        component: SeguimientoJustificacionRequerimientoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  },
  {
    path: `:${SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_PARAMS.ID}`,
    component: SeguimientoJustificacionRequerimientoEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: REQUERIMIENTO_JUSTIFICACION_KEY,
      hasAnyAuthorityForAnyUO: ['CSP-SJUS-E', 'CSP-SJUS-V']
    },
    resolve: {
      [REQUERIMIENTO_JUSTIFICACION_DATA_KEY]: SeguimientoJustificacionRequerimientoDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_NAMES.DATOS_GENERALES,
        component: SeguimientoJustificacionRequerimientoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_NAMES.GASTOS,
        component: SeguimientoJustificacionRequerimientoGastosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_NAMES.RESPUESTA_ALEGACION,
        component: SeguimientoJustificacionRequerimientoRespuestaAlegacionComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SeguimientoJustificacionRequerimientoRouting {
}
