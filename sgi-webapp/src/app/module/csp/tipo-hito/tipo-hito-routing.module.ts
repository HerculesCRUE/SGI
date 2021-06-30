import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { TipoHitoListadoComponent } from './tipo-hito-listado/tipo-hito-listado.component';

const MSG_LISTADO_TITLE = marker('csp.tipo-hito');

const routes: SgiRoutes = [
  {
    path: '',
    component: TipoHitoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-THITO-V', 'CSP-THITO-C', 'CSP-THITO-E', 'CSP-THITO-B', 'CSP-THITO-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TipoHitoRoutingModule {
}
