import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ViaProteccionListadoComponent } from './via-proteccion-listado/via-proteccion-listado.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { ViaProteccionRoutingModule } from './via-proteccion-routing.module';
import { ViaProteccionModalComponent } from './via-proteccion-modal/via-proteccion-modal.component';

@NgModule({
  declarations: [ViaProteccionListadoComponent, ViaProteccionModalComponent],
  imports: [
    CommonModule,
    SharedModule,
    ViaProteccionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    SgiAuthModule
  ]
})
export class ViaProteccionModule { }
