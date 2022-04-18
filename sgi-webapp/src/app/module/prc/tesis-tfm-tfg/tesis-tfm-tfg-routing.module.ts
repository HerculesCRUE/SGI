import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { PRODUCCION_CIENTIFICA_ROUTE_PARAMS } from '../shared/produccion-cientifica-route-params';
import { ProduccionCientificaResolver, PRODUCCION_CIENTIFICA_DATA_KEY } from '../shared/produccion-cientifica.resolver';
import { TesisTfmTfgEditarComponent } from './tesis-tfm-tfg-editar/tesis-tfm-tfg-editar.component';
import { TesisTfmTfgDatosGeneralesComponent } from './tesis-tfm-tfg-formulario/tesis-tfm-tfg-datos-generales/tesis-tfm-tfg-datos-generales.component';
import { TesisTfmTfgListadoComponent } from './tesis-tfm-tfg-listado/tesis-tfm-tfg-listado.component';
import { TESIS_TFM_TFG_ROUTE_NAMES } from './tesis-tfm-tfg-route-names';

const MSG_LISTADO_TITLE = marker('prc.tesis-tfm-tfg');
const DIRECCION_TESIS_KEY = marker('prc.tesis-tfm-tfg');

const routes: SgiRoutes = [
  {
    path: '',
    component: TesisTfmTfgListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PRC-VAL-V', 'PRC-VAL-E']
    }
  },
  {
    path: `:${PRODUCCION_CIENTIFICA_ROUTE_PARAMS.ID}`,
    component: TesisTfmTfgEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: DIRECCION_TESIS_KEY,
      titleParams: {
        ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAnyAuthority: ['PRC-VAL-V', 'PRC-VAL-E']
    },
    resolve: {
      [PRODUCCION_CIENTIFICA_DATA_KEY]: ProduccionCientificaResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: TESIS_TFM_TFG_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: TESIS_TFM_TFG_ROUTE_NAMES.DATOS_GENERALES,
        component: TesisTfmTfgDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TesisTfmTfgRoutingModule {
}
