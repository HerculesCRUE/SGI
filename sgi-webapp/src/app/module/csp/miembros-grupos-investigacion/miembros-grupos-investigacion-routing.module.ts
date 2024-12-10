import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiAuthGuard, SgiAuthRoutes } from '@sgi/framework/auth';
import { MiembrosGruposInvestigacionListadoComponent } from './miembros-grupos-investigacion-listado/miembros-grupos-investigacion-listado.component';

const MSG_LISTADO_TITLE = marker('csp.miembros-grupos-investigacion.listado');

const routes: SgiAuthRoutes = [
  {
    path: '',
    component: MiembrosGruposInvestigacionListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthorityForAnyUO: ['CSP-GIN-E', 'CSP-GIN-V']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MiembrosGruposInvestigacionRoutingModule {
}
