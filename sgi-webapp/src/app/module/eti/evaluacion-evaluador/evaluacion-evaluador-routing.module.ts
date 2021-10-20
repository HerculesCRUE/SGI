import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';
import { EvaluacionComentariosComponent } from '../evaluacion-formulario/evaluacion-comentarios/evaluacion-comentarios.component';
import { EvaluacionDatosMemoriaComponent } from '../evaluacion-formulario/evaluacion-datos-memoria/evaluacion-datos-memoria.component';
import { EvaluacionDocumentacionComponent } from '../evaluacion-formulario/evaluacion-documentacion/evaluacion-documentacion.component';
import { EvaluacionEvaluadorEvaluarComponent } from './evaluacion-evaluador-evaluar/evaluacion-evaluador-evaluar.component';
import { EvaluacionEvaluadorListadoComponent } from './evaluacion-evaluador-listado/evaluacion-evaluador-listado.component';
import { EVALUACION_EVALUADOR_ROUTE_NAMES } from './evaluacion-evaluador-route-names';
import { EvaluacionEvaluadorResolver } from './evaluacion-evaluador.resolver';

const MSG_EVALUACION_TITLE = marker('eti.evaluacion');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: EvaluacionEvaluadorListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_EVALUACION_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-INV-VR']
    }
  },
  {
    path: `:id`,
    component: EvaluacionEvaluadorEvaluarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      evaluacion: EvaluacionEvaluadorResolver
    },
    data: {
      title: MSG_EVALUACION_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: EVALUACION_EVALUADOR_ROUTE_NAMES.MEMORIA
      },
      {
        path: EVALUACION_EVALUADOR_ROUTE_NAMES.COMENTARIOS,
        component: EvaluacionComentariosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EVALUACION_EVALUADOR_ROUTE_NAMES.MEMORIA,
        component: EvaluacionDatosMemoriaComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EVALUACION_EVALUADOR_ROUTE_NAMES.DOCUMENTACION,
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
export class EvaluacionEvaluadorRoutingModule {
}
