import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';

import { ComentarioModule } from '../comentario/comentario.module';
import { DocumentacionMemoriaModule } from '../documentacion-memoria/documentacion-memoria.module';
import { SeguimientoComentariosComponent } from './seguimiento-comentarios/seguimiento-comentarios.component';
import { SeguimientoDatosMemoriaComponent } from './seguimiento-datos-memoria/seguimiento-datos-memoria.component';
import { SeguimientoDocumentacionComponent } from './seguimiento-documentacion/seguimiento-documentacion.component';
import { SeguimientoEvaluacionComponent } from './seguimiento-evaluacion/seguimiento-evaluacion.component';
import { SeguimientoListadoAnteriorMemoriaComponent } from './seguimiento-listado-anterior-memoria/seguimiento-listado-anterior-memoria.component';
import { SeguimientoListadoComentariosEquipoEvaluadorComponent } from './seguimiento-listado-comentarios-equipo-evaluador/seguimiento-listado-comentarios-equipo-evaluador.component';

@NgModule({
  declarations: [
    SeguimientoComentariosComponent,
    SeguimientoDatosMemoriaComponent,
    SeguimientoDocumentacionComponent,
    SeguimientoListadoAnteriorMemoriaComponent,
    SeguimientoEvaluacionComponent,
    SeguimientoListadoComentariosEquipoEvaluadorComponent
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
    SeguimientoComentariosComponent,
    SeguimientoDatosMemoriaComponent,
    SeguimientoDocumentacionComponent,
    SeguimientoListadoAnteriorMemoriaComponent,
    SeguimientoEvaluacionComponent,
    SeguimientoListadoComentariosEquipoEvaluadorComponent
  ]
})
export class SeguimientoFormularioModule { }
