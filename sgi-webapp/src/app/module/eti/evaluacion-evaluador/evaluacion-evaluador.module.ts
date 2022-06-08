import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';

import { ComentarioModule } from '../comentario/comentario.module';
import { DocumentacionMemoriaModule } from '../documentacion-memoria/documentacion-memoria.module';
import { EvaluacionEvaluadorRoutingModule } from './evaluacion-evaluador-routing.module';
import { EvaluacionFormularioModule } from '../evaluacion-formulario/evaluacion-formulario.module';
import { EvaluacionEvaluadorEvaluarComponent } from './evaluacion-evaluador-evaluar/evaluacion-evaluador-evaluar.component';
import { EvaluacionEvaluadorListadoComponent } from './evaluacion-evaluador-listado/evaluacion-evaluador-listado.component';
import { EvaluacionEvaluadorResolver } from './evaluacion-evaluador.resolver';
import { EtiSharedModule } from '../shared/eti-shared.module';
import { EvaluacionListadoExportService } from '../evaluacion/evaluacion-listado-export.service';
import { EvaluacionGeneralListadoExportService } from '../evaluacion/evaluacion-general-listado-export.service';
import { EvaluacionEvaluacionesAnterioresListadoExportService } from '../evaluacion/evaluacion-evaluaciones-anteriores-listado-export.service';

@NgModule({
  declarations: [
    EvaluacionEvaluadorEvaluarComponent,
    EvaluacionEvaluadorListadoComponent,

  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    EvaluacionEvaluadorRoutingModule,
    EvaluacionFormularioModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    ComentarioModule,
    DocumentacionMemoriaModule,
    EtiSharedModule
  ],
  providers: [
    EvaluacionEvaluadorResolver,
    EvaluacionListadoExportService,
    EvaluacionGeneralListadoExportService,
    EvaluacionEvaluacionesAnterioresListadoExportService
  ]

})
export class EvaluacionEvaluadorModule { }
