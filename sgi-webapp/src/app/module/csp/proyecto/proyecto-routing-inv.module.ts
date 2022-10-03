import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { Module } from '@core/module';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ProyectoDataResolver, PROYECTO_DATA_KEY } from './proyecto-data.resolver';
import { ProyectoEditarComponent } from './proyecto-editar/proyecto-editar.component';
import { ProyectoCalendarioFacturacionComponent } from './proyecto-formulario/proyecto-calendario-facturacion/proyecto-calendario-facturacion.component';
import { ProyectoFichaGeneralComponent } from './proyecto-formulario/proyecto-datos-generales/proyecto-ficha-general.component';
import { ProyectoListadoInvComponent } from './proyecto-listado-inv/proyecto-listado-inv.component';
import { PROYECTO_ROUTE_NAMES } from './proyecto-route-names';
import { PROYECTO_ROUTE_PARAMS } from './proyecto-route-params';

const MSG_PROYECTOS_TITLE = marker('inv.proyecto.listado.titulo');
const MSG_PROYECTOS_VER_TITLE = marker('inv.proyecto.ver.titulo');

const routes: SgiRoutes = [
  {
    path: '',
    component: ProyectoListadoInvComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_PROYECTOS_TITLE,
      hasAuthorityForAnyUO: 'CSP-PRO-INV-VR',
      module: Module.INV
    }
  },
  {
    path: `:${PROYECTO_ROUTE_PARAMS.ID}`,
    component: ProyectoEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      [PROYECTO_DATA_KEY]: ProyectoDataResolver
    },
    data: {
      title: MSG_PROYECTOS_VER_TITLE,
      hasAuthorityForAnyUO: 'CSP-PRO-INV-VR',
      module: Module.INV
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PROYECTO_ROUTE_NAMES.FICHA_GENERAL
      },
      {
        path: PROYECTO_ROUTE_NAMES.FICHA_GENERAL,
        component: ProyectoFichaGeneralComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PROYECTO_ROUTE_NAMES.CALENDARIO_FACTURACION,
        component: ProyectoCalendarioFacturacionComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProyectoRoutingInvModule {
}
