import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';
import { ProyectoCrearComponent } from './proyecto-crear/proyecto-crear.component';
import { ProyectoDataResolver, PROYECTO_DATA_KEY } from './proyecto-data.resolver';
import { ProyectoEditarComponent } from './proyecto-editar/proyecto-editar.component';
import { ProyectoAgrupacionesGastoComponent } from './proyecto-formulario/proyecto-agrupaciones-gasto/proyecto-agrupaciones-gasto.component';
import { ProyectoAmortizacionFondosComponent } from './proyecto-formulario/proyecto-amortizacion-fondos/proyecto-amortizacion-fondos.component';
import { ProyectoAreaConocimientoComponent } from './proyecto-formulario/proyecto-area-conocimiento/proyecto-area-conocimiento.component';
import { ProyectoCalendarioFacturacionComponent } from './proyecto-formulario/proyecto-calendario-facturacion/proyecto-calendario-facturacion.component';
import { ProyectoCalendarioJustificacionComponent } from './proyecto-formulario/proyecto-calendario-justificacion/proyecto-calendario-justificacion.component';
import { ProyectoClasificacionesComponent } from './proyecto-formulario/proyecto-clasificaciones/proyecto-clasificaciones.component';
import { ProyectoConceptosGastoComponent } from './proyecto-formulario/proyecto-conceptos-gasto/proyecto-conceptos-gasto.component';
import { ProyectoConsultaPresupuestoComponent } from './proyecto-formulario/proyecto-consulta-presupuesto/proyecto-consulta-presupuesto.component';
import { ProyectoContextoComponent } from './proyecto-formulario/proyecto-contexto/proyecto-contexto.component';
import { ProyectoFichaGeneralComponent } from './proyecto-formulario/proyecto-datos-generales/proyecto-ficha-general.component';
import { ProyectoDocumentosComponent } from './proyecto-formulario/proyecto-documentos/proyecto-documentos.component';
import { ProyectoEntidadGestoraComponent } from './proyecto-formulario/proyecto-entidad-gestora/proyecto-entidad-gestora.component';
import { ProyectoEntidadesConvocantesComponent } from './proyecto-formulario/proyecto-entidades-convocantes/proyecto-entidades-convocantes.component';
import { ProyectoEntidadesFinanciadorasComponent } from './proyecto-formulario/proyecto-entidades-financiadoras/proyecto-entidades-financiadoras.component';
import { ProyectoEquipoComponent } from './proyecto-formulario/proyecto-equipo/proyecto-equipo.component';
import { ProyectoHistoricoEstadosComponent } from './proyecto-formulario/proyecto-historico-estados/proyecto-historico-estados.component';
import { ProyectoHitosComponent } from './proyecto-formulario/proyecto-hitos/proyecto-hitos.component';
import { ProyectoPaqueteTrabajoComponent } from './proyecto-formulario/proyecto-paquete-trabajo/proyecto-paquete-trabajo.component';
import { ProyectoPartidasPresupuestariasComponent } from './proyecto-formulario/proyecto-partidas-presupuestarias/proyecto-partidas-presupuestarias.component';
import { ProyectoPeriodoSeguimientosComponent } from './proyecto-formulario/proyecto-periodo-seguimientos/proyecto-periodo-seguimientos.component';
import { ProyectoPlazosComponent } from './proyecto-formulario/proyecto-plazos/proyecto-plazos.component';
import { ProyectoPresupuestoComponent } from './proyecto-formulario/proyecto-presupuesto/proyecto-presupuesto.component';
import { ProyectoProrrogasComponent } from './proyecto-formulario/proyecto-prorrogas/proyecto-prorrogas.component';
import { ProyectoProyectosSgeComponent } from './proyecto-formulario/proyecto-proyectos-sge/proyecto-proyectos-sge.component';
import { ProyectoRelacionesComponent } from './proyecto-formulario/proyecto-relaciones/proyecto-relaciones.component';
import { ProyectoResponsableEconomicoComponent } from './proyecto-formulario/proyecto-responsable-economico/proyecto-responsable-economico.component';
import { ProyectoSociosComponent } from './proyecto-formulario/proyecto-socios/proyecto-socios.component';
import { ProyectoListadoComponent } from './proyecto-listado/proyecto-listado.component';
import { PROYECTO_ROUTE_NAMES } from './proyecto-route-names';
import { PROYECTO_ROUTE_PARAMS } from './proyecto-route-params';

