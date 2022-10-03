import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { SgiRoutes } from '@core/route';
import {
  ConvocatoriaConceptoGastoPublicEditarComponent
} from './convocatoria-concepto-gasto-editar/convocatoria-concepto-gasto-public-editar.component';
import { ConvocatoriaConceptoGastoCodigoEcPublicComponent } from './convocatoria-concepto-gasto-formulario/convocatoria-concepto-gasto-codigo-ec/convocatoria-concepto-gasto-codigo-ec-public.component';
import { ConvocatoriaConceptoGastoDatosGeneralesPublicComponent } from './convocatoria-concepto-gasto-formulario/convocatoria-concepto-gasto-datos-generales/convocatoria-concepto-gasto-datos-generales-public.component';
import { ConvocatoriaConceptoGastoPublicDataResolver, CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY } from './convocatoria-concepto-gasto-public-data.resolver';
import { CONVOCATORIA_CONCEPTO_GASTO_PUBLIC_ROUTE_NAMES } from './convocatoria-concepto-gasto-public-route-names';
import { CONVOCATORIA_CONCEPTO_GASTO_PUBLIC_ROUTE_PARAMS } from './convocatoria-concepto-gasto-public-route-params';

const routes: SgiRoutes = [
  {
    path: `:${CONVOCATORIA_CONCEPTO_GASTO_PUBLIC_ROUTE_PARAMS.ID}`,
    component: ConvocatoriaConceptoGastoPublicEditarComponent,
    canDeactivate: [ActionGuard],
    resolve: {
      [CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY]: ConvocatoriaConceptoGastoPublicDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: CONVOCATORIA_CONCEPTO_GASTO_PUBLIC_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: CONVOCATORIA_CONCEPTO_GASTO_PUBLIC_ROUTE_NAMES.DATOS_GENERALES,
        component: ConvocatoriaConceptoGastoDatosGeneralesPublicComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_CONCEPTO_GASTO_PUBLIC_ROUTE_NAMES.CODIGOS_ECONOMICOS,
        component: ConvocatoriaConceptoGastoCodigoEcPublicComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ConvocatoriaConceptoGastoPublicRouting { }
