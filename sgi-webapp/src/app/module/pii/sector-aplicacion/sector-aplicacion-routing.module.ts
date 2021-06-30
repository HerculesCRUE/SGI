import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { SectorAplicacionListadoComponent } from './sector-aplicacion-listado/sector-aplicacion-listado.component';

const MSG_LISTADO_TITLE = marker('menu.pii.configuraciones.sectores-aplicacion');

const routes: SgiRoutes = [
  {
    path: '',
    component: SectorAplicacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PII-SEA-V', 'PII-SEA-C', 'PII-SEA-E', 'PII-SEA-B', 'PII-SEA-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SectorAplicacionRoutingModule {
}
