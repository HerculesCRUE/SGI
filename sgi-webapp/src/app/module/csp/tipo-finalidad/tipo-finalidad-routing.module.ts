import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { TipoFinalidadListadoComponent } from './tipo-finalidad-listado/tipo-finalidad-listado.component';

const MSG_LISTADO_TITLE = marker('csp.tipo-finalidad');

const routes: SgiRoutes = [
  {
    path: '',
    component: TipoFinalidadListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-TFIN-V', 'CSP-TFIN-C', 'CSP-TFIN-E', 'CSP-TFIN-B', 'CSP-TFIN-R']
    }
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TipoFinalidadRoutingModule {
}
