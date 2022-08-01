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
import { SolicitudAutoevaluacionComponent } from './solicitud-formulario/solicitud-autoevaluacion/solicitud-autoevaluacion.component';
import { SolicitudDatosGeneralesComponent } from './solicitud-formulario/solicitud-datos-generales/solicitud-datos-generales.component';
import { SolicitudDocumentosComponent } from './solicitud-formulario/solicitud-documentos/solicitud-documentos.component';
import { SolicitudEquipoProyectoComponent } from './solicitud-formulario/solicitud-equipo-proyecto/solicitud-equipo-proyecto.component';
import { SolicitudHistoricoEstadosComponent } from './solicitud-formulario/solicitud-historico-estados/solicitud-historico-estados.component';
import { SolicitudProyectoAreaConocimientoComponent } from './solicitud-formulario/solicitud-proyecto-area-conocimiento/solicitud-proyecto-area-conocimiento.component';
import { SolicitudProyectoClasificacionesComponent } from './solicitud-formulario/solicitud-proyecto-clasificaciones/solicitud-proyecto-clasificaciones.component';
import { SolicitudProyectoFichaGeneralComponent } from './solicitud-formulario/solicitud-proyecto-ficha-general/solicitud-proyecto-ficha-general.component';
import { SolicitudRrhhMemoriaComponent } from './solicitud-formulario/solicitud-rrhh-memoria/solicitud-rrhh-memoria.component';
import { SolicitudRrhhRequisitosConvocatoriaComponent } from './solicitud-formulario/solicitud-rrhh-requisitos-convocatoria/solicitud-rrhh-requisitos-convocatoria.component';
import { SolicitudRrhhSolitanteComponent } from './solicitud-formulario/solicitud-rrhh-solicitante/solicitud-rrhh-solicitante.component';
import { SolicitudRrhhTutorComponent } from './solicitud-formulario/solicitud-rrhh-tutor/solicitud-rrhh-tutor.component';
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
      },
      {
        path: SOLICITUD_ROUTE_NAMES.PROYECTO_AREA_CONOCIMIENTO,
        component: SolicitudProyectoAreaConocimientoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.CLASIFICACIONES,
        component: SolicitudProyectoClasificacionesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.PROYECTO_DATOS,
        component: SolicitudProyectoFichaGeneralComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.AUTOEVALUACION,
        component: SolicitudAutoevaluacionComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.DOCUMENTOS,
        component: SolicitudDocumentosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.EQUIPO_PROYECTO,
        component: SolicitudEquipoProyectoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.HISTORICO_ESTADOS,
        component: SolicitudHistoricoEstadosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.SOLICITANTE,
        component: SolicitudRrhhSolitanteComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.TUTOR,
        component: SolicitudRrhhTutorComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.REQUISITOS_CONVOCATORIA,
        component: SolicitudRrhhRequisitosConvocatoriaComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.MEMORIA,
        component: SolicitudRrhhMemoriaComponent,
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
