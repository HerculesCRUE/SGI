import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { TipoProteccionRoutingModule } from './tipo-proteccion-routing.module';
import { PiiTipoProteccionListadoComponent } from './pii-tipo-proteccion-listado/pii-tipo-proteccion-listado.component';
import { PiiTipoProteccionModalComponent } from './pii-tipo-proteccion-modal/pii-tipo-proteccion-modal.component';

@NgModule({
  declarations: [PiiTipoProteccionListadoComponent, PiiTipoProteccionModalComponent],
  imports: [
    CommonModule,
    SharedModule,
    TipoProteccionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ]
})
export class TipoProteccionModule { }
