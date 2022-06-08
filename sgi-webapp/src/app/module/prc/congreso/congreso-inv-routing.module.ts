import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { CongresoListadoComponent } from './congreso-listado/congreso-listado.component';

const MSG_LISTADO_TITLE = marker('prc.congreso.title');
const CONGRESO_KEY = marker('prc.congreso.title');

const routes: SgiRoutes = [
  {
    path: '',
    component: CongresoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PRC-VAL-INV-ER']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CongresoInvRoutingModule {
}
