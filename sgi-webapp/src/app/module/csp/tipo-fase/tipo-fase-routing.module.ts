import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { TipoFaseListadoComponent } from './tipo-fase-listado/tipo-fase-listado.component';

const MSG_LISTADO_TITLE = marker('csp.tipo-fase');

const routes: SgiRoutes = [
  {
    path: '',
    component: TipoFaseListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-TFASE-V', 'CSP-TFASE-C', 'CSP-TFASE-E', 'CSP-TFASE-B', 'CSP-TFASE-R']
    }
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TipoFaseRoutingModule {
}
