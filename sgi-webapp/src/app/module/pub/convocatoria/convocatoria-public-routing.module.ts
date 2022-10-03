import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ConvocatoriaPublicEditarComponent } from './convocatoria-editar/convocatoria-public-editar.component';
import { ConvocatoriaConceptoGastoPublicComponent } from './convocatoria-formulario/convocatoria-concepto-gasto-public/convocatoria-concepto-gasto-public.component';
import { ConvocatoriaDatosGeneralesPublicComponent } from './convocatoria-formulario/convocatoria-datos-generales-public/convocatoria-datos-generales-public.component';
import { ConvocatoriaDocumentosPublicComponent } from './convocatoria-formulario/convocatoria-documentos-public/convocatoria-documentos-public.component';
import { ConvocatoriaEnlacePublicComponent } from './convocatoria-formulario/convocatoria-enlace-public/convocatoria-enlace-public.component';
import { ConvocatoriaEntidadesConvocantesPublicComponent } from './convocatoria-formulario/convocatoria-entidades-convocantes-public/convocatoria-entidades-convocantes-public.component';
import { ConvocatoriaEntidadesFinanciadorasPublicComponent } from './convocatoria-formulario/convocatoria-entidades-financiadoras-public/convocatoria-entidades-financiadoras-public.component';
import { ConvocatoriaHitosPublicComponent } from './convocatoria-formulario/convocatoria-hitos-public/convocatoria-hitos-public.component';
import { ConvocatoriaPartidaPresupuestariaPublicComponent } from './convocatoria-formulario/convocatoria-partidas-presupuestarias-public/convocatoria-partidas-presupuestarias-public.component';
import { ConvocatoriaPeriodosJustificacionPublicComponent } from './convocatoria-formulario/convocatoria-periodos-justificacion-public/convocatoria-periodos-justificacion-public.component';
import { ConvocatoriaPlazosFasesPublicComponent } from './convocatoria-formulario/convocatoria-plazos-fases-public/convocatoria-plazos-fases-public.component';
import { ConvocatoriaRequisitosEquipoPublicComponent } from './convocatoria-formulario/convocatoria-requisitos-equipo-public/convocatoria-requisitos-equipo-public.component';
import { ConvocatoriaRequisitosIPPublicComponent } from './convocatoria-formulario/convocatoria-requisitos-ip-public/convocatoria-requisitos-ip-public.component';
import { ConvocatoriaSeguimientoCientificoPublicComponent } from './convocatoria-formulario/convocatoria-seguimiento-cientifico-public/convocatoria-seguimiento-cientifico-public.component';
import { ConvocatoriaPublicDataResolver, CONVOCATORIA_PUBLIC_DATA_KEY } from './convocatoria-public-data.resolver';
import { ConvocatoriaPublicListadoComponent } from './convocatoria-public-listado/convocatoria-public-listado.component';
import { CONVOCATORIA_PUBLIC_ROUTE_NAMES } from './convocatoria-public-route-names';
import { CONVOCATORIA_PUBLIC_ROUTE_PARAMS } from './convocatoria-public-route-params';

const CONVOCATORIA_KEY = marker('csp.convocatoria');
const MSG_CONVOCATORIAS_VER_TITLE = marker('inv.convocatoria.ver.titulo');
const CONVOCATORIA_ELEGIBILIDAD_KEY = marker('csp.convocatoria-elegibilidad');

