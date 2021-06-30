import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { PlanInvestigacionCrearComponent } from './plan-investigacion-crear/plan-investigacion-crear.component';
import { PlanInvestigacionEditarComponent } from './plan-investigacion-editar/plan-investigacion-editar.component';
import { PlanInvestigacionDatosGeneralesComponent } from './plan-investigacion-formulario/plan-investigacion-datos-generales/plan-investigacion-datos-generales.component';
import { PlanInvestigacionProgramasComponent } from './plan-investigacion-formulario/plan-investigacion-programas/plan-investigacion-programas.component';
import { PlanInvestigacionListadoComponent } from './plan-investigacion-listado/plan-investigacion-listado.component';
import { PLAN_INVESTIGACION_ROUTE_NAMES } from './plan-investigacion-route-names';
import { PlanInvestigacionResolver } from './plan-investigacion.resolver';

const MSG_NEW_TITLE = marker('title.new.entity');
const PLAN_INVESTIGACION_KEY = marker('csp.plan-investigacion');

const routes: SgiRoutes = [
  {
    path: '',
    component: PlanInvestigacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: PLAN_INVESTIGACION_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-PRG-V', 'CSP-PRG-C', 'CSP-PRG-E', 'CSP-PRG-B', 'CSP-PRG-R']
    },
  },
  {
    path: ROUTE_NAMES.NEW,
    component: PlanInvestigacionCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: PLAN_INVESTIGACION_KEY, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthority: 'CSP-PRG-C'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PLAN_INVESTIGACION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PLAN_INVESTIGACION_ROUTE_NAMES.DATOS_GENERALES,
        component: PlanInvestigacionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PLAN_INVESTIGACION_ROUTE_NAMES.PROGRAMAS,
        component: PlanInvestigacionProgramasComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  },
  {
    path: `:id`,
    component: PlanInvestigacionEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      plan: PlanInvestigacionResolver
    },
    data: {
      title: PLAN_INVESTIGACION_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAuthority: 'CSP-PRG-E'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PLAN_INVESTIGACION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PLAN_INVESTIGACION_ROUTE_NAMES.DATOS_GENERALES,
        component: PlanInvestigacionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PLAN_INVESTIGACION_ROUTE_NAMES.PROGRAMAS,
        component: PlanInvestigacionProgramasComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PlanInvestigacionRoutingModule {
}
