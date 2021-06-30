import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ConvocatoriaConceptoGastoCrearComponent } from './convocatoria-concepto-gasto-crear/convocatoria-concepto-gasto-crear.component';
import { ConvocatoriaConceptoGastoDataResolver, CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY } from './convocatoria-concepto-gasto-data.resolver';
import {
  ConvocatoriaConceptoGastoEditarComponent
} from './convocatoria-concepto-gasto-editar/convocatoria-concepto-gasto-editar.component';
import { ConvocatoriaConceptoGastoCodigoEcComponent } from './convocatoria-concepto-gasto-formulario/convocatoria-concepto-gasto-codigo-ec/convocatoria-concepto-gasto-codigo-ec.component';
import { ConvocatoriaConceptoGastoDatosGeneralesComponent } from './convocatoria-concepto-gasto-formulario/convocatoria-concepto-gasto-datos-generales/convocatoria-concepto-gasto-datos-generales.component';
import { CONVOCATORIA_CONCEPTO_GASTO_ROUTE_NAMES } from './convocatoria-concepto-gasto-route-names';
import { CONVOCATORIA_CONCEPTO_GASTO_ROUTE_PARAMS } from './convocatoria-concepto-gasto-route-params';

const routes: SgiRoutes = [
  {
    path: `${ROUTE_NAMES.NEW}`,
    component: ConvocatoriaConceptoGastoCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      hasAuthorityForAnyUO: 'CSP-CON-E'
    },
    resolve: {
      [CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY]: ConvocatoriaConceptoGastoDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: CONVOCATORIA_CONCEPTO_GASTO_ROUTE_NAMES.DATOS_GENERALES,
      },
      {
        path: CONVOCATORIA_CONCEPTO_GASTO_ROUTE_NAMES.DATOS_GENERALES,
        component: ConvocatoriaConceptoGastoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]

      },
      {
        path: CONVOCATORIA_CONCEPTO_GASTO_ROUTE_NAMES.CODIGOS_ECONOMICOS,
        component: ConvocatoriaConceptoGastoCodigoEcComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${CONVOCATORIA_CONCEPTO_GASTO_ROUTE_PARAMS.ID}`,
    component: ConvocatoriaConceptoGastoEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      hasAnyAuthorityForAnyUO: ['CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V']
    },
    resolve: {
      [CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY]: ConvocatoriaConceptoGastoDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: CONVOCATORIA_CONCEPTO_GASTO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: CONVOCATORIA_CONCEPTO_GASTO_ROUTE_NAMES.DATOS_GENERALES,
        component: ConvocatoriaConceptoGastoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_CONCEPTO_GASTO_ROUTE_NAMES.CODIGOS_ECONOMICOS,
        component: ConvocatoriaConceptoGastoCodigoEcComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ConvocatoriaConceptoGastoRouting { }
