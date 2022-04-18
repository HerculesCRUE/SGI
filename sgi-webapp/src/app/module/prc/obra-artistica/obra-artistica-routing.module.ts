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
import { ObraArtisticaEditarComponent } from './obra-artistica-editar/obra-artistica-editar.component';
import { ObraArtisticaDatosGeneralesComponent } from './obra-artistica-formulario/obra-artistica-datos-generales/obra-artistica-datos-generales.component';
import { ObraArtisticaListadoComponent } from './obra-artistica-listado/obra-artistica-listado.component';
import { OBRA_ARTISTICA_ROUTE_NAMES } from './obra-artistica-route-names';

const MSG_LISTADO_TITLE = marker('prc.obra-artistica.title');
const OBRA_ARTISTICA_KEY = marker('prc.obra-artistica.title');

const routes: SgiRoutes = [
  {
    path: '',
    component: ObraArtisticaListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PRC-VAL-V', 'PRC-VAL-E']
    }
  },
  {
    path: `:${PRODUCCION_CIENTIFICA_ROUTE_PARAMS.ID}`,
    component: ObraArtisticaEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: OBRA_ARTISTICA_KEY,
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
        redirectTo: OBRA_ARTISTICA_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: OBRA_ARTISTICA_ROUTE_NAMES.DATOS_GENERALES,
        component: ObraArtisticaDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ObraArtisticaRoutingModule {
}
