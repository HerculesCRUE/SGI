import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ConvocatoriaDataResolver, CONVOCATORIA_DATA_KEY } from './convocatoria-data.resolver';
import { ConvocatoriaEditarComponent } from './convocatoria-editar/convocatoria-editar.component';
import { ConvocatoriaConceptoGastoComponent } from './convocatoria-formulario/convocatoria-concepto-gasto/convocatoria-concepto-gasto.component';
import { ConvocatoriaConfiguracionSolicitudesComponent } from './convocatoria-formulario/convocatoria-configuracion-solicitudes/convocatoria-configuracion-solicitudes.component';
import { ConvocatoriaDatosGeneralesComponent } from './convocatoria-formulario/convocatoria-datos-generales/convocatoria-datos-generales.component';
import { ConvocatoriaDocumentosComponent } from './convocatoria-formulario/convocatoria-documentos/convocatoria-documentos.component';
import { ConvocatoriaEnlaceComponent } from './convocatoria-formulario/convocatoria-enlace/convocatoria-enlace.component';
import { ConvocatoriaEntidadesConvocantesComponent } from './convocatoria-formulario/convocatoria-entidades-convocantes/convocatoria-entidades-convocantes.component';
import { ConvocatoriaEntidadesFinanciadorasComponent } from './convocatoria-formulario/convocatoria-entidades-financiadoras/convocatoria-entidades-financiadoras.component';
import { ConvocatoriaHitosComponent } from './convocatoria-formulario/convocatoria-hitos/convocatoria-hitos.component';
import { ConvocatoriaPartidaPresupuestariaComponent } from './convocatoria-formulario/convocatoria-partidas-presupuestarias/convocatoria-partidas-presupuestarias.component';
import { ConvocatoriaPeriodosJustificacionComponent } from './convocatoria-formulario/convocatoria-periodos-justificacion/convocatoria-periodos-justificacion.component';
import { ConvocatoriaPlazosFasesComponent } from './convocatoria-formulario/convocatoria-plazos-fases/convocatoria-plazos-fases.component';
import { ConvocatoriaRequisitosEquipoComponent } from './convocatoria-formulario/convocatoria-requisitos-equipo/convocatoria-requisitos-equipo.component';
import { ConvocatoriaRequisitosIPComponent } from './convocatoria-formulario/convocatoria-requisitos-ip/convocatoria-requisitos-ip.component';
import { ConvocatoriaSeguimientoCientificoComponent } from './convocatoria-formulario/convocatoria-seguimiento-cientifico/convocatoria-seguimiento-cientifico.component';
import { ConvocatoriaListadoInvComponent } from './convocatoria-listado-inv/convocatoria-listado-inv.component';
import { CONVOCATORIA_ROUTE_NAMES } from './convocatoria-route-names';
import { CONVOCATORIA_ROUTE_PARAMS } from './convocatoria-route-params';

const MSG_CONVOCATORIAS_TITLE = marker('inv.convocatoria.listado.titulo');
const MSG_CONVOCATORIAS_VER_TITLE = marker('inv.convocatoria.ver.titulo');
const CONVOCATORIA_ELEGIBILIDAD_KEY = marker('csp.convocatoria-elegibilidad');

const routes: SgiRoutes = [
  {
    path: '',
    component: ConvocatoriaListadoInvComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_CONVOCATORIAS_TITLE,
      hasAuthorityForAnyUO: 'CSP-CON-INV-V',
    }
  },
  {
    path: `:${CONVOCATORIA_ROUTE_PARAMS.ID}`,
    component: ConvocatoriaEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      [CONVOCATORIA_DATA_KEY]: ConvocatoriaDataResolver
    },
    data: {
      title: MSG_CONVOCATORIAS_VER_TITLE,
      hasAuthorityForAnyUO: 'CSP-CON-INV-V',
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: CONVOCATORIA_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.DATOS_GENERALES,
        component: ConvocatoriaDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      }, {
        path: CONVOCATORIA_ROUTE_NAMES.PERIODO_JUSTIFICACION,
        component: ConvocatoriaPeriodosJustificacionComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.FASES,
        component: ConvocatoriaPlazosFasesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.HITOS,
        component: ConvocatoriaHitosComponent
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.PARTIDAS_PRESUPUESTARIAS,
        component: ConvocatoriaPartidaPresupuestariaComponent
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.ENTIDADES_CONVOCANTES,
        component: ConvocatoriaEntidadesConvocantesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.ENTIDADES_FINANCIADORAS,
        component: ConvocatoriaEntidadesFinanciadorasComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.ENLACES,
        component: ConvocatoriaEnlaceComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.SEGUIMIENTO_CIENTIFICO,
        component: ConvocatoriaSeguimientoCientificoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.REQUISITOS_IP,
        component: ConvocatoriaRequisitosIPComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.ELEGIBILIDAD,
        component: ConvocatoriaConceptoGastoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.REQUISITOS_EQUIPO,
        component: ConvocatoriaRequisitosEquipoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.DOCUMENTOS,
        component: ConvocatoriaDocumentosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.CONFIGURACION_SOLICITUDES,
        component: ConvocatoriaConfiguracionSolicitudesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.CONCEPTO_GATO_PERMITIDO,
        redirectTo: CONVOCATORIA_ROUTE_NAMES.ELEGIBILIDAD
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.CONCEPTO_GATO_NO_PERMITIDO,
        redirectTo: CONVOCATORIA_ROUTE_NAMES.ELEGIBILIDAD
      }
    ]
  },
  {
    path: `:${CONVOCATORIA_ROUTE_PARAMS.ID}`,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_CONVOCATORIAS_VER_TITLE,
      hasAuthorityForAnyUO: 'CSP-CON-INV-V',
    },
    resolve: {
      [CONVOCATORIA_DATA_KEY]: ConvocatoriaDataResolver
    },
    children: [
      {
        path: `${CONVOCATORIA_ROUTE_NAMES.CONCEPTO_GATO_PERMITIDO}`,
        loadChildren: () =>
          import('../convocatoria-concepto-gasto/convocatoria-concepto-gasto.module').then(
            (m) => m.ConvocatoriaConceptoGastoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: CONVOCATORIA_ELEGIBILIDAD_KEY,
          permitido: true
        }
      },
      {
        path: `${CONVOCATORIA_ROUTE_NAMES.CONCEPTO_GATO_NO_PERMITIDO}`,
        loadChildren: () =>
          import('../convocatoria-concepto-gasto/convocatoria-concepto-gasto.module').then(
            (m) => m.ConvocatoriaConceptoGastoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: CONVOCATORIA_ELEGIBILIDAD_KEY,
          permitido: false
        }
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ConvocatoriaRoutingInvModule {
}
