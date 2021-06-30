import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';
import { FuenteFinanciacionListadoComponent } from './fuente-financiacion-listado/fuente-financiacion-listado.component';

const MSG_LISTADO_TITLE = marker('menu.csp.configuraciones.fuentes-financiacion');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: FuenteFinanciacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['CSP-FNT-V', 'CSP-FNT-C', 'CSP-FNT-E', 'CSP-FNT-B', 'CSP-FNT-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FuenteFinanciacionRoutingModule {
}
