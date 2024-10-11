import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { CspInicioComponent } from './csp-inicio/csp-inicio.component';
import { CspRootComponent } from './csp-root/csp-root.component';
import { CSP_ROUTE_NAMES } from './csp-route-names';

const AUTORIZACION_KEY = marker('csp.autorizacion');
const NOTIFICACION_CVN_KEY = marker('csp.notificacion-cvn');
const PROYECTO_KEY = marker('csp.proyectos');

const MSG_AREA_TEMATICA_TITLE = marker('menu.csp.configuraciones.areas-tematicas');
const MSG_CONVOCATORIA_TITLE = marker('csp.convocatoria');
const MSG_EJECUCION_ECONOMICA_TITLE = marker('menu.csp.ejecucion-economica');
const MSG_FACTURAS_PREVISTAS_PENDIENTES_TITLE = marker('menu.csp.facturas-previstas-pendientes');
const MSG_FUENTE_FINANCIACION_TITLE = marker('menu.csp.configuraciones.fuentes-financiacion');
const MSG_GESTION_CONCEPTO_GASTO_TITLE = marker('menu.csp.configuraciones.conceptos-gasto');
const MSG_GESTION_LINEA_INVESTIGACION_TITLE = marker('menu.csp.configuraciones.lineas-investigacion');
const MSG_GRUPO_TITLE = marker('csp.grupo');
const MSG_MODELO_EJECUCION_TITLE = marker('menu.csp.configuraciones.modelos-ejecucion');
const MSG_NOTIFICACION_PRESUPUESTO_SGE_TITLE = marker('menu.csp.notificacion-presupuesto-sge');
const MSG_PLAN_INVESTIGACION_TITLE = marker('menu.csp.configuraciones.planes-investigacion');
const MSG_ROL_EQUIPO_TITLE = marker('csp.rol-equipo');
const MSG_ROL_SOCIO_PROYECTO = marker('menu.csp.rol-socio');
const MSG_ROOT_TITLE = marker('csp.root.title');
const MSG_SOLICITUD_TITLE = marker('csp.solicitud');
const MSG_TIPO_AMBITO_GEOGRAFICO_TITLE = marker('csp.tipo-ambito-geografico');
const MSG_TIPO_DOCUMENTO_TITLE = marker('csp.tipo-documento');
const MSG_TIPO_ENLACE_TITLE = marker('csp.tipo-enlace');
const MSG_TIPO_FINALIDAD_TITLE = marker('csp.tipo-finalidad');
const MSG_TIPO_FINANCIACION_TITLE = marker('menu.csp.configuraciones.tipos-financiacion');
const MSG_TIPO_HITO_TITLE = marker('csp.tipo-hito');
const MSG_TIPO_ORIGEN_FUENTE_FINANCIACION_TITLE = marker('menu.csp.tipo-origen-fuente-financiacion');
const MSG_TIPO_REGIMEN_CONCURRENCIA_TITLE = marker('csp.tipo-regimen-concurrencia');

