import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SgiAuthModule } from '@sgi/framework/auth';
import { EvaluadorDatosGeneralesComponent } from './evaluador-formulario/evaluador-datos-generales/evaluador-datos-generales.component';
import { EvaluadorListadoComponent } from './evaluador-listado/evaluador-listado.component';
import { EvaluadorCrearComponent } from './evaluador-crear/evaluador-crear.component';
import { EvaluadorEditarComponent } from './evaluador-editar/evaluador-editar.component';
import { EvaluadorRoutingModule } from './evaluador-routing.module';
import { EvaluadorResolver } from './evaluador.resolver';
import { EvaluadorConflictosInteresListadoComponent } from './evaluador-formulario/evaluador-conflictos-interes/evaluador-conflictos-interes-listado/evaluador-conflictos-interes-listado.component';
import { EvaluadorConflictosInteresModalComponent } from './evaluador-formulario/evaluador-conflictos-interes/evaluador-conflictos-interes-modal/evaluador-conflictos-interes-modal.component';

@NgModule({
  declarations: [
    EvaluadorCrearComponent,
    EvaluadorEditarComponent,
    EvaluadorListadoComponent,
    EvaluadorDatosGeneralesComponent,
    EvaluadorConflictosInteresListadoComponent,
    EvaluadorConflictosInteresModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    EvaluadorRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule
  ],
  providers: [
    EvaluadorResolver
  ]
})
export class EvaluadorModule { }
