import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { EjecucionEconomicaDataResolver, EJECUCION_ECONOMICA_DATA_KEY } from './ejecucion-economica-data.resolver';
import { EjecucionEconomicaEditarComponent } from './ejecucion-economica-editar/ejecucion-economica-editar.component';
import { DetalleOperacionesGastosComponent } from './ejecucion-economica-formulario/detalle-operaciones-gastos/detalle-operaciones-gastos.component';
import { DetalleOperacionesIngresosComponent } from './ejecucion-economica-formulario/detalle-operaciones-ingresos/detalle-operaciones-ingresos.component';
import { DetalleOperacionesModificacionesComponent } from './ejecucion-economica-formulario/detalle-operaciones-modificaciones/detalle-operaciones-modificaciones.component';
import { EjecucionPresupuestariaEstadoActualComponent } from './ejecucion-economica-formulario/ejecucion-presupuestaria-estado-actual/ejecucion-presupuestaria-estado-actual.component';
import { EjecucionPresupuestariaGastosComponent } from './ejecucion-economica-formulario/ejecucion-presupuestaria-gastos/ejecucion-presupuestaria-gastos.component';
import { EjecucionPresupuestariaIngresosComponent } from './ejecucion-economica-formulario/ejecucion-presupuestaria-ingresos/ejecucion-presupuestaria-ingresos.component';
import { FacturasEmitidasComponent } from './ejecucion-economica-formulario/facturas-emitidas/facturas-emitidas.component';
import { FacturasGastosComponent } from './ejecucion-economica-formulario/facturas-gastos/facturas-gastos.component';
import { PersonalContratadoComponent } from './ejecucion-economica-formulario/personal-contratado/personal-contratado.component';
import { ProyectosComponent } from './ejecucion-economica-formulario/proyectos/proyectos.component';
import { ValidacionGastosComponent } from './ejecucion-economica-formulario/validacion-gastos/validacion-gastos.component';
import { ViajesDietasComponent } from './ejecucion-economica-formulario/viajes-dietas/viajes-dietas.component';
import { EjecucionEconomicaListadoComponent } from './ejecucion-economica-listado/ejecucion-economica-listado.component';
import { EJECUCION_ECONOMICA_ROUTE_NAMES } from './ejecucion-economica-route-names';
import { EJECUCION_ECONOMICA_ROUTE_PARAMS } from './ejecucion-economica-route-params';

const EJECUCION_ECONOMICA_KEY = marker('menu.csp.ejecucion-economica');

const routes: SgiRoutes = [
  {
    path: '',
    component: EjecucionEconomicaListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: EJECUCION_ECONOMICA_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['CSP-EJEC-V', 'CSP-EJEC-E']
    }
  },
  {
    path: `:${EJECUCION_ECONOMICA_ROUTE_PARAMS.ID}`,
    component: EjecucionEconomicaEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: EJECUCION_ECONOMICA_KEY,
      hasAnyAuthorityForAnyUO: ['CSP-EJEC-V', 'CSP-EJEC-E']
    },
    resolve: {
      [EJECUCION_ECONOMICA_DATA_KEY]: EjecucionEconomicaDataResolver
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: EJECUCION_ECONOMICA_ROUTE_NAMES.PROYECTOS
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.PROYECTOS,
        component: ProyectosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL,
        component: EjecucionPresupuestariaEstadoActualComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.EJECUCION_PRESUPUESTARIA_GASTOS,
        component: EjecucionPresupuestariaGastosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.EJECUCION_PRESUPUESTARIA_INGRESOS,
        component: EjecucionPresupuestariaIngresosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.DETALLE_OPERACIONES_GASTOS,
        component: DetalleOperacionesGastosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.DETALLE_OPERACIONES_INGRESOS,
        component: DetalleOperacionesIngresosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.DETALLE_OPERACIONES_MODIFICACIONES,
        component: DetalleOperacionesModificacionesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.FACTURAS_GASTOS,
        component: FacturasGastosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.VIAJES_DIETAS,
        component: ViajesDietasComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.PERSONAL_CONTRATADO,
        component: PersonalContratadoComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.VALIDACION_GASTOS,
        component: ValidacionGastosComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: EJECUCION_ECONOMICA_ROUTE_NAMES.FACTURAS_EMITIDAS,
        component: FacturasEmitidasComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EjecucionEconomicaRoutingModule {
}
