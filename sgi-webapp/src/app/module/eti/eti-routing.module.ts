import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { Module } from '@core/module';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { EtiInicioComponent } from './eti-inicio/eti-inicio.component';
import { EtiRootComponent } from './eti-root/eti-root.component';
import { ETI_ROUTE_NAMES } from './eti-route-names';

const MSG_ROOT_TITLE = marker('eti.root.title');
const MSG_SOLICITUDES_CONVOCATORIA_TITLE = marker('eti.convocatoria-reunion');
const MSG_EVALUACIONES_EVALUADOR_TITLE = marker('menu.eti.evaluacion-evaluador');
const MSG_EVALUACIONES_TITLE = marker('eti.evaluacion');
const MSG_ACTAS_TITLE = marker('eti.acta');
const MSG_EVALUADORES_TITLE = marker('eti.evaluador');
const MSG_MEMORIAS_TITLE = marker('eti.memoria');
const MSG_SEGUIMIENTOS_TITLE = marker('menu.eti.seguimiento-evaluador');
const MSG_PETICIONES_EVALUACION_TITLE = marker('eti.peticion-evaluacion');
const MSG_GESTION_SEGUIMIENTO_TITLE = marker('eti.seguimiento');
const MSG_CONFIGURACION_TITLE = marker('eti.configuracion');

const routes: SgiRoutes = [
  {
    path: '',
    component: EtiRootComponent,
    data: {
      title: MSG_ROOT_TITLE
    },
    children: [
      {
        path: '',
        component: EtiInicioComponent,
        pathMatch: 'full',
        data: {
          title: MSG_ROOT_TITLE
        }
      },
      {
        path: ETI_ROUTE_NAMES.SOLICITUDES_CONVOCATORIA,
        loadChildren: () =>
          import('./convocatoria-reunion/convocatoria-reunion.module').then(
            (m) => m.ConvocatoriaReunionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_SOLICITUDES_CONVOCATORIA_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['ETI-CNV-V', 'ETI-CNV-C', 'ETI-CNV-E', 'ETI-CNV-B', 'ETI-CNV-ENV'],
          module: Module.ETI
        }
      },
      {
        path: ETI_ROUTE_NAMES.EVALUACIONES_EVALUADOR,
        loadChildren: () =>
          import('./evaluacion-evaluador/evaluacion-evaluador.module').then(
            (m) => m.EvaluacionEvaluadorModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_EVALUACIONES_EVALUADOR_TITLE,
          hasAnyAuthorityForAnyUO: [
            'ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR'
          ],
          module: Module.ETI
        }
      },
      {
        path: ETI_ROUTE_NAMES.EVALUACIONES,
        loadChildren: () =>
          import('./evaluacion/evaluacion.module').then(
            (m) => m.EvaluacionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_EVALUACIONES_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: [
            'ETI-EVC-V', 'ETI-EVC-EVAL'
          ],
          module: Module.ETI
        }
      },
      {
        path: ETI_ROUTE_NAMES.ACTAS,
        loadChildren: () =>
          import('./acta/acta.module').then(
            (m) => m.ActaModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_ACTAS_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['ETI-ACT-V', 'ETI-ACT-C', 'ETI-ACT-E', 'ETI-ACT-ER', 'ETI-ACT-DES', 'ETI-ACT-DESR', 'ETI-ACT-FIN'],
          module: Module.ETI
        }
      },
      {
        path: ETI_ROUTE_NAMES.EVALUADORES,
        loadChildren: () =>
          import('./evaluador/evaluador.module').then(
            (m) => m.EvaluadorModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_EVALUADORES_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['ETI-EVR-V', 'ETI-EVR-C', 'ETI-EVR-E', 'ETI-EVR-B'],
          module: Module.ETI
        }
      },
      {
        path: ETI_ROUTE_NAMES.MEMORIAS,
        loadChildren: () =>
          import('./memoria/memoria-ges.module').then(
            (m) => m.MemoriaGesModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_MEMORIAS_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['ETI-MEM-V', 'ETI-MEM-CEST'],
          module: Module.ETI
        }
      },
      {
        path: ETI_ROUTE_NAMES.SEGUIMIENTOS,
        loadChildren: () =>
          import('./seguimiento/seguimiento.module').then(
            (m) => m.SeguimientoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_SEGUIMIENTOS_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR'],
          module: Module.ETI
        }
      },
      {
        path: ETI_ROUTE_NAMES.GESTION_SEGUIMIENTO,
        loadChildren: () =>
          import('./gestion-seguimiento/gestion-seguimiento.module').then(
            (m) => m.GestionSeguimientoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_GESTION_SEGUIMIENTO_TITLE,
          hasAnyAuthorityForAnyUO: ['ETI-EVC-V', 'ETI-EVC-EVAL'],
          module: Module.ETI
        }
      },
      {
        path: ETI_ROUTE_NAMES.PETICIONES_EVALUACION,
        loadChildren: () =>
          import('./peticion-evaluacion/peticion-evaluacion-ges.module').then(
            (m) => m.PeticionEvaluacionGesModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_PETICIONES_EVALUACION_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAuthorityForAnyUO: 'ETI-PEV-V',
          module: Module.ETI
        }
      }, {
        path: ETI_ROUTE_NAMES.CONFIGURACIONES,
        loadChildren: () =>
          import('./configuracion/configuracion.module').then(
            (m) => m.ConfiguracionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_CONFIGURACION_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAuthorityForAnyUO: 'ETI-CNF-E',
          module: Module.ETI
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
export class EtiRoutingModule {
}