const routes: SgiRoutes = [
  {
    path: '',
    component: ConvocatoriaPublicListadoComponent,
    data: {
      title: CONVOCATORIA_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL
    }
  },
  {
    path: `:${CONVOCATORIA_PUBLIC_ROUTE_PARAMS.ID}`,
    component: ConvocatoriaPublicEditarComponent,
    resolve: {
      [CONVOCATORIA_PUBLIC_DATA_KEY]: ConvocatoriaPublicDataResolver
    },
    data: {
      title: MSG_CONVOCATORIAS_VER_TITLE
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: CONVOCATORIA_PUBLIC_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: CONVOCATORIA_PUBLIC_ROUTE_NAMES.DATOS_GENERALES,
        component: ConvocatoriaDatosGeneralesPublicComponent,
        canDeactivate: [FragmentGuard]
      }, {
        path: CONVOCATORIA_PUBLIC_ROUTE_NAMES.PERIODO_JUSTIFICACION,
        component: ConvocatoriaPeriodosJustificacionPublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_PUBLIC_ROUTE_NAMES.FASES,
        component: ConvocatoriaPlazosFasesPublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_PUBLIC_ROUTE_NAMES.HITOS,
        component: ConvocatoriaHitosPublicComponent
      },
      {
        path: CONVOCATORIA_PUBLIC_ROUTE_NAMES.PARTIDAS_PRESUPUESTARIAS,
        component: ConvocatoriaPartidaPresupuestariaPublicComponent
      },
      {
        path: CONVOCATORIA_PUBLIC_ROUTE_NAMES.ENTIDADES_CONVOCANTES,
        component: ConvocatoriaEntidadesConvocantesPublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_PUBLIC_ROUTE_NAMES.ENTIDADES_FINANCIADORAS,
        component: ConvocatoriaEntidadesFinanciadorasPublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_PUBLIC_ROUTE_NAMES.ENLACES,
        component: ConvocatoriaEnlacePublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_PUBLIC_ROUTE_NAMES.SEGUIMIENTO_CIENTIFICO,
        component: ConvocatoriaSeguimientoCientificoPublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_PUBLIC_ROUTE_NAMES.REQUISITOS_IP,
        component: ConvocatoriaRequisitosIPPublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_PUBLIC_ROUTE_NAMES.ELEGIBILIDAD,
        component: ConvocatoriaConceptoGastoPublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_PUBLIC_ROUTE_NAMES.REQUISITOS_EQUIPO,
        component: ConvocatoriaRequisitosEquipoPublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_PUBLIC_ROUTE_NAMES.DOCUMENTOS,
        component: ConvocatoriaDocumentosPublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_PUBLIC_ROUTE_NAMES.CONCEPTO_GATO_PERMITIDO,
        redirectTo: CONVOCATORIA_PUBLIC_ROUTE_NAMES.ELEGIBILIDAD
      },
      {
        path: CONVOCATORIA_PUBLIC_ROUTE_NAMES.CONCEPTO_GATO_NO_PERMITIDO,
        redirectTo: CONVOCATORIA_PUBLIC_ROUTE_NAMES.ELEGIBILIDAD
      }
    ]
  },
  {
    path: `:${CONVOCATORIA_PUBLIC_ROUTE_PARAMS.ID}`,
    data: {
      title: MSG_CONVOCATORIAS_VER_TITLE
    },
    resolve: {
      [CONVOCATORIA_PUBLIC_DATA_KEY]: ConvocatoriaPublicDataResolver
    },
    children: [
      {
        path: `${CONVOCATORIA_PUBLIC_ROUTE_NAMES.CONCEPTO_GATO_PERMITIDO}`,
        loadChildren: () =>
          import('../convocatoria-concepto-gasto/convocatoria-concepto-gasto-public.module').then(
            (m) => m.ConvocatoriaConceptoGastoPublicModule
          ),
        data: {
          title: CONVOCATORIA_ELEGIBILIDAD_KEY,
          permitido: true
        }
      },
      {
        path: `${CONVOCATORIA_PUBLIC_ROUTE_NAMES.CONCEPTO_GATO_NO_PERMITIDO}`,
        loadChildren: () =>
          import('../convocatoria-concepto-gasto/convocatoria-concepto-gasto-public.module').then(
            (m) => m.ConvocatoriaConceptoGastoPublicModule
          ),
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
  exports: [RouterModule]
})
export class ConvocatoriaPublicRoutingModule {
}
