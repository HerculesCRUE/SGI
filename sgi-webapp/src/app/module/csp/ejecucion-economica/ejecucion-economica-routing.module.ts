import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { EjecucionEconomicaListadoComponent } from './ejecucion-economica-listado/ejecucion-economica-listado.component';

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
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EjecucionEconomicaRoutingModule {
}
