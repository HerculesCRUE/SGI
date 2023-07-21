import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { TipoAmbitoGeograficoListadoComponent } from './tipo-ambito-geografico-listado/tipo-ambito-geografico-listado.component';

const TIPO_AMBITO_GEOGRAFICO_KEY = marker('csp.tipo-ambito-geografico');

const routes: SgiRoutes = [
  {
    path: '',
    component: TipoAmbitoGeograficoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: TIPO_AMBITO_GEOGRAFICO_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-TAGE-V', 'CSP-TAGE-C', 'CSP-TAGE-E', 'CSP-TAGE-B', 'CSP-TAGE-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TipoAmbitoGeograficoRoutingModule { }
