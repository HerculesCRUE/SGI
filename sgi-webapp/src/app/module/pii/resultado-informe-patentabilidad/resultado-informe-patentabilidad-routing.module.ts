import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ResultadoInformePatentabilidadListadoComponent } from './resultado-informe-patentabilidad-listado/resultado-informe-patentabilidad-listado.component';

const MSG_LISTADO_TITLE = marker('pii.resultado-informe-patentabilidad');

const routes: SgiRoutes = [
  {
    path: '',
    component: ResultadoInformePatentabilidadListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PII-RIP-V', 'PII-RIP-C', 'PII-RIP-E', 'PII-RIP-B', 'PII-RIP-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ResultadoInformePatentabilidadRoutingModule {
}
