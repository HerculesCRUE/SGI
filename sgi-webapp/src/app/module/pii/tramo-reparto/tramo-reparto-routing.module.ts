import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiAuthGuard } from '@sgi/framework/auth';
import { TramoRepartoListadoComponent } from './tramo-reparto-listado/tramo-reparto-listado.component';

const MSG_LISTADO_TITLE = marker('pii.tramo-reparto');

const routes: Routes = [
  {
    path: '',
    component: TramoRepartoListadoComponent,
    canActivate: [SgiAuthGuard],
    data: {
      title: MSG_LISTADO_TITLE,
      hasAnyAuthority: ['PII-VPR-V', 'PII-VPR-C', 'PII-VPR-E', 'PII-VPR-B', 'PII-VPR-R']
    }
  }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TramoRepartoRoutingModule { }
