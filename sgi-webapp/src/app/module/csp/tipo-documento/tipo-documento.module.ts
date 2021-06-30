import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { TipoDocumentoListadoComponent } from './tipo-documento-listado/tipo-documento-listado.component';
import { TipoDocumentoModalComponent } from './tipo-documento-modal/tipo-documento-modal.component';
import { TipoDocumentoRoutingModule } from './tipo-documento-routing.module';

@NgModule({
  declarations: [
    TipoDocumentoListadoComponent,
    TipoDocumentoModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    TipoDocumentoRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ]
})
export class TipoDocumentoModule { }
