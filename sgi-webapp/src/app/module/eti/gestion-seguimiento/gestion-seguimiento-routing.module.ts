import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';
import { SeguimientoComentariosComponent } from '../seguimiento-formulario/seguimiento-comentarios/seguimiento-comentarios.component';
import { SeguimientoDocumentacionComponent } from '../seguimiento-formulario/seguimiento-documentacion/seguimiento-documentacion.component';
import { SeguimientoEvaluacionComponent } from '../seguimiento-formulario/seguimiento-evaluacion/seguimiento-evaluacion.component';
import { GestionSeguimientoEvaluarComponent } from './gestion-seguimiento-evaluar/gestion-seguimiento-evaluar.component';
import { GestionSeguimientoListadoComponent } from './gestion-seguimiento-listado/gestion-seguimiento-listado.component';
import { GESTION_SEGUIMIENTO_ROUTE_NAMES } from './gestion-seguimiento-route-names';
import { GestionSeguimientoResolver } from './gestion-seguimiento.resolver';

const GESTION_SEGUIMIENTO_KEY = marker('eti.gestion-seguimiento');

const routes: SgiAuthRoutes = [
  {
    path: '',
    pathMatch: 'full',
    component: GestionSeguimientoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: GESTION_SEGUIMIENTO_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['ETI-EVC-V']
    }
  },
  {
    path: `:id`,
    component: GestionSeguimientoEvaluarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      evaluacion: GestionSeguimientoResolver
    },
    data: {
      title: GESTION_SEGUIMIENTO_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['ETI-EVC-EVAL']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: GESTION_SEGUIMIENTO_ROUTE_NAMES.EVALUACIONES
      },
      {
        path: GESTION_SEGUIMIENTO_ROUTE_NAMES.EVALUACIONES,
        component: SeguimientoEvaluacionComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: GESTION_SEGUIMIENTO_ROUTE_NAMES.COMENTARIOS,
        component: SeguimientoComentariosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: GESTION_SEGUIMIENTO_ROUTE_NAMES.DOCUMENTACION,
        component: SeguimientoDocumentacionComponent,
        canDeactivate: [FragmentGuard]
      }

    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class GestionSeguimientoRoutingModule {
}
