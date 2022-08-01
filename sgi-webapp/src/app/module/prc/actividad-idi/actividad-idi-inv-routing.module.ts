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
import { ActividadIdiEditarComponent } from './actividad-idi-editar/actividad-idi-editar.component';
import { ActividadIdiDatosGeneralesComponent } from './actividad-idi-formulario/actividad-idi-datos-generales/actividad-idi-datos-generales.component';
import { ActividadIdiListadoComponent } from './actividad-idi-listado/actividad-idi-listado.component';
import { ACTIVIDAD_IDI_ROUTE_NAMES } from './actividad-idi-route-names';

const MSG_LISTADO_TITLE = marker('prc.actividad-idi');
const ACTIVIDAD_KEY = marker('prc.actividad-idi');

const routes: SgiRoutes = [
  {
    path: '',
    component: ActividadIdiListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAuthority: 'PRC-VAL-INV-ER'
    }
  },
  {
    path: `:${PRODUCCION_CIENTIFICA_ROUTE_PARAMS.ID}`,
    component: ActividadIdiEditarComponent,
    canActivate: [SgiAuthGuard, ProduccionCientificaInvGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: ACTIVIDAD_KEY,
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
        redirectTo: ACTIVIDAD_IDI_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: ACTIVIDAD_IDI_ROUTE_NAMES.DATOS_GENERALES,
        component: ActividadIdiDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ActividadIdiInvRoutingModule {
}
