import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { TipoFaseListadoComponent } from './tipo-fase-listado/tipo-fase-listado.component';
import { TipoFaseModalComponent } from './tipo-fase-modal/tipo-fase-modal.component';
import { TipoFaseRoutingModule } from './tipo-fase-routing.module';

@NgModule({
  declarations: [
    TipoFaseListadoComponent,
    TipoFaseModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    TipoFaseRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ],
  providers: [
  ]
})
export class TipoFaseModule { }
