import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ConvocatoriaBaremacionCrearComponent } from './convocatoria-baremacion-crear/convocatoria-baremacion-crear.component';
import { ConvocatoriaBaremacionEditarComponent } from './convocatoria-baremacion-editar/convocatoria-baremacion-editar.component';
import { ConvocatoriaBaremacionBaremosPuntuacionesComponent } from './convocatoria-baremacion-formulario/convocatoria-baremacion-baremos-puntuaciones/convocatoria-baremacion-baremos-puntuaciones.component';
import { ConvocatoriaBaremacionDatosGeneralesComponent } from './convocatoria-baremacion-formulario/convocatoria-baremacion-datos-generales/convocatoria-baremacion-datos-generales.component';
import { ModuladoresRangosComponent } from './convocatoria-baremacion-formulario/moduladores-rangos/moduladores-rangos.component';
import { ConvocatoriaBaremacionListadoComponent } from './convocatoria-baremacion-listado/convocatoria-baremacion-listado.component';
import { CONVOCATORIA_BAREMACION_ROUTE_PARAMS } from './convocatoria-baremacion-params';
import { ConvocatoriaBaremacionResolver, CONVOCATORIA_BAREMACION_DATA_KEY } from './convocatoria-baremacion.resolver';
import { CONVOCATORIA_ROUTE_NAMES } from './convocatoria-route-names';

const MSG_LISTADO_TITLE = marker('prc.convocatoria.title');
const MSG_NEW_TITLE = marker('title.new.entity');

const routes: SgiRoutes = [
  {
    path: '',
    component: ConvocatoriaBaremacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PRC-CON-V', 'PRC-CON-E', 'RC-CON-B', 'RC-CON-R']
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: ConvocatoriaBaremacionCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: MSG_LISTADO_TITLE, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthority: 'PRC-CON-C'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: CONVOCATORIA_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.DATOS_GENERALES,
        component: ConvocatoriaBaremacionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.MODULADORES_RANGOS,
        component: ModuladoresRangosComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:${CONVOCATORIA_BAREMACION_ROUTE_PARAMS.ID}`,
    component: ConvocatoriaBaremacionEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      [CONVOCATORIA_BAREMACION_DATA_KEY]: ConvocatoriaBaremacionResolver
    },
    data: {
      title: MSG_LISTADO_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthority: ['PRC-CON-V', 'PRC-CON-E']
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: CONVOCATORIA_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.DATOS_GENERALES,
        component: ConvocatoriaBaremacionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.BAREMOS_PUNTUACIONES,
        component: ConvocatoriaBaremacionBaremosPuntuacionesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: CONVOCATORIA_ROUTE_NAMES.MODULADORES_RANGOS,
        component: ModuladoresRangosComponent,
        canDeactivate: [FragmentGuard]
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ConvocatoriaRoutingModule {
}
