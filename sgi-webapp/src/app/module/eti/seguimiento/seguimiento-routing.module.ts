import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';
import { SeguimientoComentariosComponent } from '../seguimiento-formulario/seguimiento-comentarios/seguimiento-comentarios.component';
import { SeguimientoDatosMemoriaComponent } from '../seguimiento-formulario/seguimiento-datos-memoria/seguimiento-datos-memoria.component';
import { SeguimientoDocumentacionComponent } from '../seguimiento-formulario/seguimiento-documentacion/seguimiento-documentacion.component';
import { SeguimientoEvaluarComponent } from './seguimiento-evaluar/seguimiento-evaluar.component';
import { SeguimientoListadoComponent } from './seguimiento-listado/seguimiento-listado.component';
import { SEGUIMIENTO_ROUTE_NAMES } from './seguimiento-route-names';
import { SeguimientoResolver } from './seguimiento.resolver';

const SEGUIMIENTO_EVALUADOR_KEY = marker('menu.eti.seguimiento-evaluador');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: SeguimientoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: SEGUIMIENTO_EVALUADOR_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['ETI-EVC-V', 'ETI-EVC-VR', 'ETI-EVC-VR-INV']
    }
  },
  {
    path: ':id',
    component: SeguimientoEvaluarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      evaluacion: SeguimientoResolver
    },
    data: {
      title: SEGUIMIENTO_EVALUADOR_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SEGUIMIENTO_ROUTE_NAMES.MEMORIA
      },
      {
        path: SEGUIMIENTO_ROUTE_NAMES.COMENTARIOS,
        component: SeguimientoComentariosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SEGUIMIENTO_ROUTE_NAMES.MEMORIA,
        component: SeguimientoDatosMemoriaComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SEGUIMIENTO_ROUTE_NAMES.DOCUMENTACION,
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
export class SeguimientoRoutingModule {
}
