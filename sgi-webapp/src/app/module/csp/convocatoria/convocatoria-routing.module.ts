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
import { ConvocatoriaCrearComponent } from './convocatoria-crear/convocatoria-crear.component';
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
import { ConvocatoriaListadoComponent } from './convocatoria-listado/convocatoria-listado.component';
import { CONVOCATORIA_ROUTE_NAMES } from './convocatoria-route-names';
import { CONVOCATORIA_ROUTE_PARAMS } from './convocatoria-route-params';

const CONVOCATORIA_KEY = marker('csp.convocatoria');
const CONVOCATORIA_ELEGIBILIDAD_KEY = marker('csp.convocatoria-elegibilidad');
const MSG_NEW_TITLE = marker('title.new.entity');

const routes: SgiRoutes = [
  {
    path: '',
    component: ConvocatoriaListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: CONVOCATORIA_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['CSP-CON-V', 'CSP-CON-C', 'CSP-CON-E', 'CSP-CON-B', 'CSP-CON-R'],
      module: Module.CSP
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: ConvocatoriaCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: CONVOCATORIA_KEY, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR,
      },
      hasAuthorityForAnyUO: 'CSP-CON-C',
      module: Module.CSP
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
        canDeactivate: [FragmentGuard],
      },
      {
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
        path: CONVOCATORIA_ROUTE_NAMES.SEGUIMIENTO_CIENTIFICO,
        component: ConvocatoriaSeguimientoCientificoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.ENLACES,
        component: ConvocatoriaEnlaceComponent,
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
    ]
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
      title: CONVOCATORIA_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['CSP-CON-V', 'CSP-CON-E'],
      module: Module.CSP
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
      title: CONVOCATORIA_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      module: Module.CSP
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
          permitido: true,
          module: Module.CSP
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
          permitido: false,
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
export class ConvocatoriaRoutingModule {
}