const routes: SgiRoutes = [
  {
    path: '',
    component: CspRootComponent,
    data: {
      title: MSG_ROOT_TITLE
    },
    children: [
      {
        path: '',
        component: CspInicioComponent,
        pathMatch: 'full',
        data: {
          title: MSG_ROOT_TITLE
        }
      },
      {
        path: CSP_ROUTE_NAMES.CONVOCATORIA,
        loadChildren: () =>
          import('./convocatoria/convocatoria.module').then(
            (m) => m.ConvocatoriaModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_CONVOCATORIA_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['CSP-CON-V', 'CSP-CON-C', 'CSP-CON-E', 'CSP-CON-B', 'CSP-CON-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.SOLICITUD,
        loadChildren: () =>
          import('./solicitud/solicitud.module').then(
            (m) => m.SolicitudModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_SOLICITUD_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['CSP-SOL-V', 'CSP-SOL-E', 'CSP-SOL-C', 'CSP-SOL-B', 'CSP-SOL-R', 'CSP-PRO-C']
        }
      },
      {
        path: CSP_ROUTE_NAMES.PROYECTO,
        loadChildren: () =>
          import('./proyecto/proyecto.module').then(
            (m) => m.ProyectoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: PROYECTO_KEY,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E', 'CSP-PRO-B', 'CSP-PRO-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.TIPO_DOCUMENTO,
        loadChildren: () =>
          import('./tipo-documento/tipo-documento.module').then(
            (m) => m.TipoDocumentoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_TIPO_DOCUMENTO_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['CSP-TDOC-V', 'CSP-TDOC-C', 'CSP-TDOC-E', 'CSP-TDOC-B', 'CSP-TDOC-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.TIPO_FINALIDAD,
        loadChildren: () =>
          import('./tipo-finalidad/tipo-finalidad.module').then(
            (m) => m.TipoFinalidadModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_TIPO_FINALIDAD_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['CSP-TFIN-V', 'CSP-TFIN-C', 'CSP-TFIN-E', 'CSP-TFIN-B', 'CSP-TFIN-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.MODELO_EJECUCION,
        loadChildren: () =>
          import('./modelo-ejecucion/modelo-ejecucion.module').then(
            (m) => m.ModeloEjecucionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_MODELO_EJECUCION_TITLE,
          hasAnyAuthorityForAnyUO: ['CSP-ME-V', 'CSP-ME-C', 'CSP-ME-E', 'CSP-ME-B', 'CSP-ME-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.TIPO_ENLACE,
        loadChildren: () =>
          import('./tipo-enlace/tipo-enlace.module').then(
            (m) => m.TipoEnlaceModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_TIPO_ENLACE_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['CSP-TENL-V', 'CSP-TENL-C', 'CSP-TENL-E', 'CSP-TENL-B', 'CSP-TENL-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.TIPO_HITO,
        loadChildren: () =>
          import('./tipo-hito/tipo-hito.module').then(
            (m) => m.TipoHitoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_TIPO_HITO_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['CSP-THITO-V', 'CSP-THITO-C', 'CSP-THITO-E', 'CSP-THITO-B', 'CSP-THITO-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.TIPO_FASE,
        loadChildren: () =>
          import('./tipo-fase/tipo-fase.module').then(
            (m) => m.TipoFaseModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_CONVOCATORIA_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['CSP-TFASE-V', 'CSP-TFASE-C', 'CSP-TFASE-E', 'CSP-TFASE-B', 'CSP-TFASE-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.PLANES_INVESTIGACION,
        loadChildren: () =>
          import('./plan-investigacion/plan-investigacion.module').then(
            (m) => m.PlanInvestigacionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_PLAN_INVESTIGACION_TITLE,
          hasAnyAuthority: ['CSP-PRG-V', 'CSP-PRG-C', 'CSP-PRG-E', 'CSP-PRG-B', 'CSP-PRG-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.CONCEPTO_GASTO,
        loadChildren: () =>
          import('./concepto-gasto/concepto-gasto.module').then(
            (m) => m.ConceptoGastoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_GESTION_CONCEPTO_GASTO_TITLE,
          hasAnyAuthority: ['CSP-TGTO-V', 'CSP-TGTO-C', 'CSP-TGTO-E', 'CSP-TGTO-B', 'CSP-TGTO-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.LINEA_INVESTIGACION,
        loadChildren: () =>
          import('./linea-investigacion/linea-investigacion.module').then(
            (m) => m.LineaInvestigacionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_GESTION_LINEA_INVESTIGACION_TITLE,
          hasAnyAuthority: ['CSP-LIN-C', 'CSP-LIN-E', 'CSP-LIN-B', 'CSP-LIN-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.TIPO_FINANCIACION,
        loadChildren: () =>
          import('./tipo-financiacion/tipo-financiacion.module').then(
            (m) => m.TipoFinanciacionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_TIPO_FINANCIACION_TITLE,
          hasAnyAuthority: ['CSP-TFNA-V', 'CSP-TFNA-C', 'CSP-TFNA-E', 'CSP-TFNA-B', 'CSP-TFNA-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.FUENTE_FINANCIACION,
        loadChildren: () =>
          import('./fuente-financiacion/fuente-financiacion.module').then(
            (m) => m.FuenteFinanciacionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_FUENTE_FINANCIACION_TITLE,
          hasAnyAuthority: ['CSP-FNT-V', 'CSP-FNT-C', 'CSP-FNT-E', 'CSP-FNT-B', 'CSP-FNT-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.AREA_TEMATICA,
        loadChildren: () =>
          import('./area-tematica/area-tematica.module').then(
            (m) => m.AreaTematicaModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_AREA_TEMATICA_TITLE,
          hasAnyAuthority: ['CSP-AREA-V', 'CSP-AREA-C', 'CSP-AREA-E', 'CSP-AREA-B', 'CSP-AREA-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.EJECUCION_ECONOMICA,
        loadChildren: () =>
          import('./ejecucion-economica/ejecucion-economica.module').then(
            (m) => m.EjecucionEconomicaModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_EJECUCION_ECONOMICA_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
          hasAnyAuthorityForAnyUO: ['CSP-EJEC-V', 'CSP-EJEC-E', 'CSP-EJEC-INV-VR']
        }
      },
      {
        path: CSP_ROUTE_NAMES.TIPO_ORIGEN_FUENTE_FINANCIACION,
        loadChildren: () =>
          import('./tipo-origen-fuente-financiacion/tipo-origen-fuente-financiacion.module').then(
            (m) => m.TipoOrigenFuenteFinanciacionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_TIPO_ORIGEN_FUENTE_FINANCIACION_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
          hasAnyAuthorityForAnyUO: ['CSP-TOFF-V', 'CSP-TOFF-C', 'CSP-TOFF-E', 'CSP-TOFF-B', 'CSP-TOFF-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.NOTIFICACION_PRESUPUESTO_SGE,
        loadChildren: () =>
          import('./notificacion-presupuesto-sge/notificacion-presupuesto-sge.module').then(
            (m) => m.NotificacionPresupuestoSgeModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_NOTIFICACION_PRESUPUESTO_SGE_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
          hasAuthorityForAnyUO: 'CSP-EJEC-E'
        }
      },
      {
        path: CSP_ROUTE_NAMES.AUTORIZACION,
        loadChildren: () =>
          import('./autorizacion/autorizacion.module').then(
            (m) => m.AutorizacionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: AUTORIZACION_KEY,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['CSP-AUT-E', 'CSP-AUT-V', 'CSP-AUT-B', 'CSP-AUT-INV-C', 'CSP-AUT-INV-ER', 'CSP-AUT-INV-BR'],
        }
      },
      {
        path: CSP_ROUTE_NAMES.NOTIFICACION_CVN,
        loadChildren: () =>
          import('./notificacion-cvn/notificacion-cvn.module').then(
            (m) => m.NotificacionCvnModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: NOTIFICACION_CVN_KEY,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['CSP-CVPR-V', 'CSP-CVPR-E']
        }
      },
      {
        path: CSP_ROUTE_NAMES.GRUPO,
        loadChildren: () =>
          import('./grupo/grupo.module').then(
            (m) => m.GrupoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_GRUPO_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['CSP-GIN-V', 'CSP-GIN-E', 'CSP-GIN-C', 'CSP-GIN-B', 'CSP-GIN-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.TIPOS_REGIMEN_CONCURRENCIA,
        loadChildren: () =>
          import('./tipo-regimen-concurrencia/tipo-regimen-concurrencia.module').then(
            (m) => m.TipoRegimenConcurrenciaModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_TIPO_REGIMEN_CONCURRENCIA_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['CSP-TRCO-V', 'CSP-TRCO-E', 'CSP-TRCO-C', 'CSP-TRCO-B', 'CSP-TRCO-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.TIPOS_AMBITO_GEOGRAFICO,
        loadChildren: () =>
          import('./tipo-ambito-geografico/tipo-ambito-geografico.module').then(
            (m) => m.TipoAmbitoGeograficoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_TIPO_AMBITO_GEOGRAFICO_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['CSP-TAGE-V', 'CSP-TAGE-C', 'CSP-TAGE-E', 'CSP-TAGE-B', 'CSP-TAGE-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.TIPO_FACTURACION,
        loadChildren: () =>
          import('./tipo-facturacion/tipo-facturacion.module').then(
            (m) => m.TipoFacturacionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_TIPO_REGIMEN_CONCURRENCIA_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['CSP-TFAC-V', 'CSP-TFAC-E', 'CSP-TFAC-C', 'CSP-TFAC-B', 'CSP-TFAC-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.ROL_SOCIO_PROYECTO,
        loadChildren: () =>
          import('./rol-socio-proyecto/rol-socio.module').then(
            (m) => m.RolSocioModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_ROL_SOCIO_PROYECTO,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['CSP-ROLS-V', 'CSP-ROLS-C', 'CSP-ROLS-E', 'CSP-ROLS-B', 'CSP-ROLS-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.ROL_EQUIPO,
        loadChildren: () =>
          import('./rol-equipo/rol-equipo.module').then(
            (m) => m.RolEquipoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_ROL_EQUIPO_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['CSP-ROLE-V', 'CSP-ROLE-E', 'CSP-ROLE-C', 'CSP-ROLE-B', 'CSP-ROLE-R']
        }
      },
      {
        path: CSP_ROUTE_NAMES.FACTURAS_PREVISTAS_PENDIENTES,
        loadChildren: () =>
          import('./facturas-previstas-pendientes/facturas-previstas-pendientes.module').then(
            (m) => m.FacturasPrevistasPendientesModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_FACTURAS_PREVISTAS_PENDIENTES_TITLE,
          hasAuthorityForAnyUO: 'CSP-PRO-E'
        }
      },
      { path: '**', component: null }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CspRoutingModule {
}
