import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { TipoRegimenConcurrenciaListadoComponent } from './tipo-regimen-concurrencia-listado/tipo-regimen-concurrencia-listado.component';

const TIPO_REGIMEN_CONCURRENCIA_KEY = marker('csp.tipo-regimen-concurrencia');

const routes: SgiRoutes = [
  {
    path: '',
    component: TipoRegimenConcurrenciaListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: TIPO_REGIMEN_CONCURRENCIA_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-TRCO-V', 'CSP-TRCO-C', 'CSP-TRCO-E', 'CSP-TRCO-B', 'CSP-TRCO-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TipoRegimenConcurrenciaRoutingModule { }
