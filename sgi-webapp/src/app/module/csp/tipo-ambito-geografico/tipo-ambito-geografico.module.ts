import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { TipoAmbitoGeograficoListadoComponent } from './tipo-ambito-geografico-listado/tipo-ambito-geografico-listado.component';
import { TipoAmbitoGeograficoModalComponent } from './tipo-ambito-geografico-modal/tipo-ambito-geografico-modal.component';
import { TipoAmbitoGeograficoRoutingModule } from './tipo-ambito-geografico-routing.module';


@NgModule({
  declarations: [TipoAmbitoGeograficoListadoComponent, TipoAmbitoGeograficoModalComponent],
  imports: [
    CommonModule,
    TipoAmbitoGeograficoRoutingModule,
    SharedModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ]
})
export class TipoAmbitoGeograficoModule { }
