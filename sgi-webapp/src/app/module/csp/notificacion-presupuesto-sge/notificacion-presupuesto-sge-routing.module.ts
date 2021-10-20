import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { NotificacionPresupuestoSgeListadoComponent } from './notificacion-presupuesto-sge-listado/notificacion-presupuesto-sge-listado.component';

const NOTIFICACION_PRESUPUESTO_SGE_KEY = marker('menu.csp.notificacion-presupuesto-sge');

const routes: SgiRoutes = [
  {
    path: '',
    component: NotificacionPresupuestoSgeListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: NOTIFICACION_PRESUPUESTO_SGE_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      hasAuthorityForAnyUO: 'CSP-EJEC-E'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class NotificacionPresupuestoSgeRoutingModule {
}
