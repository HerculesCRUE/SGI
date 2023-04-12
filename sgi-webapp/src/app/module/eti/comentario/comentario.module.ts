import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { BloqueApartadoPipe } from '../shared/pipes/bloque-apartado.pipe';

import { ComentarioModalComponent } from './comentario-modal/comentario-modal.component';

@NgModule({
  declarations: [
    ComentarioModalComponent,
    BloqueApartadoPipe,
  ],
  imports: [
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SharedModule
  ],
  exports: [
    BloqueApartadoPipe,
  ],
})
export class ComentarioModule { }
