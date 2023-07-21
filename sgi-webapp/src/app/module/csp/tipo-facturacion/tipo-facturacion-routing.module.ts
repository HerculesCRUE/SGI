import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { TipoFacturacionListadoComponent } from './tipo-facturacion-listado/tipo-facturacion-listado.component';
import { SgiRoutes } from '@core/route';

const MSG_LISTADO_TITLE = marker('csp.tipo-facturacion');

const routes: SgiRoutes = [
  {
    path: '',
    component: TipoFacturacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-TFAC-V', 'CSP-TFAC-C', 'CSP-TFAC-E', 'CSP-TFAC-B', 'CSP-TFAC-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TipoFacturacionRoutingModule { }
