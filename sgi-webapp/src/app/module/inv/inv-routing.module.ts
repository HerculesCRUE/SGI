import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';

import { InvRootComponent } from './inv-root/inv-root.component';
import { INV_ROUTE_NAMES } from './inv-route-names';
import { InvInicioComponent } from './inv-inicio/inv-inicio.component';

const MSG_ROOT_TITLE = marker('inv.root.title');
const MSG_EVALUACIONES_TITLE = marker('menu.principal.inv.evaluaciones');
const MSG_SEGUIMIENTOS_TITLE = marker('menu.principal.inv.seguimientos');
const MSG_PETICIONES_EVALUACION_TITLE = marker('menu.principal.inv.peticionesEvaluacion');
const MSG_MEMORIAS_TITLE = marker('menu.principal.inv.memorias');
const MSG_CONVOCATORIAS_TITLE = marker('menu.principal.inv.convocatorias');

const routes: SgiRoutes = [
  {
    path: '',
    component: InvRootComponent,
    data: {
      title: MSG_ROOT_TITLE
    },
    children: [
      {
        path: '',
        component: InvInicioComponent,
        pathMatch: 'full',
        data: {
          title: MSG_ROOT_TITLE
        }
      },
      {
        path: INV_ROUTE_NAMES.EVALUACIONES,
        loadChildren: () =>
          import('../eti/evaluacion-evaluador/evaluacion-evaluador.module').then(
            (m) => m.EvaluacionEvaluadorModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_EVALUACIONES_TITLE,
          hasAnyAuthorityForAnyUO: ['ETI-EVC-VR-INV', 'ETI-EVC-EVALR-INV']
        }
      },
      {
        path: INV_ROUTE_NAMES.SEGUIMIENTOS,
        loadChildren: () =>
          import('../eti/seguimiento/seguimiento.module').then(
            (m) => m.SeguimientoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_SEGUIMIENTOS_TITLE,
          hasAnyAuthorityForAnyUO: ['ETI-EVC-VR-INV', 'ETI-EVC-EVALR-INV']
        }
      },
      {
        path: INV_ROUTE_NAMES.PETICIONES_EVALUACION,
        loadChildren: () =>
          import('../eti/peticion-evaluacion/peticion-evaluacion-inv.module').then(
            (m) => m.PeticionEvaluacionInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_PETICIONES_EVALUACION_TITLE,
          hasAnyAuthorityForAnyUO: ['ETI-PEV-VR-INV', 'ETI-PEV-C-INV', 'ETI-PEV-ER-INV', 'ETI-PEV-BR-INV']
        }
      },
      {
        path: INV_ROUTE_NAMES.MEMORIAS,
        loadChildren: () =>
          import('../eti/memoria/memoria-inv.module').then(
            (m) => m.MemoriaInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_MEMORIAS_TITLE,
          hasAuthorityForAnyUO: 'ETI-PEV-VR-INV'
        }
      },
      {
        path: INV_ROUTE_NAMES.CONVOCATORIAS,
        loadChildren: () =>
          import('../csp/convocatoria/convocatoria-inv.module').then(
            (m) => m.ConvocatoriaInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_CONVOCATORIAS_TITLE,
          hasAuthorityForAnyUO: 'CSP-CON-INV-V'
        }
      },
      {
        path: INV_ROUTE_NAMES.SOLICITUDES,
        loadChildren: () =>
          import('../csp/solicitud/solicitud-inv.module').then(
            (m) => m.SolicitudInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_CONVOCATORIAS_TITLE,
          hasAnyAuthorityForAnyUO: ['CSP-SOL-INV-ER', 'CSP-SOL-INV-BR'],
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
export class InvRoutingModule {
}
