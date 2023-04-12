import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { Module } from '@core/module';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { InvInicioComponent } from './inv-inicio/inv-inicio.component';
import { InvRootComponent } from './inv-root/inv-root.component';
import { INV_ROUTE_NAMES } from './inv-route-names';

const MSG_ROOT_TITLE = marker('inv.root.title');
const MSG_EVALUACIONES_TITLE = marker('eti.evaluacion');
const MSG_SEGUIMIENTOS_TITLE = marker('eti.seguimiento');
const MSG_PETICIONES_EVALUACION_TITLE = marker('menu.principal.inv.peticionesEvaluacion');
const MSG_MEMORIAS_TITLE = marker('menu.principal.inv.memorias');
const MSG_CHECKLIST_TITLE = marker('menu.principal.inv.autoevaluacion');
const MSG_ACTAS_TITLE = marker('eti.acta');
const MSG_CONVOCATORIAS_TITLE = marker('menu.principal.inv.convocatorias');
const MSG_PROYECTOS_TITLE = marker('menu.principal.inv.proyectos');
const MSG_SOLICITUDES_TITLE = marker('menu.principal.inv.solicitudes');
const MSG_AUTORIZACIONES_TITLE = marker('menu.principal.inv.autorizaciones');
const MSG_GRUPO_TITLE = marker('csp.grupo');
const MSG_PUBLICACION_TITLE = marker('prc.publicacion');
const MSG_CONGRESO_TITLE = marker('prc.congreso.title');
const MSG_COMITE_EDITORIAL_TITLE = marker('prc.comite-editorial');
const MSG_ACTIVIDAD_TITLE = marker('prc.actividad-idi');
const MSG_OBRAS_ARTISTICAS_TITLE = marker('prc.obra-artistica.title');
const MSG_DIRECCION_TESIS_TITLE = marker('prc.tesis-tfm-tfg');
const MSG_VALIDACION_TUTOR_TITLE = marker('menu.principal.inv.validacion-tutor');

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
          hasAnyAuthorityForAnyUO: ['ETI-EVC-INV-VR', 'ETI-EVC-INV-EVALR'],
          module: Module.INV
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
          hasAnyAuthorityForAnyUO: ['ETI-EVC-INV-VR', 'ETI-EVC-INV-EVALR'],
          module: Module.INV
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
          hasAnyAuthorityForAnyUO: ['ETI-PEV-INV-VR', 'ETI-PEV-INV-C', 'ETI-PEV-INV-ER', 'ETI-PEV-INV-BR'],
          module: Module.INV
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
          hasAnyAuthorityForAnyUO: ['ETI-MEM-INV-VR', 'ETI-MEM-INV-ER', 'ETI-MEM-INV-BR', 'ETI-MEM-INV-ESCR', 'ETI-MEM-INV-ERTR'],
          module: Module.INV
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
          hasAuthorityForAnyUO: 'ETI-CHK-INV-E',
          module: Module.INV
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
          hasAnyAuthorityForAnyUO: ['ETI-ACT-INV-ER', 'ETI-ACT-INV-DESR'],
          module: Module.INV
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
          hasAuthorityForAnyUO: 'CSP-CON-INV-V',
          module: Module.INV
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
          module: Module.INV
        }
      },
      {
        path: INV_ROUTE_NAMES.PROYECTOS,
        loadChildren: () =>
          import('../csp/proyecto/proyecto-inv.module').then(
            (m) => m.ProyectoInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_PROYECTOS_TITLE,
          hasAnyAuthorityForAnyUO: ['CSP-PRO-INV-VR'],
          module: Module.INV
        }
      },
      {
        path: INV_ROUTE_NAMES.AUTORIZACIONES,
        loadChildren: () =>
          import('../csp/autorizacion/autorizacion-inv.module').then(
            (m) => m.AutorizacionInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_AUTORIZACIONES_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthorityForAnyUO: ['CSP-AUT-INV-C', 'CSP-AUT-INV-ER', 'CSP-AUT-INV-BR'],
          module: Module.INV
        }
      },
      {
        path: INV_ROUTE_NAMES.GRUPOS,
        loadChildren: () =>
          import('../csp/grupo/grupo-inv.module').then(
            (m) => m.GrupoInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_GRUPO_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAuthorityForAnyUO: 'CSP-GIN-INV-VR',
          module: Module.INV
        }
      },
      {
        path: INV_ROUTE_NAMES.VALIDACION_PUBLICACIONES,
        loadChildren: () =>
          import('../prc/publicacion/publicacion-inv.module').then(
            (m) => m.PublicacionInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_PUBLICACION_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAuthorityForAnyUO: 'PRC-VAL-INV-ER',
          module: Module.INV
        }
      },
      {
        path: INV_ROUTE_NAMES.INFORMES,
        loadChildren: () =>
          import('../prc/informe/informe-inv.module').then(
            (m) => m.InformeInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_PUBLICACION_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAuthorityForAnyUO: 'PRC-INF-INV-GR',
          module: Module.INV
        }
      },
      {
        path: INV_ROUTE_NAMES.VALIDACION_CONGRESOS,
        loadChildren: () =>
          import('../prc/congreso/congreso-inv.module').then(
            (m) => m.CongresoInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_CONGRESO_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAuthorityForAnyUO: 'PRC-VAL-INV-ER',
          module: Module.INV
        }
      },
      {
        path: INV_ROUTE_NAMES.VALIDACION_COMITES_EDITORIALES,
        loadChildren: () =>
          import('../prc/comite-editorial/comite-editorial-inv.module').then(
            (m) => m.ComiteEditorialInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_COMITE_EDITORIAL_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAuthorityForAnyUO: 'PRC-VAL-INV-ER',
          module: Module.INV
        }
      },
      {
        path: INV_ROUTE_NAMES.VALIDACION_ACTIVIDADES_IDI,
        loadChildren: () =>
          import('../prc/actividad-idi/actividad-idi-inv.module').then(
            (m) => m.ActividadIdiInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_ACTIVIDAD_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAuthorityForAnyUO: 'PRC-VAL-INV-ER',
          module: Module.INV
        }
      },
      {
        path: INV_ROUTE_NAMES.VALIDACION_OBRAS_ARTISTICAS,
        loadChildren: () =>
          import('../prc/obra-artistica/obra-artistica-inv.module').then(
            (m) => m.ObraArtisticaInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_OBRAS_ARTISTICAS_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAuthorityForAnyUO: 'PRC-VAL-INV-ER',
          module: Module.INV
        }
      },
      {
        path: INV_ROUTE_NAMES.VALIDACION_TESIS_TFM_TFG,
        loadChildren: () =>
          import('../prc/tesis-tfm-tfg/tesis-tfm-tfg-inv.module').then(
            (m) => m.TesisTfmTfgInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_DIRECCION_TESIS_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
          hasAuthorityForAnyUO: 'PRC-VAL-INV-ER',
          module: Module.INV
        }
      },
      {
        path: INV_ROUTE_NAMES.VALIDACION_TUTOR,
        loadChildren: () =>
          import('../csp/solicitud/solicitud-validacion-tutor-inv.module').then(
            (m) => m.SolicitudValidacionTutorInvModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_VALIDACION_TUTOR_TITLE,
          hasAuthorityForAnyUO: 'CSP-SOL-INV-ER',
          module: Module.INV
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
