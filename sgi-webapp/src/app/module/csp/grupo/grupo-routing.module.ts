import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { GRUPO_ROUTE_PARAMS } from './autorizacion-route-params';
import { GrupoCrearComponent } from './grupo-crear/grupo-crear.component';
import { GrupoDataResolver, GRUPO_DATA_KEY } from './grupo-data.resolver';
import { GrupoEditarComponent } from './grupo-editar/grupo-editar.component';
import { GrupoDatosGeneralesComponent } from './grupo-formulario/grupo-datos-generales/grupo-datos-generales.component';
import { GrupoListadoComponent } from './grupo-listado/grupo-listado.component';
import { GRUPO_ROUTE_NAMES } from './grupo-route-names';

const GRUPO_TITLE_KEY = marker('csp.grupo');
const MSG_NEW_TITLE = marker('title.new.entity');

const routes: SgiRoutes = [
  {
    path: '',
    component: GrupoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: GRUPO_TITLE_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['CSP-GIN-E', 'CSP-GIN-B', 'CSP-GIN-R', 'CSP-GIN-V']
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: GrupoCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: GRUPO_TITLE_KEY, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR,
      },
      hasAuthorityForAnyUO: 'CSP-GIN-C'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: GRUPO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: GRUPO_ROUTE_NAMES.DATOS_GENERALES,
        component: GrupoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard],
      },
    ]
  },
  {
    path: `:${GRUPO_ROUTE_PARAMS.ID}`,
    component: GrupoEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      [GRUPO_DATA_KEY]: GrupoDataResolver
    },
    data: {
      title: GRUPO_TITLE_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['CSP-GIN-E', 'CSP-GIN-V']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: GRUPO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: GRUPO_ROUTE_NAMES.DATOS_GENERALES,
        component: GrupoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard],
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GrupoRoutingModule { }
