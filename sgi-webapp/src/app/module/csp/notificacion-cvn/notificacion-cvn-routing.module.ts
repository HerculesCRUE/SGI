import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentGuard } from '@core/guards/detail-form.guard';
import { ActionGuard } from '@core/guards/master-form.guard';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { NotificacionCvnDataResolver, NOTIFICACION_DATA_KEY } from './notificacion-cvn-data.resolver';
import { NotificacionCvnEditarComponent } from './notificacion-cvn-editar/notificacion-cvn-editar.component';
import { NotificacionCvnDatosGeneralesComponent } from './notificacion-cvn-formulario/notificacion-cvn-datos-generales/notificacion-cvn-datos-generales.component';
import { NotificacionCvnListadoComponent } from './notificacion-cvn-listado/notificacion-cvn-listado.component';
import { NOTIFICACION_CVN_ROUTE_NAMES } from './notificacion-cvn-route-names';
import { NOTIFICACION_CVN_ROUTE_PARAMS } from './notificacion-cvn-route-params';

const NOTIFICACION_CVN_KEY = marker('csp.notificacion-cvn');
const routes: SgiRoutes = [
  {
    path: '',
    component: NotificacionCvnListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: NOTIFICACION_CVN_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthorityForAnyUO: ['CSP-CVPR-V', 'CSP-CVPR-E'],
    }
  },
  {
    path: `:${NOTIFICACION_CVN_ROUTE_PARAMS.ID}`,
    component: NotificacionCvnEditarComponent,
    canActivate: [SgiAuthGuard],
    canDeactivate: [ActionGuard],
    resolve: {
      [NOTIFICACION_DATA_KEY]: NotificacionCvnDataResolver
    },
    data: {
      title: NOTIFICACION_CVN_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAnyAuthorityForAnyUO: ['CSP-CVPR-E', 'CSP-CVPR-V'],
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: NOTIFICACION_CVN_ROUTE_NAMES.DATOS_GENERALES
      },
      {
        path: NOTIFICACION_CVN_ROUTE_NAMES.DATOS_GENERALES,
        component: NotificacionCvnDatosGeneralesComponent,
        canDeactivate: [FragmentGuard],
      },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class NotificacionCvnRoutingModule { }
