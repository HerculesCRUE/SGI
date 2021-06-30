import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { EvaluadorCrearComponent } from './evaluador-crear/evaluador-crear.component';
import { EvaluadorEditarComponent } from './evaluador-editar/evaluador-editar.component';
import { EvaluadorConflictosInteresListadoComponent } from './evaluador-formulario/evaluador-conflictos-interes/evaluador-conflictos-interes-listado/evaluador-conflictos-interes-listado.component';
import { EvaluadorDatosGeneralesComponent } from './evaluador-formulario/evaluador-datos-generales/evaluador-datos-generales.component';
import { EvaluadorListadoComponent } from './evaluador-listado/evaluador-listado.component';
import { EVALUADOR_ROUTE_NAMES } from './evaluador-route-names';
import { EvaluadorResolver } from './evaluador.resolver';

const EVALUADOR_KEY = marker('eti.evaluador');
const MSG_NEW_TITLE = marker('title.new.entity');

const routes: SgiRoutes = [
  {
    path: '',
    component: EvaluadorListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: EVALUADOR_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAuthorityForAnyUO: 'ETI-EVR-V'
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: EvaluadorCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: EVALUADOR_KEY, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthorityForAnyUO: 'ETI-EVR-C'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: EVALUADOR_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: EVALUADOR_ROUTE_NAMES.DATOS_GENERALES,
        component: EvaluadorDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EVALUADOR_ROUTE_NAMES.CONFLICTOS_INTERES,
        component: EvaluadorConflictosInteresListadoComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:id`,
    component: EvaluadorEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      evaluador: EvaluadorResolver
    },
    data: {
      title: EVALUADOR_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAuthorityForAnyUO: 'ETI-EVR-E'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: EVALUADOR_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: EVALUADOR_ROUTE_NAMES.DATOS_GENERALES,
        component: EvaluadorDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EVALUADOR_ROUTE_NAMES.CONFLICTOS_INTERES,
        component: EvaluadorConflictosInteresListadoComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EvaluadorRoutingModule {
}
