import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TipoProcedimientoListadoComponent } from './tipo-procedimiento-listado/tipo-procedimiento-listado.component';
import { TipoProcedimientoRoutingModule } from './tipo-procedimiento-routing.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { TipoProcedimientoModalComponent } from './tipo-procedimiento-modal/tipo-procedimiento-modal.component';

@NgModule({
  declarations: [
    TipoProcedimientoListadoComponent,
    TipoProcedimientoModalComponent],
  imports: [
    CommonModule,
    SharedModule,
    TipoProcedimientoRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ]
})
export class TipoProcedimientoModule { }