const PROYECTO_KEY = marker('csp.proyecto');
const PROYECTO_SOCIOS_KEY = marker('menu.csp.proyectos.socios');
const PROYECTO_PERIODOS_SEGUIMIENTO_KEY = marker('menu.csp.proyectos.seguimientos-cientificos');
const PROYECTO_PRESUPUESTO_KEY = marker('menu.csp.proyectos.configuracion-economica.presupuesto');
const PROYECTO_PRORROGA_KEY = marker('menu.csp.proyectos.prorrogas');
const PROYECTO_ELEGIBILIDAD_KEY = marker('csp.proyecto-elegibilidad');
const PROYECTO_AGRUPACION_GASTO_KEY = marker('csp.proyecto-agrupacion-gasto');
const MSG_NEW_TITLE = marker('title.new.entity');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: ProyectoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: PROYECTO_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E', 'CSP-PRO-B', 'CSP-PRO-R']
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: ProyectoCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: PROYECTO_KEY, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthorityForAnyUO: 'CSP-PRO-C'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_ROUTE_NAMES.FICHA_GENERAL
      },
      {
        path: PROYECTO_ROUTE_NAMES.FICHA_GENERAL,
        component: ProyectoFichaGeneralComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${PROYECTO_ROUTE_PARAMS.ID}`,
    component: ProyectoEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      [PROYECTO_DATA_KEY]: ProyectoDataResolver
    },
    data: {
      title: PROYECTO_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['CSP-PRO-V', 'CSP-PRO-E']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_ROUTE_NAMES.FICHA_GENERAL
      },
      {
        path: PROYECTO_ROUTE_NAMES.FICHA_GENERAL,
        component: ProyectoFichaGeneralComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.ENTIDADES_FINANCIADORAS,
        component: ProyectoEntidadesFinanciadorasComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.HITOS,
        component: ProyectoHitosComponent,
        canDeactivate: [FragmentGuard],
        canActivate: [SgiAuthGuard],
        data: {
          hasAuthorityForAnyUO: 'CSP-PRO-E'
        },
      },
      {
        path: PROYECTO_ROUTE_NAMES.SOCIOS,
        component: ProyectoSociosComponent,
        canDeactivate: [FragmentGuard],
        canActivate: [SgiAuthGuard],
        data: {
          hasAnyAuthorityForAnyUO: ['CSP-PRO-V', 'CSP-PRO-E']
        },
      },
      {
        path: PROYECTO_ROUTE_NAMES.ENTIDADES_CONVOCANTES,
        component: ProyectoEntidadesConvocantesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.PAQUETE_TRABAJO,
        component: ProyectoPaqueteTrabajoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.FASES,
        component: ProyectoPlazosComponent,
        canDeactivate: [FragmentGuard],
        canActivate: [SgiAuthGuard],
        data: {
          hasAuthorityForAnyUO: 'CSP-PRO-E'
        },
      },
      {
        path: PROYECTO_ROUTE_NAMES.CONTEXTO_PROYECTO,
        component: ProyectoContextoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.AREA_CONOCIMIENTO,
        component: ProyectoAreaConocimientoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.SEGUIMIENTO_CIENTIFICO,
        component: ProyectoPeriodoSeguimientosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.ENTIDAD_GESTORA,
        component: ProyectoEntidadGestoraComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.EQUIPO_PROYECTO,
        component: ProyectoEquipoComponent,
      },
      {
        path: PROYECTO_ROUTE_NAMES.DOCUMENTOS,
        component: ProyectoDocumentosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.PRORROGAS,
        component: ProyectoProrrogasComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.HISTORICO_ESTADOS,
        component: ProyectoHistoricoEstadosComponent,
        canDeactivate: [FragmentGuard],
        canActivate: [SgiAuthGuard],
        data: {
          hasAuthorityForAnyUO: 'CSP-PRO-E'
        },
      },
      {
        path: PROYECTO_ROUTE_NAMES.CLASIFICACIONES,
        component: ProyectoClasificacionesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.IDENTIFICACION,
        component: ProyectoProyectosSgeComponent,
        canDeactivate: [FragmentGuard],
        canActivate: [SgiAuthGuard],
        data: {
          hasAuthorityForAnyUO: 'CSP-PRO-E'
        },
      },
      {
        path: PROYECTO_ROUTE_NAMES.PARTIDAS_PRESUPUESTARIAS,
        component: ProyectoPartidasPresupuestariasComponent,
        canActivate: [SgiAuthGuard],
        data: {
          hasAuthorityForAnyUO: 'CSP-PRO-E'
        },
      },
      {
        path: PROYECTO_ROUTE_NAMES.AGRUPACIONES_GASTO,
        component: ProyectoAgrupacionesGastoComponent,
        canDeactivate: [FragmentGuard],
        canActivate: [SgiAuthGuard],
        data: {
          hasAuthorityForAnyUO: 'CSP-PRO-E'
        },
      },
      {
        path: PROYECTO_ROUTE_NAMES.ELEGIBILIDAD,
        component: ProyectoConceptosGastoComponent,
        canDeactivate: [FragmentGuard],
        canActivate: [SgiAuthGuard],
        data: {
          hasAuthorityForAnyUO: 'CSP-PRO-E'
        },
      },
      {
        path: PROYECTO_ROUTE_NAMES.CONCEPTO_GATO_PERMITIDO,
        redirectTo: PROYECTO_ROUTE_NAMES.ELEGIBILIDAD,
        canActivate: [SgiAuthGuard],
        data: {
          hasAuthorityForAnyUO: 'CSP-PRO-E'
        },
      },
      {
        path: PROYECTO_ROUTE_NAMES.CONCEPTO_GATO_NO_PERMITIDO,
        redirectTo: PROYECTO_ROUTE_NAMES.ELEGIBILIDAD,
        canActivate: [SgiAuthGuard],
        data: {
          hasAuthorityForAnyUO: 'CSP-PRO-E'
        },
      },
      {
        path: PROYECTO_ROUTE_NAMES.PRESUPUESTO,
        component: ProyectoPresupuestoComponent,
        canDeactivate: [FragmentGuard],
      },
      {
        path: PROYECTO_ROUTE_NAMES.RESPONSABLE_ECONOMICO,
        component: ProyectoResponsableEconomicoComponent,
        canDeactivate: [FragmentGuard],
        canActivate: [SgiAuthGuard],
        data: {
          hasAuthorityForAnyUO: 'CSP-PRO-E'
        },
      },
      {
        path: PROYECTO_ROUTE_NAMES.CALENDARIO_FACTURACION,
        component: ProyectoCalendarioFacturacionComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.CALENDARIO_JUSTIFICACION,
        component: ProyectoCalendarioJustificacionComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.CONSULTA_PRESUPUESTO,
        component: ProyectoConsultaPresupuestoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.AMORTIZACION_FONDOS,
        component: ProyectoAmortizacionFondosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.RELACIONES,
        component: ProyectoRelacionesComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  },
  {
    path: `:${PROYECTO_ROUTE_PARAMS.ID}`,
    canActivate: [SgiAuthGuard],
    data: {
      title: PROYECTO_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAuthorityForAnyUO: 'CSP-PRO-E'
    },
    resolve: {
      [PROYECTO_DATA_KEY]: ProyectoDataResolver
    },
    children: [
      {
        path: PROYECTO_ROUTE_NAMES.SOCIOS,
        loadChildren: () =>
          import('../proyecto-socio/proyecto-socio.module').then(
            (m) => m.ProyectoSocioModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: PROYECTO_SOCIOS_KEY
        }
      },
      {
        path: PROYECTO_ROUTE_NAMES.SEGUIMIENTO_CIENTIFICO,
        loadChildren: () =>
          import('../proyecto-periodo-seguimiento/proyecto-periodo-seguimiento.module').then(
            (m) => m.ProyectoPeriodoSeguimientoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: PROYECTO_PERIODOS_SEGUIMIENTO_KEY,
          hasAuthorityForAnyUO: 'CSP-PRO-E'
        }
      },
      {
        path: PROYECTO_ROUTE_NAMES.AGRUPACIONES_GASTO,
        loadChildren: () =>
          import('../proyecto-agrupacion-gasto/proyecto-agrupacion-gasto.module').then(
            (m) => m.ProyectoAgrupacionGastoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: PROYECTO_AGRUPACION_GASTO_KEY
        }
      },
      {
        path: PROYECTO_ROUTE_NAMES.PRORROGAS,
        loadChildren: () =>
          import('../proyecto-prorroga/proyecto-prorroga.module').then(
            (m) => m.ProyectoProrrogaModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: PROYECTO_PRORROGA_KEY,
          hasAuthorityForAnyUO: 'CSP-PRO-E'
        }
      },
      {
        path: `${PROYECTO_ROUTE_NAMES.CONCEPTO_GATO_PERMITIDO}`,
        loadChildren: () =>
          import('../proyecto-concepto-gasto/proyecto-concepto-gasto.module').then(
            (m) => m.ProyectoConceptoGastoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: PROYECTO_ELEGIBILIDAD_KEY,
          permitido: true
        }
      },
      {
        path: `${PROYECTO_ROUTE_NAMES.CONCEPTO_GATO_NO_PERMITIDO}`,
        loadChildren: () =>
          import('../proyecto-concepto-gasto/proyecto-concepto-gasto.module').then(
            (m) => m.ProyectoConceptoGastoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: PROYECTO_ELEGIBILIDAD_KEY,
          permitido: false
        }
      },
      {
        path: PROYECTO_ROUTE_NAMES.PRESUPUESTO,
        loadChildren: () =>
          import('../proyecto-anualidad/proyecto-anualidad.module').then(
            (m) => m.ProyectoAnualidadModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: PROYECTO_PRESUPUESTO_KEY,
          hasAuthorityForAnyUO: 'CSP-PRO-E'
        }
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProyectoRoutingModule {
}
