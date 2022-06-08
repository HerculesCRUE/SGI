import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { EtiSharedModule } from '../shared/eti-shared.module';
import { PeticionEvaluacionListadoExportModalComponent } from './modals/peticion-evaluacion-listado-export-modal/peticion-evaluacion-listado-export-modal.component';
import { PeticionEvaluacionAsignacionTareasListadoExportService } from './peticion-evaluacion-asignacion-tareas-listado-export.service';
import { PeticionEvaluacionGeneralListadoExportService } from './peticion-evaluacion-general-listado-export.service';
import { PeticionEvaluacionGesRoutingModule } from './peticion-evaluacion-ges-routing.module';
import { PeticionEvaluacionListadoExportService } from './peticion-evaluacion-listado-export.service';
import { PeticionEvaluacionListadoGesComponent } from './peticion-evaluacion-listado-ges/peticion-evaluacion-listado-ges.component';
import { PeticionEvaluacionMemoriasListadoExportService } from './peticion-evaluacion-memorias-listado-export.service';
import { PeticionEvaluacionResolver } from './peticion-evaluacion.resolver';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { PeticionEvaluacionEquipoInvestigadorListadoExportService } from './peticion-evaluacion-equipo-investigador-listado-export.service';

@NgModule({
  declarations: [
    PeticionEvaluacionListadoGesComponent,
    PeticionEvaluacionListadoExportModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    PeticionEvaluacionGesRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule,
    SgpSharedModule,
    EtiSharedModule,
    CspSharedModule
  ],
  providers: [
    PeticionEvaluacionResolver,
    PeticionEvaluacionListadoExportService,
    PeticionEvaluacionGeneralListadoExportService,
    PeticionEvaluacionAsignacionTareasListadoExportService,
    PeticionEvaluacionMemoriasListadoExportService,
    PeticionEvaluacionEquipoInvestigadorListadoExportService
  ]
})
export class PeticionEvaluacionGesModule { }
