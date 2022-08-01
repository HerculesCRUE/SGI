import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ProduccionCientificaInvGuard } from '../shared/produccion-cientifica-inv.guard';
import { ProduccionCientificaInvResolver } from '../shared/produccion-cientifica-inv.resolver';
import { PRODUCCION_CIENTIFICA_DATA_KEY, PRODUCCION_CIENTIFICA_ROUTE_PARAMS } from '../shared/produccion-cientifica-route-params';
import { ComiteEditorialEditarComponent } from './comite-editorial-editar/comite-editorial-editar.component';
import { ComiteEditorialDatosGeneralesComponent } from './comite-editorial-formulario/comite-editorial-datos-generales/comite-editorial-datos-generales.component';
import { ComiteEditorialListadoComponent } from './comite-editorial-listado/comite-editorial-listado.component';
import { COMITE_EDITORIAL_ROUTE_NAMES } from './comite-editorial-route-names';

const MSG_LISTADO_TITLE = marker('prc.comite-editorial');
const COMITE_EDITORIAL_KEY = marker('prc.comite-editorial');

const routes: SgiRoutes = [
  {
    path: '',
    component: ComiteEditorialListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAuthority: 'PRC-VAL-INV-ER'
    }
  },
  {
    path: `:${PRODUCCION_CIENTIFICA_ROUTE_PARAMS.ID}`,
    component: ComiteEditorialEditarComponent,
    canActivate: [SgiAuthGuard, ProduccionCientificaInvGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: COMITE_EDITORIAL_KEY,
      titleParams: {
        ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthority: 'PRC-VAL-INV-ER'
    },
    resolve: {
      [PRODUCCION_CIENTIFICA_DATA_KEY]: ProduccionCientificaInvResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: COMITE_EDITORIAL_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: COMITE_EDITORIAL_ROUTE_NAMES.DATOS_GENERALES,
        component: ComiteEditorialDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ComiteEditorialInvRoutingModule {
}
