import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';
import { FacturasPrevistasPendientesListadoComponent } from './facturas-previstas-pendientes-listado/facturas-previstas-pendientes-listado.component';

const MSG_LISTADO_TITLE = marker('csp.facturas-previstas-pendientes.listado');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: FacturasPrevistasPendientesListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthorityForAnyUO: ['CSP-PRO-E', 'CSP-PRO-V']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FacturasPrevistasPendientesRoutingModule {
}
