import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { ROUTE_NAMES } from '@core/route.names';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { TipoProteccionCrearComponent } from './tipo-proteccion-crear/tipo-proteccion-crear.component';
import { TipoProteccionEditarComponent } from './tipo-proteccion-editar/tipo-proteccion-editar.component';
import { TipoProteccionDatosGeneralesComponent } from './tipo-proteccion-formulario/tipo-proteccion-datos-generales/tipo-proteccion-datos-generales.component';
import { TipoProteccionSubtiposComponent } from './tipo-proteccion-formulario/tipo-proteccion-subtipos/tipo-proteccion-subtipos.component';
import { TipoProteccionListadoComponent } from './tipo-proteccion-listado/tipo-proteccion-listado.component';
import { PII_TIPO_PROTECCION_ROUTE_NAMES } from './tipo-proteccion-route-names';
import { TipoProteccionResolver } from './tipo-proteccion.resolver';

const MSG_LISTADO_TITLE = marker('pii.tipo-proteccion');
const MSG_NEW_TITLE = marker('title.new.entity');

const routes: SgiRoutes = [
  {
    path: '',
    component: TipoProteccionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PII-TPR-V', 'PII-TPR-C', 'PII-TPR-E', 'PII-TPR-B', 'PII-TPR-R']
    }
  },
  {
    path: ROUTE_NAMES.NEW,
    component: TipoProteccionCrearComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    data: {
      title: MSG_NEW_TITLE,
      titleParams: {
        entity: MSG_LISTADO_TITLE, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthority: 'PII-TPR-C'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PII_TIPO_PROTECCION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PII_TIPO_PROTECCION_ROUTE_NAMES.DATOS_GENERALES,
        component: TipoProteccionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PII_TIPO_PROTECCION_ROUTE_NAMES.SUBTIPOS,
        component: TipoProteccionSubtiposComponent,
        canDeactivate: [FragmentGuard]
      }
    ]
  },
  {
    path: `:id`,
    component: TipoProteccionEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      tipoProteccion: TipoProteccionResolver
    },
    data: {
      title: MSG_LISTADO_TITLE,
      titleParams: {
        entity: MSG_LISTADO_TITLE, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
      },
      hasAuthority: 'PII-TPR-E'
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: PII_TIPO_PROTECCION_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: PII_TIPO_PROTECCION_ROUTE_NAMES.DATOS_GENERALES,
        component: TipoProteccionDatosGeneralesComponent,
        canDeactivate: [FragmentGuard]
      },
      {
        path: PII_TIPO_PROTECCION_ROUTE_NAMES.SUBTIPOS,
        component: TipoProteccionSubtiposComponent,
        canDeactivate: [FragmentGuard],
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TipoProteccionRoutingModule {
}
