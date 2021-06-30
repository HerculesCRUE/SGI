import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ProyectoAnualidadCrearComponent } from './proyecto-anualidad-crear/proyecto-anualidad-crear.component';
import { ProyectoAnualidadDataResolver, PROYECTO_ANUALIDAD_DATA_KEY } from './proyecto-anualidad-data.resolver';
import { ProyectoAnualidadEditarComponent } from './proyecto-anualidad-editar/proyecto-anualidad-editar.component';
import { ProyectoAnualidadDatosGeneralesComponent } from './proyecto-anualidad-formulario/proyecto-anualidad-datos-generales/proyecto-anualidad-datos-generales.component';
import { ProyectoAnualidadGastosComponent } from './proyecto-anualidad-formulario/proyecto-anualidad-gastos/proyecto-anualidad-gastos.component';
import { ProyectoAnualidadIngresosComponent } from './proyecto-anualidad-formulario/proyecto-anualidad-ingresos/proyecto-anualidad-ingresos.component';
import { ProyectoAnualidadResumenComponent } from './proyecto-anualidad-formulario/proyecto-anualidad-resumen/proyecto-anualidad-resumen.component';
import { PROYECTO_ANUALIDAD_ROUTE_NAMES } from './proyecto-anualidad-route-names';
import { PROYECTO_ANUALIDAD_ROUTE_PARAMS } from './proyecto-anualidad-route-params';

const MSG_NEW_TITLE = marker('title.new.entity');
const MSG_EDIT_TITLE = marker('csp.proyecto-presupuesto.anualidad');

const routes: SgiRoutes = [
  {
    path: `${ROUTE_NAMES.NEW}`,
    component: ProyectoAnualidadCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: MSG_EDIT_TITLE, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthorityForAnyUO: 'CSP-PRO-E'
    },
    resolve: {
      [PROYECTO_ANUALIDAD_DATA_KEY]: ProyectoAnualidadDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_ANUALIDAD_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PROYECTO_ANUALIDAD_ROUTE_NAMES.DATOS_GENERALES,
        component: ProyectoAnualidadDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ANUALIDAD_ROUTE_NAMES.GASTOS,
        component: ProyectoAnualidadGastosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ANUALIDAD_ROUTE_NAMES.INGRESOS,
        component: ProyectoAnualidadIngresosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ANUALIDAD_ROUTE_NAMES.RESUMEN,
        component: ProyectoAnualidadResumenComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${PROYECTO_ANUALIDAD_ROUTE_PARAMS.ID}`,
    component: ProyectoAnualidadEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_EDIT_TITLE,
      hasAuthorityForAnyUO: 'CSP-PRO-E'
    },
    resolve: {
      [PROYECTO_ANUALIDAD_DATA_KEY]: ProyectoAnualidadDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_ANUALIDAD_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PROYECTO_ANUALIDAD_ROUTE_NAMES.DATOS_GENERALES,
        component: ProyectoAnualidadDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ANUALIDAD_ROUTE_NAMES.GASTOS,
        component: ProyectoAnualidadGastosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ANUALIDAD_ROUTE_NAMES.INGRESOS,
        component: ProyectoAnualidadIngresosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ANUALIDAD_ROUTE_NAMES.RESUMEN,
        component: ProyectoAnualidadResumenComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProyectoAnualidadRouting {
}
