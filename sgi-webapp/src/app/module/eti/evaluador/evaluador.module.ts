import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { EtiSharedModule } from '../shared/eti-shared.module';
import { EvaluadorConflictosInteresListadoExportService } from './evaluador-conflictos-interes-listado-export.service';
import { EvaluadorCrearComponent } from './evaluador-crear/evaluador-crear.component';
import { EvaluadorEditarComponent } from './evaluador-editar/evaluador-editar.component';
import { EvaluadorConflictosInteresListadoComponent } from './evaluador-formulario/evaluador-conflictos-interes/evaluador-conflictos-interes-listado/evaluador-conflictos-interes-listado.component';
import { EvaluadorConflictosInteresModalComponent } from './evaluador-formulario/evaluador-conflictos-interes/evaluador-conflictos-interes-modal/evaluador-conflictos-interes-modal.component';
import { EvaluadorDatosGeneralesComponent } from './evaluador-formulario/evaluador-datos-generales/evaluador-datos-generales.component';
import { EvaluadorGeneralListadoExportService } from './evaluador-general-listado-export.service';
import { EvaluadorListadoExportService } from './evaluador-listado-export.service';
import { EvaluadorListadoComponent } from './evaluador-listado/evaluador-listado.component';
import { EvaluadorRoutingModule } from './evaluador-routing.module';
import { EvaluadorResolver } from './evaluador.resolver';
import { EvaluadorListadoExportModalComponent } from './modals/evaluador-listado-export-modal/evaluador-listado-export-modal.component';
import { EstadoEvaluadorPipe } from './pipes/estado-evaluador.pipe';


@NgModule({
  declarations: [
    EvaluadorCrearComponent,
    EvaluadorEditarComponent,
    EvaluadorListadoComponent,
    EvaluadorDatosGeneralesComponent,
    EvaluadorConflictosInteresListadoComponent,
    EvaluadorConflictosInteresModalComponent,
    EstadoEvaluadorPipe,
    EvaluadorListadoExportModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    EvaluadorRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule,
    SgpSharedModule,
    EtiSharedModule
  ],
  providers: [
    EvaluadorResolver,
    EvaluadorListadoExportService,
    EvaluadorGeneralListadoExportService,
    EvaluadorConflictosInteresListadoExportService,
    EstadoEvaluadorPipe
  ]
})
export class EvaluadorModule { }
