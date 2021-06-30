import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { EvaluacionListadoComponent } from './evaluacion-listado/evaluacion-listado.component';
import { EvaluacionRoutingModule } from './evaluacion-routing.module';
import { EvaluacionEvaluarComponent } from './evaluacion-evaluar/evaluacion-evaluar.component';
import { EvaluacionFormularioModule } from '../evaluacion-formulario/evaluacion-formulario.module';
import { EvaluacionResolver } from '../evaluacion/evaluacion.resolver';
import { EvaluacionListadoAnteriorMemoriaComponent } from '../evaluacion-formulario/evaluacion-listado-anterior-memoria/evaluacion-listado-anterior-memoria.component';

@NgModule({
  declarations: [
    EvaluacionListadoComponent,
    EvaluacionEvaluarComponent,
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    EvaluacionRoutingModule,
    EvaluacionFormularioModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule
  ],
  exports: [
    EvaluacionListadoAnteriorMemoriaComponent,
  ],
  providers: [
    EvaluacionResolver
  ]
})
export class EvaluacionModule { }
