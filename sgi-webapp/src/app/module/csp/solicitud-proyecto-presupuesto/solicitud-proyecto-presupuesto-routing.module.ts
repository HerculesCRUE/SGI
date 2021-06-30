import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { SolicitudProyectoPresupuestoDataResolver, SOLICITUD_PROYECTO_PRESUPUESTO_DATA_KEY } from './solicitud-proyecto-presupuesto-data.resolver';
import { SolicitudProyectoPresupuestoEditarComponent } from './solicitud-proyecto-presupuesto-editar/solicitud-proyecto-presupuesto-editar.component';
import { SolicitudProyectoPresupuestoDatosGeneralesComponent } from './solicitud-proyecto-presupuesto-formulario/solicitud-proyecto-presupuesto-datos-generales/solicitud-proyecto-presupuesto-datos-generales.component';
import { SolicitudProyectoPresupuestoPartidasGastoComponent } from './solicitud-proyecto-presupuesto-formulario/solicitud-proyecto-presupuesto-partidas-gasto/solicitud-proyecto-presupuesto-partidas-gasto.component';
import { SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_NAMES } from './solicitud-proyecto-presupuesto-route-names';
import { SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_PARAMS } from './solicitud-proyecto-presupuesto-route-params';

const MSG_EDIT_TITLE = marker('menu.csp.proyectos.proyecto-presupuesto');

const routes: SgiRoutes = [
  {
    path: `:${SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_PARAMS.EMPRESA_REF}`,
    component: SolicitudProyectoPresupuestoEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_EDIT_TITLE,
    },
    resolve: {
      [SOLICITUD_PROYECTO_PRESUPUESTO_DATA_KEY]: SolicitudProyectoPresupuestoDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_NAMES.DATOS_GENERALES,
        component: SolicitudProyectoPresupuestoDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: SOLICITUD_PROYECTO_PRESUPUESTO_ROUTE_NAMES.PARTIDAS_GASTO,
        component: SolicitudProyectoPresupuestoPartidasGastoComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SolicitudProyectoPresupuestoRoutingModule {
}
