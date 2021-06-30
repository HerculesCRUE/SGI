import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { TipoFinalidadListadoComponent } from './tipo-finalidad-listado/tipo-finalidad-listado.component';
import { TipoFinalidadModalComponent } from './tipo-finalidad-modal/tipo-finalidad-modal.component';
import { TipoFinalidadRoutingModule } from './tipo-finalidad-routing.module';

@NgModule({
  declarations: [
    TipoFinalidadListadoComponent,
    TipoFinalidadModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    TipoFinalidadRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ]
})
export class TipoFinalidadModule { }
