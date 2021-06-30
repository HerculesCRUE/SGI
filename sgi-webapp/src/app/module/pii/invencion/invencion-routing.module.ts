import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { InvencionListadoComponent } from './invencion-listado/invencion-listado.component';

const MSG_LISTADO_TITLE = marker('menu.pii.invenciones');

const routes: SgiRoutes = [
  {
    path: '',
    component: InvencionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PII-INV-V', 'PII-INV-C', 'PII-INV-E', 'PII-INV-B', 'PII-INV-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class InvencionRoutingModule {
}
