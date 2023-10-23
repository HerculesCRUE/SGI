import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { EtiSharedModule } from '../shared/eti-shared.module';
import { PeticionEvaluacionAsignacionTareasListadoExportService } from './peticion-evaluacion-asignacion-tareas-listado-export.service';
import { PeticionEvaluacionCrearComponent } from './peticion-evaluacion-crear/peticion-evaluacion-crear.component';
import { PeticionEvaluacionEditarComponent } from './peticion-evaluacion-editar/peticion-evaluacion-editar.component';
import { PeticionEvaluacionEquipoInvestigadorListadoExportService } from './peticion-evaluacion-equipo-investigador-listado-export.service';
import { EquipoInvestigadorCrearModalComponent } from './peticion-evaluacion-formulario/equipo-investigador/equipo-investigador-crear-modal/equipo-investigador-crear-modal.component';
import { EquipoInvestigadorListadoComponent } from './peticion-evaluacion-formulario/equipo-investigador/equipo-investigador-listado/equipo-investigador-listado.component';
import { MemoriasListadoComponent } from './peticion-evaluacion-formulario/memorias-listado/memorias-listado.component';
import { PeticionEvaluacionDatosGeneralesComponent } from './peticion-evaluacion-formulario/peticion-evaluacion-datos-generales/peticion-evaluacion-datos-generales.component';
import { PeticionEvaluacionTareasListadoComponent } from './peticion-evaluacion-formulario/peticion-evaluacion-tareas/peticion-evaluacion-tareas-listado/peticion-evaluacion-tareas-listado.component';
import { PeticionEvaluacionTareasModalComponent } from './peticion-evaluacion-formulario/peticion-evaluacion-tareas/peticion-evaluacion-tareas-modal/peticion-evaluacion-tareas-modal.component';
import { PeticionEvaluacionGeneralListadoExportService } from './peticion-evaluacion-general-listado-export.service';
import { PeticionEvaluacionInvRoutingModule } from './peticion-evaluacion-inv-routing.module';
import { PeticionEvaluacionListadoExportService } from './peticion-evaluacion-listado-export.service';
import { PeticionEvaluacionListadoInvComponent } from './peticion-evaluacion-listado-inv/peticion-evaluacion-listado-inv.component';
import { PeticionEvaluacionMemoriasListadoExportService } from './peticion-evaluacion-memorias-listado-export.service';
import { PeticionEvaluacionResolver } from './peticion-evaluacion.resolver';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';

@NgModule({
  declarations: [
    PeticionEvaluacionListadoInvComponent,
    PeticionEvaluacionCrearComponent,
    PeticionEvaluacionDatosGeneralesComponent,
    EquipoInvestigadorListadoComponent,
    EquipoInvestigadorCrearModalComponent,
    MemoriasListadoComponent,
    PeticionEvaluacionTareasListadoComponent,
    PeticionEvaluacionTareasModalComponent,
    PeticionEvaluacionEditarComponent,
  ],
  imports: [
    SharedModule,
    CommonModule,
    PeticionEvaluacionInvRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule,
    SgpSharedModule,
    EtiSharedModule,
    CKEditorModule
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
export class PeticionEvaluacionInvModule { }
