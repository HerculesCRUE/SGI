import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ProyectoSocioPeriodoJustificacionCrearComponent } from './proyecto-socio-periodo-justificacion-crear/proyecto-socio-periodo-justificacion-crear.component';
import { ProyectoSocioPeriodoJustificacionDataResolver, PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DATA_KEY } from './proyecto-socio-periodo-justificacion-data.resolver';
import { ProyectoSocioPeriodoJustificacionEditarComponent } from './proyecto-socio-periodo-justificacion-editar/proyecto-socio-periodo-justificacion-editar.component';
import { ProyectoSocioPeriodoJustificacionDatosGeneralesComponent } from './proyecto-socio-periodo-justificacion-formulario/proyecto-socio-periodo-justificacion-datos-generales/proyecto-socio-periodo-justificacion-datos-generales.component';
import { ProyectoSocioPeriodoJustificacionDocumentosComponent } from './proyecto-socio-periodo-justificacion-formulario/proyecto-socio-periodo-justificacion-documentos/proyecto-socio-periodo-justificacion-documentos.component';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES } from './proyecto-socio-periodo-justificacion-names';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_PARAMS } from './proyecto-socio-periodo-justificacion-route-params';

const MSG_NEW_TITLE = marker('title.new.entity');
const MSG_EDIT_TITLE = marker('title.csp.proyecto-socio-periodo-justificacion');

const routes: SgiRoutes = [
  {
    path: `${ROUTE_NAMES.NEW}`,
    component: ProyectoSocioPeriodoJustificacionCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: MSG_EDIT_TITLE, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR,
        hasAuthorityForAnyUO: 'CSP-PRO-E'
      }
    },
    resolve: {
      [PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DATA_KEY]: ProyectoSocioPeriodoJustificacionDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES.DATOS_GENERALES,
        component: ProyectoSocioPeriodoJustificacionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES.DOCUMENTOS,
        component: ProyectoSocioPeriodoJustificacionDocumentosComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_PARAMS.ID}`,
    component: ProyectoSocioPeriodoJustificacionEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_EDIT_TITLE,
      hasAuthorityForAnyUO: 'CSP-PRO-E'
    },
    resolve: {
      [PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DATA_KEY]: ProyectoSocioPeriodoJustificacionDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES.DATOS_GENERALES,
        component: ProyectoSocioPeriodoJustificacionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_NAMES.DOCUMENTOS,
        component: ProyectoSocioPeriodoJustificacionDocumentosComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProyectoSocioPeriodoJustificacionRouting {
}
