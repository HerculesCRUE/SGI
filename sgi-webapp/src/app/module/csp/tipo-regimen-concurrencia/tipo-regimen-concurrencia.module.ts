import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { TipoRegimenConcurrenciaListadoComponent } from './tipo-regimen-concurrencia-listado/tipo-regimen-concurrencia-listado.component';
import { TipoRegimenConcurrenciaModalComponent } from './tipo-regimen-concurrencia-modal/tipo-regimen-concurrencia-modal.component';
import { TipoRegimenConcurrenciaRoutingModule } from './tipo-regimen-concurrencia-routing.module';


@NgModule({
  declarations: [
    TipoRegimenConcurrenciaListadoComponent,
    TipoRegimenConcurrenciaModalComponent
  ],
  imports: [
    CommonModule,
    TipoRegimenConcurrenciaRoutingModule,
    SharedModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ],
  exports: [
    TipoRegimenConcurrenciaListadoComponent,
    TipoRegimenConcurrenciaModalComponent
  ]
})
export class TipoRegimenConcurrenciaModule { }
