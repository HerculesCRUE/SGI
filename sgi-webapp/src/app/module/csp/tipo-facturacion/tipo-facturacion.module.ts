import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TipoFacturacionRoutingModule } from './tipo-facturacion-routing.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { TipoFacturacionListadoComponent } from './tipo-facturacion-listado/tipo-facturacion-listado.component';
import { TipoFacturacionModalComponent } from './tipo-facturacion-modal/tipo-facturacion-modal.component';


@NgModule({
  declarations: [TipoFacturacionListadoComponent, TipoFacturacionModalComponent],
  imports: [
    CommonModule,
    TipoFacturacionRoutingModule,
    SharedModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ]
})
export class TipoFacturacionModule { }
