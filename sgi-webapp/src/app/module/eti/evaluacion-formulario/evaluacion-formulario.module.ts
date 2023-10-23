import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';

import { ComentarioModule } from '../comentario/comentario.module';
import { DocumentacionMemoriaModule } from '../documentacion-memoria/documentacion-memoria.module';
import {
  EvaluacionComentariosComponent,
} from '../evaluacion-formulario/evaluacion-comentarios/evaluacion-comentarios.component';
import {
  EvaluacionDatosMemoriaComponent,
} from '../evaluacion-formulario/evaluacion-datos-memoria/evaluacion-datos-memoria.component';
import {
  EvaluacionDocumentacionComponent,
} from '../evaluacion-formulario/evaluacion-documentacion/evaluacion-documentacion.component';
import {
  EvaluacionListadoAnteriorMemoriaComponent,
} from '../evaluacion-formulario/evaluacion-listado-anterior-memoria/evaluacion-listado-anterior-memoria.component';
import { BloqueApartadoPipe } from '../shared/pipes/bloque-apartado.pipe';
import { EvaluacionEvaluacionComponent } from './evaluacion-evaluacion/evaluacion-evaluacion.component';
import { EvaluacionListadoComentariosEquipoEvaluadorComponent } from './evaluacion-listado-comentarios-equipo-evaluador/evaluacion-listado-comentarios-equipo-evaluador.component';

@NgModule({
  declarations: [
    EvaluacionComentariosComponent,
    EvaluacionDatosMemoriaComponent,
    EvaluacionDocumentacionComponent,
    EvaluacionListadoAnteriorMemoriaComponent,
    EvaluacionEvaluacionComponent,
    EvaluacionListadoComentariosEquipoEvaluadorComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    ComentarioModule,
    DocumentacionMemoriaModule
  ],
  exports: [
    EvaluacionComentariosComponent,
    EvaluacionDatosMemoriaComponent,
    EvaluacionDocumentacionComponent,
    EvaluacionListadoAnteriorMemoriaComponent,
    EvaluacionEvaluacionComponent,
    EvaluacionListadoComentariosEquipoEvaluadorComponent
  ]
})
export class EvaluacionFormularioModule { }
