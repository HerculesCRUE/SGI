import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { NotificacionCvnListadoComponent } from './notificacion-cvn-listado/notificacion-cvn-listado.component';

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
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class NotificacionCvnRoutingModule { }
