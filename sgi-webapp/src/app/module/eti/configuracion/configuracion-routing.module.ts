import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ConfiguracionFormularioComponent } from './configuracion-formulario/configuracion-formulario.component';

const MSG_FORMULARIO_TITLE = marker('eti.configuracion');

const routes: SgiRoutes = [
  {
    path: '',
    component: ConfiguracionFormularioComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_FORMULARIO_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAuthorityForAnyUO: 'ETI-CNF-E'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ConfiguracionRoutingModule {
}
