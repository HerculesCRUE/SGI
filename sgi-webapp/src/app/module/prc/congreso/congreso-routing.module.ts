import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { PRODUCCION_CIENTIFICA_DATA_KEY, PRODUCCION_CIENTIFICA_ROUTE_PARAMS } from '../shared/produccion-cientifica-route-params';
import { ProduccionCientificaResolver } from '../shared/produccion-cientifica.resolver';
import { CongresoEditarComponent } from './congreso-editar/congreso-editar.component';
import { CongresoDatosGeneralesComponent } from './congreso-formulario/congreso-datos-generales/congreso-datos-generales.component';
import { CongresoListadoComponent } from './congreso-listado/congreso-listado.component';
import { CONGRESO_ROUTE_NAMES } from './congreso-route-names';

const MSG_LISTADO_TITLE = marker('prc.congreso.title');
const CONGRESO_KEY = marker('prc.congreso.title');

const routes: SgiRoutes = [
  {
    path: '',
    component: CongresoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PRC-VAL-V', 'PRC-VAL-E']
    }
  },
  {
    path: `:${PRODUCCION_CIENTIFICA_ROUTE_PARAMS.ID}`,
    component: CongresoEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: CONGRESO_KEY,
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
        redirectTo: CONGRESO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: CONGRESO_ROUTE_NAMES.DATOS_GENERALES,
        component: CongresoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CongresoRoutingModule {
}
