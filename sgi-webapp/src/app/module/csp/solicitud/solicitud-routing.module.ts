import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { Module } from '@core/module';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { SOLICITUD_PROYECTO_PRESUPUESTO_AJENA_KEY, SOLICITUD_PROYECTO_PRESUPUESTO_FINANCIADORA_KEY } from '../solicitud-proyecto-presupuesto/solicitud-proyecto-presupuesto-data.resolver';
import { SolicitudCrearComponent } from './solicitud-crear/solicitud-crear.component';
import { SolicitudDataResolver, SOLICITUD_DATA_KEY } from './solicitud-data.resolver';
import { SolicitudEditarComponent } from './solicitud-editar/solicitud-editar.component';
import { SolicitudAutoevaluacionComponent } from './solicitud-formulario/solicitud-autoevaluacion/solicitud-autoevaluacion.component';
import { SolicitudDatosGeneralesComponent } from './solicitud-formulario/solicitud-datos-generales/solicitud-datos-generales.component';
import { SolicitudDocumentosComponent } from './solicitud-formulario/solicitud-documentos/solicitud-documentos.component';
import { SolicitudEquipoProyectoComponent } from './solicitud-formulario/solicitud-equipo-proyecto/solicitud-equipo-proyecto.component';
import { SolicitudHistoricoEstadosComponent } from './solicitud-formulario/solicitud-historico-estados/solicitud-historico-estados.component';
import { SolicitudHitosComponent } from './solicitud-formulario/solicitud-hitos/solicitud-hitos.component';
import { SolicitudProyectoAreaConocimientoComponent } from './solicitud-formulario/solicitud-proyecto-area-conocimiento/solicitud-proyecto-area-conocimiento.component';
import { SolicitudProyectoClasificacionesComponent } from './solicitud-formulario/solicitud-proyecto-clasificaciones/solicitud-proyecto-clasificaciones.component';
import { SolicitudProyectoEntidadesFinanciadorasComponent } from './solicitud-formulario/solicitud-proyecto-entidades-financiadoras/solicitud-proyecto-entidades-financiadoras.component';
import { SolicitudProyectoFichaGeneralComponent } from './solicitud-formulario/solicitud-proyecto-ficha-general/solicitud-proyecto-ficha-general.component';
import { SolicitudProyectoPresupuestoEntidadesComponent } from './solicitud-formulario/solicitud-proyecto-presupuesto-entidades/solicitud-proyecto-presupuesto-entidades.component';
import { SolicitudProyectoPresupuestoGlobalComponent } from './solicitud-formulario/solicitud-proyecto-presupuesto-global/solicitud-proyecto-presupuesto-global.component';
import { SolicitudProyectoResponsableEconomicoComponent } from './solicitud-formulario/solicitud-proyecto-responsable-economico/solicitud-proyecto-responsable-economico.component';
import { SolicitudProyectoSocioComponent } from './solicitud-formulario/solicitud-proyecto-socio/solicitud-proyecto-socio.component';
import { SolicitudRrhhMemoriaComponent } from './solicitud-formulario/solicitud-rrhh-memoria/solicitud-rrhh-memoria.component';
import { SolicitudRrhhRequisitosConvocatoriaComponent } from './solicitud-formulario/solicitud-rrhh-requisitos-convocatoria/solicitud-rrhh-requisitos-convocatoria.component';
import { SolicitudRrhhSolitanteComponent } from './solicitud-formulario/solicitud-rrhh-solicitante/solicitud-rrhh-solicitante.component';
import { SolicitudRrhhTutorComponent } from './solicitud-formulario/solicitud-rrhh-tutor/solicitud-rrhh-tutor.component';
import { SolicitudListadoComponent } from './solicitud-listado/solicitud-listado.component';
import { SOLICITUD_ROUTE_NAMES } from './solicitud-route-names';
import { SOLICITUD_ROUTE_PARAMS } from './solicitud-route-params';

const SOLICITUD_KEY = marker('csp.solicitud');
const MSG_NEW_TITLE = marker('title.new.entity');
const PROYECTO_SOCIO_KEY = marker('csp.solicitud-proyecto-socio');
const PROYECTO_PRESUPUESTO_KEY = marker('menu.csp.solicitudes.desgloses-presupuesto');

