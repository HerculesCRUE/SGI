import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { TipoDocumentoListadoComponent } from './tipo-documento-listado/tipo-documento-listado.component';

const MSG_LISTADO_TITLE = marker('csp.tipo-documento');

const routes: SgiRoutes = [
  {
    path: '',
    component: TipoDocumentoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-TDOC-V', 'CSP-TDOC-C', 'CSP-TDOC-E', 'CSP-TDOC-B', 'CSP-TDOC-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TipoDocumentoRoutingModule {
}
