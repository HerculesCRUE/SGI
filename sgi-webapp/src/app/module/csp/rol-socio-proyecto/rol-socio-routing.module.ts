import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { RolSocioListadoComponent } from './rol-socio-listado/rol-socio-listado.component';

const ROL_SOCIO_PROYECTO_KEY = marker('menu.csp.rol-socio');

const routes: SgiRoutes = [
  {
    path: '',
    component: RolSocioListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: ROL_SOCIO_PROYECTO_KEY,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAnyAuthority: ['CSP-ROLS-V', 'CSP-ROLS-C', 'CSP-ROLS-E', 'CSP-ROLS-B', 'CSP-ROLS-R']
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RolSocioRoutingModule { }
