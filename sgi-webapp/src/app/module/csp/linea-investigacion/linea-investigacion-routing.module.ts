import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';
import { LineaInvestigacionListadoComponent } from './linea-investigacion-listado/linea-investigacion-listado.component';

const MSG_LISTADO_TITLE = marker('csp.linea-investigacion');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: LineaInvestigacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-LIN-C', 'CSP-LIN-E', 'CSP-LIN-B', 'CSP-LIN-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LineaInvestigacionRoutingModule {
}
