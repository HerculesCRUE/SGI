import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiRoutes } from '@core/route';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { ChecklistFormularioComponent } from './checklist-formulario/checklist-formulario.component';

const MSG_FORMULARIO_TITLE = marker('eti.checklist');

const routes: SgiRoutes = [
  {
    path: '',
    component: ChecklistFormularioComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_FORMULARIO_TITLE,
      titleParams: MSG_PARAMS.CARDINALIRY.PLURAL,
      hasAuthorityForAnyUO: 'ETI-CHK-E'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ChecklistRoutingModule {
}
