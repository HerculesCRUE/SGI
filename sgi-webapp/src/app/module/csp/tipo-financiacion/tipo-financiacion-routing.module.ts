import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { TipoFinanciacionListadoComponent } from './tipo-financiacion-listado/tipo-financiacion-listado.component';

const MSG_LISTADO_TITLE = marker('menu.csp.configuraciones.tipos-financiacion');

const routes: SgiRoutes = [
  {
    path: '',
    component: TipoFinanciacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['CSP-TFNA-V', 'CSP-TFNA-C', 'CSP-TFNA-E', 'CSP-TFNA-B', 'CSP-TFNA-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TipoFinanciacionRoutingModule {
}
