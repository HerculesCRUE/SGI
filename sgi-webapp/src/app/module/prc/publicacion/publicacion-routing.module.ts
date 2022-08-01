import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionGuard } from '@core/guards/master-form.guard';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { PublicacionEditarComponent } from './publicacion-editar/publicacion-editar.component';
import { PublicacionListadoComponent } from './publicacion-listado/publicacion-listado.component';
import { PUBLICACION_ROUTE_NAMES } from './publicacion-route-names';
import { PublicacionDatosGeneralesComponent } from './publicacion-formulario/publicacion-datos-generales/publicacion-datos-generales.component';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { PRODUCCION_CIENTIFICA_DATA_KEY, PRODUCCION_CIENTIFICA_ROUTE_PARAMS } from '../shared/produccion-cientifica-route-params';
import { ProduccionCientificaResolver } from '../shared/produccion-cientifica.resolver';
import { MSG_PARAMS } from '@core/i18n';

const MSG_LISTADO_TITLE = marker('prc.publicacion-documento-cientifico');
const PUBLICACION_KEY = marker('prc.publicacion-documento-cientifico');

const routes: SgiRoutes = [
  {
    path: '',
    component: PublicacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PRC-VAL-V', 'PRC-VAL-E']
    }
  },
  {
    path: `:${PRODUCCION_CIENTIFICA_ROUTE_PARAMS.ID}`,
    component: PublicacionEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: PUBLICACION_KEY,
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
        redirectTo: PUBLICACION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PUBLICACION_ROUTE_NAMES.DATOS_GENERALES,
        component: PublicacionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PublicacionRoutingModule {
}
