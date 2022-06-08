import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SelectComiteComponent } from './select-comite/select-comite.component';
import { SelectEvaluadorComponent } from './select-evaluador/select-evaluador.component';
import { SelectTipoConvocatoriaReunionComponent } from './select-tipo-convocatoria-reunion/select-tipo-convocatoria-reunion.component';
import { SelectTipoEvaluacionComponent } from './select-tipo-evaluacion/select-tipo-evaluacion.component';

@NgModule({
  declarations: [
    SelectComiteComponent,
    SelectEvaluadorComponent,
    SelectTipoConvocatoriaReunionComponent,
    SelectTipoEvaluacionComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  exports: [
    SelectComiteComponent,
    SelectEvaluadorComponent,
    SelectTipoConvocatoriaReunionComponent,
    SelectTipoEvaluacionComponent
  ]
})
export class EtiSharedModule { }
