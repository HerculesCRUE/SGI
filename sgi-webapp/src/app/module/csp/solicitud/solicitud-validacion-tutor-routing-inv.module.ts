import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { SolicitudDataResolver, SOLICITUD_DATA_KEY } from './solicitud-data.resolver';
import { SolicitudEditarComponent } from './solicitud-editar/solicitud-editar.component';
import { SolicitudDatosGeneralesComponent } from './solicitud-formulario/solicitud-datos-generales/solicitud-datos-generales.component';
import { SolicitudDocumentosComponent } from './solicitud-formulario/solicitud-documentos/solicitud-documentos.component';
import { SolicitudHistoricoEstadosComponent } from './solicitud-formulario/solicitud-historico-estados/solicitud-historico-estados.component';
import { SolicitudRrhhMemoriaComponent } from './solicitud-formulario/solicitud-rrhh-memoria/solicitud-rrhh-memoria.component';
import { SolicitudRrhhRequisitosConvocatoriaComponent } from './solicitud-formulario/solicitud-rrhh-requisitos-convocatoria/solicitud-rrhh-requisitos-convocatoria.component';
import { SolicitudRrhhSolitanteComponent } from './solicitud-formulario/solicitud-rrhh-solicitante/solicitud-rrhh-solicitante.component';
import { SolicitudRrhhTutorComponent } from './solicitud-formulario/solicitud-rrhh-tutor/solicitud-rrhh-tutor.component';
import { SOLICITUD_ROUTE_NAMES } from './solicitud-route-names';
import { SOLICITUD_ROUTE_PARAMS } from './solicitud-route-params';
import { ValidacionTutorListadoInvComponent } from './validacion-tutor-listado-inv/validacion-tutor-listado-inv.component';

const SOLICITUD_KEY = marker('csp.solicitud');
const MSG_VALIDACION_TUTOR_TITLE = marker('title.csp.validacion-solicitudes-tutor');

const routes: SgiRoutes = [
  {
    path: '',
    component: ValidacionTutorListadoInvComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_VALIDACION_TUTOR_TITLE,
      hasAnyAuthority: ['CSP-SOL-INV-ER'],
    }
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
        path: SOLICITUD_ROUTE_NAMES.DOCUMENTOS,
        component: SolicitudDocumentosComponent,
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
export class SolicitudValidacionTutorRoutingInvModule {
}
