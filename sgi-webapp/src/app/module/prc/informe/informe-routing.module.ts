import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { InformeGenerarComponent } from './informe-generar/informe-generar.component';

const MSG_TITLE = marker('prc.informe.reparto-baremacion-grupos');

const routes: SgiRoutes = [
  {
    path: '',
    component: InformeGenerarComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_TITLE,
      hasAuthority: 'PRC-INF-G'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class InformeRoutingModule {
}
