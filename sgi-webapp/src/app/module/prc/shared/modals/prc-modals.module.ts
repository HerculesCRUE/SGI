import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RechazarProduccionCientificaModalComponent } from './rechazar-produccion-cientifica-modal/rechazar-produccion-cientifica-modal.component';
import { SharedModule } from '@shared/shared.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { ReactiveFormsModule } from '@angular/forms';



@NgModule({
  declarations: [RechazarProduccionCientificaModalComponent],
  imports: [
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    MaterialDesignModule,
    TranslateModule
  ],
  exports: [RechazarProduccionCientificaModalComponent]
})
export class PrcModalsModule { }