const routes: SgiRoutes = [
  {
    path: '',
    component: SolicitudListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: SOLICITUD_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['CSP-SOL-V', 'CSP-SOL-C', 'CSP-SOL-E', 'CSP-SOL-B', 'CSP-SOL-R', 'CSP-PRO-C'],
      module: Module.CSP
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: SolicitudCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: SOLICITUD_KEY, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthorityForAnyUO: 'CSP-SOL-C',
      module: Module.CSP
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
      hasAnyAuthorityForAnyUO: ['CSP-SOL-E', 'CSP-SOL-V'],
      module: Module.CSP
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
        path: SOLICITUD_ROUTE_NAMES.PROYECTO_DATOS,
        component: SolicitudProyectoFichaGeneralComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.PROYECTO_AREA_CONOCIMIENTO,
        component: SolicitudProyectoAreaConocimientoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.SOCIOS,
        component: SolicitudProyectoSocioComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.HITOS,
        component: SolicitudHitosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.HISTORICO_ESTADOS,
        component: SolicitudHistoricoEstadosComponent,
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
        path: SOLICITUD_ROUTE_NAMES.ENTIDADES_FINANCIADORAS,
        component: SolicitudProyectoEntidadesFinanciadorasComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.DESGLOSE_PRESUPUESTO_GLOBAL,
        component: SolicitudProyectoPresupuestoGlobalComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.DESGLOSE_PRESUPUESTO_ENTIDADES,
        component: SolicitudProyectoPresupuestoEntidadesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.DESGLOSE_PRESUPUESTO_ENTIDADES_FINANCIADORAS_CONVOCATORIA,
        redirectTo: SOLICITUD_ROUTE_NAMES.DESGLOSE_PRESUPUESTO_ENTIDADES
      },
      {
        path: SOLICITUD_ROUTE_NAMES.DESGLOSE_PRESUPUESTO_ENTIDADES_FINANCIADORAS_SOLICITUD,
        redirectTo: SOLICITUD_ROUTE_NAMES.DESGLOSE_PRESUPUESTO_ENTIDADES
      },
      {
        path: SOLICITUD_ROUTE_NAMES.DESGLOSE_PRESUPUESTO_ENTIDADES_GESTORAS_CONVOCATORIA,
        redirectTo: SOLICITUD_ROUTE_NAMES.DESGLOSE_PRESUPUESTO_ENTIDADES
      },
      {
        path: SOLICITUD_ROUTE_NAMES.CLASIFICACIONES,
        component: SolicitudProyectoClasificacionesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.RESPONSABLE_ECONOMICO,
        component: SolicitudProyectoResponsableEconomicoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_ROUTE_NAMES.AUTOEVALUACION,
        component: SolicitudAutoevaluacionComponent,
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
  },
  {
    path: `:${SOLICITUD_ROUTE_PARAMS.ID}`,
    canActivate: [SgiAuthGuard],
    data: {
      title: SOLICITUD_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['CSP-SOL-E', 'CSP-SOL-V'],
      module: Module.CSP
    },
    resolve: {
      [SOLICITUD_DATA_KEY]: SolicitudDataResolver
    },
    children: [
      {
        path: SOLICITUD_ROUTE_NAMES.SOCIOS,
        loadChildren: () =>
          import('../solicitud-proyecto-socio/solicitud-proyecto-socio.module').then(
            (m) => m.SolicitudProyectoSocioModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: PROYECTO_SOCIO_KEY,
          module: Module.CSP
        }
      },
      {
        path: SOLICITUD_ROUTE_NAMES.DESGLOSE_PRESUPUESTO_ENTIDADES_FINANCIADORAS_CONVOCATORIA,
        loadChildren: () =>
          import('../solicitud-proyecto-presupuesto/solicitud-proyecto-presupuesto.module').then(
            (m) => m.SolicitudProyectoPresupuestoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          [SOLICITUD_PROYECTO_PRESUPUESTO_AJENA_KEY]: false,
          [SOLICITUD_PROYECTO_PRESUPUESTO_FINANCIADORA_KEY]: true,
          title: PROYECTO_PRESUPUESTO_KEY,
          module: Module.CSP
        }
      },
      {
        path: SOLICITUD_ROUTE_NAMES.DESGLOSE_PRESUPUESTO_ENTIDADES_FINANCIADORAS_SOLICITUD,
        loadChildren: () =>
          import('../solicitud-proyecto-presupuesto/solicitud-proyecto-presupuesto.module').then(
            (m) => m.SolicitudProyectoPresupuestoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          [SOLICITUD_PROYECTO_PRESUPUESTO_AJENA_KEY]: true,
          [SOLICITUD_PROYECTO_PRESUPUESTO_FINANCIADORA_KEY]: true,
          title: PROYECTO_PRESUPUESTO_KEY,
          module: Module.CSP
        }
      },
      {
        path: SOLICITUD_ROUTE_NAMES.DESGLOSE_PRESUPUESTO_ENTIDADES_GESTORAS_CONVOCATORIA,
        loadChildren: () =>
          import('../solicitud-proyecto-presupuesto/solicitud-proyecto-presupuesto.module').then(
            (m) => m.SolicitudProyectoPresupuestoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          [SOLICITUD_PROYECTO_PRESUPUESTO_AJENA_KEY]: false,
          [SOLICITUD_PROYECTO_PRESUPUESTO_FINANCIADORA_KEY]: false,
          title: PROYECTO_PRESUPUESTO_KEY,
          module: Module.CSP
        }
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SolicitudRoutingModule {
}
