import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';

import { ComentarioModalComponent } from './comentario-modal/comentario-modal.component';

@NgModule({
  declarations: [
    ComentarioModalComponent,
  ],
  imports: [
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule
  ]
})
export class ComentarioModule { }
