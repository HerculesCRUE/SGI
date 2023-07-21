import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { RolEquipoCrearComponent } from './rol-equipo-crear/rol-equipo-crear.component';
import { RolEquipoEditarComponent } from './rol-equipo-editar/rol-equipo-editar.component';
import { RolEquipoColectivosComponent } from './rol-equipo-formulario/rol-equipo-colectivos/rol-equipo-colectivos.component';
import { RolEquipoDatosGeneralesComponent } from './rol-equipo-formulario/rol-equipo-datos-generales/rol-equipo-datos-generales.component';
import { RolEquipoListadoComponent } from './rol-equipo-listado/rol-equipo-listado.component';
import { ROL_EQUIPO_ROUTE_NAMES } from './rol-equipo-route-names';
import { RolEquipoResolver } from './rol-equipo.resolver';

const ROL_EQUIPO_KEY = marker('csp.rol-equipo');
const MSG_NEW_TITLE = marker('title.new.entity');

const routes: SgiRoutes = [
  {
    path: '',
    component: RolEquipoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: ROL_EQUIPO_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-ROLE-V', 'CSP-ROLE-C', 'CSP-ROLE-E', 'CSP-ROLE-B', 'CSP-ROLE-R']
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: RolEquipoCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: ROL_EQUIPO_KEY, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAnyAuthority: ['CSP-ROLE-C']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: ROL_EQUIPO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: ROL_EQUIPO_ROUTE_NAMES.DATOS_GENERALES,
        component: RolEquipoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: ROL_EQUIPO_ROUTE_NAMES.COLECTIVOS,
        component: RolEquipoColectivosComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  },
  {
    path: `:id`,
    component: RolEquipoEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      rolEquipo: RolEquipoResolver
    },
    data: {
      title: ROL_EQUIPO_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthority: ['CSP-ROLE-E']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: ROL_EQUIPO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: ROL_EQUIPO_ROUTE_NAMES.DATOS_GENERALES,
        component: RolEquipoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: ROL_EQUIPO_ROUTE_NAMES.COLECTIVOS,
        component: RolEquipoColectivosComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RolEquipoRoutingModule { }
