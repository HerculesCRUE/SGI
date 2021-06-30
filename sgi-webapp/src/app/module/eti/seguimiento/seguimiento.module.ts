import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';

import { DocumentacionMemoriaModule } from '../documentacion-memoria/documentacion-memoria.module';
import { EvaluacionModule } from '../evaluacion/evaluacion.module';
import { SeguimientoEvaluarComponent } from './seguimiento-evaluar/seguimiento-evaluar.component';
import { SeguimientoListadoComponent } from './seguimiento-listado/seguimiento-listado.component';
import { SeguimientoRoutingModule } from './seguimiento-routing.module';
import { SeguimientoResolver } from './seguimiento.resolver';
import { SeguimientoComentariosComponent } from '../seguimiento-formulario/seguimiento-comentarios/seguimiento-comentarios.component';
import { SeguimientoDatosMemoriaComponent } from '../seguimiento-formulario/seguimiento-datos-memoria/seguimiento-datos-memoria.component';
import { SeguimientoDocumentacionComponent } from '../seguimiento-formulario/seguimiento-documentacion/seguimiento-documentacion.component';
import { SeguimientoFormularioModule } from '../seguimiento-formulario/seguimiento-formulario.module';

@NgModule({
  declarations: [
    SeguimientoListadoComponent,
    SeguimientoEvaluarComponent,
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    SeguimientoRoutingModule,
    SeguimientoFormularioModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    DocumentacionMemoriaModule,
    EvaluacionModule
  ],
  providers: [
    SeguimientoResolver,
  ]
})
export class SeguimientoModule { }
