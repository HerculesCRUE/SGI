import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { AreaTematicaCrearComponent } from './area-tematica-crear/area-tematica-crear.component';
import { AreaTematicaEditarComponent } from './area-tematica-editar/area-tematica-editar.component';
import { AreaTematicaArbolComponent } from './area-tematica-formulario/area-tematica-arbol/area-tematica-arbol.component';
import { AreaTematicaDatosGeneralesComponent } from './area-tematica-formulario/area-tematica-datos-generales/area-tematica-datos-generales.component';
import { AreaTematicaListadoComponent } from './area-tematica-listado/area-tematica-listado.component';
import { AREA_TEMATICA_ROUTE_NAMES } from './area-tematica-route-names';
import { AreaTematicaResolver } from './area-tematica.resolver';

const MSG_NEW_TITLE = marker('title.new.entity');
const AREA_TEMATICA_KEY = marker('csp.area-tematica');

const routes: SgiRoutes = [
  {
    path: '',
    component: AreaTematicaListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: AREA_TEMATICA_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-AREA-V', 'CSP-AREA-C', 'CSP-AREA-E', 'CSP-AREA-B', 'CSP-AREA-R']
    },
  },
  {
    path: ROUTE_NAMES.NEW,
    component: AreaTematicaCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: AREA_TEMATICA_KEY, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAnyAuthority: ['CSP-AREA-C']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: AREA_TEMATICA_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: AREA_TEMATICA_ROUTE_NAMES.DATOS_GENERALES,
        component: AreaTematicaDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: AREA_TEMATICA_ROUTE_NAMES.AREAS_ARBOL,
        component: AreaTematicaArbolComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  },
  {
    path: `:id`,
    component: AreaTematicaEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      area: AreaTematicaResolver
    },
    data: {
      title: AREA_TEMATICA_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthority: ['CSP-AREA-E']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: AREA_TEMATICA_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: AREA_TEMATICA_ROUTE_NAMES.DATOS_GENERALES,
        component: AreaTematicaDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: AREA_TEMATICA_ROUTE_NAMES.AREAS_ARBOL,
        component: AreaTematicaArbolComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AreaTematicaRoutingModule {
}
