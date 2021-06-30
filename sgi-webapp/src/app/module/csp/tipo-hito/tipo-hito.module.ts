import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { TipoHitoListadoComponent } from './tipo-hito-listado/tipo-hito-listado.component';
import { TipoHitoModalComponent } from './tipo-hito-modal/tipo-hito-modal.component';
import { TipoHitoRoutingModule } from './tipo-hito-routing.module';

@NgModule({
  declarations: [
    TipoHitoListadoComponent,
    TipoHitoModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    TipoHitoRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ],
  providers: [
  ]
})
export class TipoHitoModule { }
