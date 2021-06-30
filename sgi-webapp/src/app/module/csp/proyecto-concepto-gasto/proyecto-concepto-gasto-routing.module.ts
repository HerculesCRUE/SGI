import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ProyectoConceptoGastoCrearComponent } from './proyecto-concepto-gasto-crear/proyecto-concepto-gasto-crear.component';
import { ProyectoConceptoGastoDataResolver, PROYECTO_CONCEPTO_GASTO_DATA_KEY } from './proyecto-concepto-gasto-data.resolver';
import {
  ProyectoConceptoGastoEditarComponent
} from './proyecto-concepto-gasto-editar/proyecto-concepto-gasto-editar.component';
import { ProyectoConceptoGastoCodigoEcComponent } from './proyecto-concepto-gasto-formulario/proyecto-concepto-gasto-codigo-ec/proyecto-concepto-gasto-codigo-ec.component';
import { ProyectoConceptoGastoDatosGeneralesComponent } from './proyecto-concepto-gasto-formulario/proyecto-concepto-gasto-datos-generales/proyecto-concepto-gasto-datos-generales.component';
import { PROYECTO_CONCEPTO_GASTO_ROUTE_NAMES } from './proyecto-concepto-gasto-route-names';
import { PROYECTO_CONCEPTO_GASTO_ROUTE_PARAMS } from './proyecto-concepto-gasto-route-params';

const routes: SgiRoutes = [
  {
    path: `${ROUTE_NAMES.NEW}`,
    component: ProyectoConceptoGastoCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      hasAuthorityForAnyUO: 'CSP-PRO-E'
    },
    resolve: {
      [PROYECTO_CONCEPTO_GASTO_DATA_KEY]: ProyectoConceptoGastoDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_CONCEPTO_GASTO_ROUTE_NAMES.DATOS_GENERALES,
      },
      {
        path: PROYECTO_CONCEPTO_GASTO_ROUTE_NAMES.DATOS_GENERALES,
        component: ProyectoConceptoGastoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]

      },
      {
        path: PROYECTO_CONCEPTO_GASTO_ROUTE_NAMES.CODIGOS_ECONOMICOS,
        component: ProyectoConceptoGastoCodigoEcComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${PROYECTO_CONCEPTO_GASTO_ROUTE_PARAMS.ID}`,
    component: ProyectoConceptoGastoEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      hasAnyAuthorityForAnyUO: ['CSP-PRO-E', 'CSP-PRO-V']
    },
    resolve: {
      [PROYECTO_CONCEPTO_GASTO_DATA_KEY]: ProyectoConceptoGastoDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_CONCEPTO_GASTO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PROYECTO_CONCEPTO_GASTO_ROUTE_NAMES.DATOS_GENERALES,
        component: ProyectoConceptoGastoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_CONCEPTO_GASTO_ROUTE_NAMES.CODIGOS_ECONOMICOS,
        component: ProyectoConceptoGastoCodigoEcComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProyectoConceptoGastoRouting { }
