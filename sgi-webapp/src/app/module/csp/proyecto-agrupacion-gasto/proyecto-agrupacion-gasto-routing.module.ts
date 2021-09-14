import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ProyectoAgrupacionGastoCrearComponent } from './proyecto-agrupacion-gasto-crear/proyecto-agrupacion-gasto-crear.component';
import { ProyectoAgrupacionGastoDataResolver, PROYECTO_AGRUPACION_GASTO_DATA_KEY } from './proyecto-agrupacion-gasto-data.resolver';
import { ProyectoAgrupacionGastoEditarComponent } from './proyecto-agrupacion-gasto-editar/proyecto-agrupacion-gasto-editar.component';
import { AgrupacionGastoConceptoComponent } from './proyecto-agrupacion-gasto-formulario/agrupacion-gasto-concepto/agrupacion-gasto-concepto.component';
import { ProyectoAgrupacionGastoDatosGeneralesComponent } from './proyecto-agrupacion-gasto-formulario/proyecto-agrupacion-gasto-datos-generales/proyecto-agrupacion-gasto-datos-generales.component';
import { PROYECTO_AGRUPACION_GASTO_ROUTE_NAMES } from './proyecto-agrupacion-gasto-route-names';
import { PROYECTO_AGRUPACION_GASTO_ROUTE_PARAMS } from './proyecto-agrupacion-gasto-route-params';

const MSG_NEW_TITLE = marker('title.new.entity');
const PROYECTO_AGRUPACION_GASTO_KEY = marker('title.csp.proyecto-agrupacion-gasto');

const routes: SgiRoutes = [
  {
    path: `${ROUTE_NAMES.NEW}`,
    component: ProyectoAgrupacionGastoCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: PROYECTO_AGRUPACION_GASTO_KEY, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthorityForAnyUO: 'CSP-PRO-E'
    },
    resolve: {
      [PROYECTO_AGRUPACION_GASTO_DATA_KEY]: ProyectoAgrupacionGastoDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_AGRUPACION_GASTO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PROYECTO_AGRUPACION_GASTO_ROUTE_NAMES.DATOS_GENERALES,
        component: ProyectoAgrupacionGastoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_AGRUPACION_GASTO_ROUTE_NAMES.AGRUPACION_GASTO_CONCEPTO,
        component: AgrupacionGastoConceptoComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  },
  {
    path: `:${PROYECTO_AGRUPACION_GASTO_ROUTE_PARAMS.ID}`,
    component: ProyectoAgrupacionGastoEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: PROYECTO_AGRUPACION_GASTO_KEY,
      hasAuthorityForAnyUO: 'CSP-PRO-E'
    },
    resolve: {
      [PROYECTO_AGRUPACION_GASTO_DATA_KEY]: ProyectoAgrupacionGastoDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_AGRUPACION_GASTO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PROYECTO_AGRUPACION_GASTO_ROUTE_NAMES.DATOS_GENERALES,
        component: ProyectoAgrupacionGastoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_AGRUPACION_GASTO_ROUTE_NAMES.AGRUPACION_GASTO_CONCEPTO,
        component: AgrupacionGastoConceptoComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProyectoAgrupacionGastoRouting {
}
