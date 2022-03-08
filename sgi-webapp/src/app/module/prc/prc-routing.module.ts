import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { PrcInicioComponent } from './prc-inicio/prc-inicio.component';
import { PrcRootComponent } from './prc-root/prc-root.component';
import { PRC_ROUTE_NAMES } from './prc-route-names';

const MSG_ROOT_TITLE = marker('prc.root.title');
const MSG_PUBLICACION_TITLE = marker('prc.publicacion');
const MSG_CONGRESO_TITLE = marker('prc.congreso');
const MSG_OBRA_ARTISTICA_TITLE = marker('prc.obra-artistica');
const MSG_COMITE_EDITORIAL_TITLE = marker('prc.comite-editorial');
const MSG_TESIS_TFM_TFG_TITLE = marker('prc.tesis-tfm-tfg');
const MSG_ACTIVIDAD_IDI_TITLE = marker('prc.actividad-idi');
const MSG_INFORME_TITLE = marker('prc.informe');
const MSG_CONVOCATORIA_TITLE = marker('prc.convocatoria');

const routes: SgiRoutes = [
  {
    path: '',
    component: PrcRootComponent,
    data: {
      title: MSG_ROOT_TITLE
    },
    children: [
      {
        path: '',
        component: PrcInicioComponent,
        pathMatch: 'full',
        data: {
          title: MSG_ROOT_TITLE
        }
      },
      {
        path: PRC_ROUTE_NAMES.PUBLICACION,
        loadChildren: () =>
          import('./publicacion/publicacion.module').then(
            (m) => m.PublicacionModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_PUBLICACION_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['PRC-VAL-V', 'PRC-VAL-E']
        }
      },
      {
        path: PRC_ROUTE_NAMES.CONGRESO,
        loadChildren: () =>
          import('./congreso/congreso.module').then(
            (m) => m.CongresoModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_CONGRESO_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['PRC-VAL-V', 'PRC-VAL-E']
        }
      },
      {
        path: PRC_ROUTE_NAMES.OBRA_ARTISTICA,
        loadChildren: () =>
          import('./obra-artistica/obra-artistica.module').then(
            (m) => m.ObraArtisticaModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_OBRA_ARTISTICA_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['PRC-VAL-V', 'PRC-VAL-E']
        }
      },
      {
        path: PRC_ROUTE_NAMES.COMITE_EDITORIAL,
        loadChildren: () =>
          import('./comite-editorial/comite-editorial.module').then(
            (m) => m.ComiteEditorialModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_COMITE_EDITORIAL_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['PRC-VAL-V', 'PRC-VAL-E']
        }
      },
      {
        path: PRC_ROUTE_NAMES.TESIS_TFM_TFG,
        loadChildren: () =>
          import('./tesis-tfm-tfg/tesis-tfm-tfg.module').then(
            (m) => m.TesisTfmTfgModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_TESIS_TFM_TFG_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['PRC-VAL-V', 'PRC-VAL-E']
        }
      },
      {
        path: PRC_ROUTE_NAMES.ACTIVIDAD_IDI,
        loadChildren: () =>
          import('./actividad-idi/actividad-idi.module').then(
            (m) => m.ActividadIdiModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_ACTIVIDAD_IDI_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['PRC-VAL-V', 'PRC-VAL-E']
        }
      },
      {
        path: PRC_ROUTE_NAMES.INFORME,
        loadChildren: () =>
          import('./informe/informe.module').then(
            (m) => m.InformeModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_INFORME_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['PRC-VAL-V', 'PRC-VAL-E']
        }
      },
      {
        path: PRC_ROUTE_NAMES.CONVOCATORIA,
        loadChildren: () =>
          import('./convocatoria/convocatoria.module').then(
            (m) => m.ConvocatoriaModule
          ),
        canActivate: [SgiAuthGuard],
        data: {
          title: MSG_CONVOCATORIA_TITLE,
          titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
          hasAnyAuthority: ['PRC-VAL-V', 'PRC-VAL-E']
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
export class PrcRoutingModule { }
