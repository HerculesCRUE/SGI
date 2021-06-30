import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';
import { EvaluacionComentariosComponent } from '../evaluacion-formulario/evaluacion-comentarios/evaluacion-comentarios.component';
import { EvaluacionDocumentacionComponent } from '../evaluacion-formulario/evaluacion-documentacion/evaluacion-documentacion.component';
import { EvaluacionEvaluacionComponent } from '../evaluacion-formulario/evaluacion-evaluacion/evaluacion-evaluacion.component';
import { EvaluacionEvaluarComponent } from './evaluacion-evaluar/evaluacion-evaluar.component';
import { EvaluacionListadoComponent } from './evaluacion-listado/evaluacion-listado.component';
import { EVALUACION_ROUTE_NAMES } from './evaluacion-route-names';
import { EvaluacionResolver } from './evaluacion.resolver';

const MSG_EVALUACION_TITLE = marker('eti.evaluacion');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: EvaluacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_EVALUACION_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['ETI-EVC-V']
    }
  },
  {
    path: `:id`,
    component: EvaluacionEvaluarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      evaluacion: EvaluacionResolver
    },
    data: {
      title: MSG_EVALUACION_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['ETI-EVC-EVAL']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: EVALUACION_ROUTE_NAMES.EVALUACIONES
      },
      {
        path: EVALUACION_ROUTE_NAMES.EVALUACIONES,
        component: EvaluacionEvaluacionComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EVALUACION_ROUTE_NAMES.COMENTARIOS,
        component: EvaluacionComentariosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EVALUACION_ROUTE_NAMES.DOCUMENTACION,
        component: EvaluacionDocumentacionComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EvaluacionRoutingModule {
}
