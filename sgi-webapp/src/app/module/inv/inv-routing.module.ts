import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { InvInicioComponent } from './inv-inicio/inv-inicio.component';
import { InvRootComponent } from './inv-root/inv-root.component';
import { INV_ROUTE_NAMES } from './inv-route-names';


const MSG_ROOT_TITLE = marker('inv.root.title');
const MSG_EVALUACIONES_TITLE = marker('menu.principal.inv.evaluaciones');
const MSG_SEGUIMIENTOS_TITLE = marker('menu.principal.inv.seguimientos');
const MSG_PETICIONES_EVALUACION_TITLE = marker('menu.principal.inv.peticionesEvaluacion');
const MSG_MEMORIAS_TITLE = marker('menu.principal.inv.memorias');
const MSG_CHECKLIST_TITLE = marker('menu.principal.inv.checklist');
const MSG_ACTAS_TITLE = marker('eti.acta');
const MSG_CONVOCATORIAS_TITLE = marker('menu.principal.inv.convocatorias');
const MSG_SOLICITUDES_TITLE = marker('menu.principal.inv.solicitudes');

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
          hasAnyAuthorityForAnyUO: ['ETI-EVC-INV-VR', 'ETI-EVC-INV-EVALR']
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
          hasAnyAuthorityForAnyUO: ['ETI-EVC-INV-VR', 'ETI-EVC-INV-EVALR']
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
          hasAnyAuthorityForAnyUO: ['ETI-PEV-INV-VR', 'ETI-PEV-INV-C', 'ETI-PEV-INV-ER', 'ETI-PEV-INV-BR']
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
          hasAnyAuthorityForAnyUO: ['ETI-MEM-INV-VR', 'ETI-MEM-INV-ER', 'ETI-MEM-INV-BR', 'ETI-MEM-INV-ESCR', 'ETI-MEM-INV-ERTR']
        }
      },
      {
        path: INV_ROUTE_NAMES.CHECKLIST,
        loadChildren: () =>
          import('../eti/checklist/checklist.module').then(
            (m) => m.ChecklistModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_CHECKLIST_TITLE,
          hasAuthorityForAnyUO: 'ETI-CHK-E'
        }
      },
      {
        path: INV_ROUTE_NAMES.ACTAS,
        loadChildren: () =>
          import('../eti/acta/acta.module').then(
            (m) => m.ActaModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_ACTAS_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['ETI-ACT-INV-ER', 'ETI-ACT-INV-DESR']
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
          title: MSG_SOLICITUDES_TITLE,
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
