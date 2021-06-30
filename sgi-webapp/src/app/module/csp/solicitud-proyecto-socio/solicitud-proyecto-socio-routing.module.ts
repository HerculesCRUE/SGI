import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { SolicitudProyectoSocioCrearComponent } from './solicitud-proyecto-socio-crear/solicitud-proyecto-socio-crear.component';
import { SolicitudProyectoSocioDataResolver, SOLICITUD_PROYECTO_SOCIO_DATA_KEY } from './solicitud-proyecto-socio-data.resolver';
import { SolicitudProyectoSocioEditarComponent } from './solicitud-proyecto-socio-editar/solicitud-proyecto-socio-editar.component';
import { SolicitudProyectoSocioDatosGeneralesComponent } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-datos-generales/solicitud-proyecto-socio-datos-generales.component';
import { SolicitudProyectoSocioEquipoComponent } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-equipo/solicitud-proyecto-socio-equipo.component';
import { SolicitudProyectoSocioPeriodoJustificacionComponent } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-periodo-justificacion/solicitud-proyecto-socio-periodo-justificacion.component';
import { SolicitudProyectoSocioPeriodoPagoComponent } from './solicitud-proyecto-socio-formulario/solicitud-proyecto-socio-periodo-pago/solicitud-proyecto-socio-periodo-pago.component';
import { SOLICITUD_PROYECTO_SOCIO_ROUTE_NAMES } from './solicitud-proyecto-socio-route-names';
import { SOLICITUD_PROYECTO_SOCIO_ROUTE_PARAMS } from './solicitud-proyecto-socio-route-params';

const MSG_NEW_TITLE = marker('title.new.entity');
const MSG_EDIT_TITLE = marker('csp.solicitud-proyecto-socio');

const routes: SgiRoutes = [
  {
    path: ROUTE_NAMES.NEW,
    component: SolicitudProyectoSocioCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: MSG_EDIT_TITLE, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      }
    },
    resolve: {
      [SOLICITUD_PROYECTO_SOCIO_DATA_KEY]: SolicitudProyectoSocioDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SOLICITUD_PROYECTO_SOCIO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: SOLICITUD_PROYECTO_SOCIO_ROUTE_NAMES.DATOS_GENERALES,
        component: SolicitudProyectoSocioDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PROYECTO_SOCIO_ROUTE_NAMES.PERIODOS_PAGOS,
        component: SolicitudProyectoSocioPeriodoPagoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PROYECTO_SOCIO_ROUTE_NAMES.PERIODOS_JUSTIFICACION,
        component: SolicitudProyectoSocioPeriodoJustificacionComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PROYECTO_SOCIO_ROUTE_NAMES.EQUIPO,
        component: SolicitudProyectoSocioEquipoComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${SOLICITUD_PROYECTO_SOCIO_ROUTE_PARAMS.ID}`,
    component: SolicitudProyectoSocioEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_EDIT_TITLE
    },
    resolve: {
      [SOLICITUD_PROYECTO_SOCIO_DATA_KEY]: SolicitudProyectoSocioDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SOLICITUD_PROYECTO_SOCIO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: SOLICITUD_PROYECTO_SOCIO_ROUTE_NAMES.DATOS_GENERALES,
        component: SolicitudProyectoSocioDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PROYECTO_SOCIO_ROUTE_NAMES.PERIODOS_PAGOS,
        component: SolicitudProyectoSocioPeriodoPagoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PROYECTO_SOCIO_ROUTE_NAMES.PERIODOS_JUSTIFICACION,
        component: SolicitudProyectoSocioPeriodoJustificacionComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PROYECTO_SOCIO_ROUTE_NAMES.EQUIPO,
        component: SolicitudProyectoSocioEquipoComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SolicitudProyectoSocioRouting {
}
