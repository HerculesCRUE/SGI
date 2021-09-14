import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { TipoProcedimientoListadoComponent } from './tipo-procedimiento-listado/tipo-procedimiento-listado.component';

const MSG_LISTADO_TITLE = marker('pii.tipo-procedimiento');

const routes: SgiRoutes = [
  {
    path: '',
    component: TipoProcedimientoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PII-TPR-V', 'PII-TPR-C', 'PII-TPR-E', 'PII-TPR-B', 'PII-TPR-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TipoProcedimientoRoutingModule {
}
