import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ProyectoSocioCrearComponent } from './proyecto-socio-crear/proyecto-socio-crear.component';
import { ProyectoSocioDataResolver, PROYECTO_SOCIO_DATA_KEY } from './proyecto-socio-data.resolver';
import { ProyectoSocioEditarComponent } from './proyecto-socio-editar/proyecto-socio-editar.component';
import { ProyectoSocioDatosGeneralesComponent } from './proyecto-socio-formulario/proyecto-socio-datos-generales/proyecto-socio-datos-generales.component';
import { ProyectoSocioEquipoComponent } from './proyecto-socio-formulario/proyecto-socio-equipo/proyecto-socio-equipo.component';
import { ProyectoSocioPeriodoJustificacionComponent } from './proyecto-socio-formulario/proyecto-socio-periodo-justificacion/proyecto-socio-periodo-justificacion.component';
import { ProyectoSocioPeriodoPagoComponent } from './proyecto-socio-formulario/proyecto-socio-periodo-pago/proyecto-socio-periodo-pago.component';
import { PROYECTO_SOCIO_ROUTE_NAMES } from './proyecto-socio-route-names';
import { PROYECTO_SOCIO_ROUTE_PARAMS } from './proyecto-socio-route-params';

const MSG_NEW_TITLE = marker('title.new.entity');
const PROYECTO_SOCIO_KEY = marker('csp.proyecto-socio');
const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_KEY = marker('menu.csp.proyectos.socios.periodos-justificacion');

const routes: SgiRoutes = [
  {
    path: `${ROUTE_NAMES.NEW}`,
    component: ProyectoSocioCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: PROYECTO_SOCIO_KEY, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthorityForAnyUO: 'CSP-PRO-E'
    },
    resolve: {
      [PROYECTO_SOCIO_DATA_KEY]: ProyectoSocioDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_SOCIO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PROYECTO_SOCIO_ROUTE_NAMES.DATOS_GENERALES,
        component: ProyectoSocioDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_SOCIO_ROUTE_NAMES.EQUIPO,
        component: ProyectoSocioEquipoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_SOCIO_ROUTE_NAMES.PERIODO_PAGO,
        component: ProyectoSocioPeriodoPagoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_SOCIO_ROUTE_NAMES.PERIODO_JUSTIFICACION,
        component: ProyectoSocioPeriodoJustificacionComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${PROYECTO_SOCIO_ROUTE_PARAMS.ID}`,
    component: ProyectoSocioEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: PROYECTO_SOCIO_KEY,
      hasAuthorityForAnyUO: 'CSP-PRO-E'
    },
    resolve: {
      [PROYECTO_SOCIO_DATA_KEY]: ProyectoSocioDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_SOCIO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PROYECTO_SOCIO_ROUTE_NAMES.DATOS_GENERALES,
        component: ProyectoSocioDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_SOCIO_ROUTE_NAMES.EQUIPO,
        component: ProyectoSocioEquipoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_SOCIO_ROUTE_NAMES.PERIODO_PAGO,
        component: ProyectoSocioPeriodoPagoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_SOCIO_ROUTE_NAMES.PERIODO_JUSTIFICACION,
        component: ProyectoSocioPeriodoJustificacionComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${PROYECTO_SOCIO_ROUTE_PARAMS.ID}`,
    canActivate: [SgiAuthGuard],
    data: {
      title: PROYECTO_SOCIO_KEY,
      hasAuthorityForAnyUO: 'CSP-PRO-E'
    },
    resolve: {
      [PROYECTO_SOCIO_DATA_KEY]: ProyectoSocioDataResolver
    },
    children: [
      {
        path: PROYECTO_SOCIO_ROUTE_NAMES.PERIODO_JUSTIFICACION,
        loadChildren: () =>
          import('../proyecto-socio-periodo-justificacion/proyecto-socio-periodo-justificacion.module').then(
            (m) => m.ProyectoSocioPeriodoJustificacionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: PROYECTO_SOCIO_PERIODO_JUSTIFICACION_KEY
        }
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProyectoSocioRouting {
}
