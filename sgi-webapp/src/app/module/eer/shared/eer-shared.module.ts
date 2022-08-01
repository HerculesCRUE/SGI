import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SelectTipoDocumentoComponent } from './select-tipo-documento/select-tipo-documento.component';
import { SelectSubtipoDocumentoComponent } from './select-subtipo-documento/select-subtipo-documento.component';
import { SharedModule } from '@shared/shared.module';
import { TranslateModule } from '@ngx-translate/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';



@NgModule({
  declarations: [SelectTipoDocumentoComponent, SelectSubtipoDocumentoComponent],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  exports: [SelectTipoDocumentoComponent, SelectSubtipoDocumentoComponent]
})
export class EerSharedModule { }
