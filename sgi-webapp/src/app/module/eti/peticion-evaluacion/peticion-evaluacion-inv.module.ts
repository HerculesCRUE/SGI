import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SgiAuthModule } from '@sgi/framework/auth';
import { PeticionEvaluacionListadoInvComponent } from './peticion-evaluacion-listado-inv/peticion-evaluacion-listado-inv.component';
import { PeticionEvaluacionInvRoutingModule } from './peticion-evaluacion-inv-routing.module';
import { PeticionEvaluacionCrearComponent } from './peticion-evaluacion-crear/peticion-evaluacion-crear.component';
import { PeticionEvaluacionDatosGeneralesComponent } from './peticion-evaluacion-formulario/peticion-evaluacion-datos-generales/peticion-evaluacion-datos-generales.component';
import { EquipoInvestigadorListadoComponent } from './peticion-evaluacion-formulario/equipo-investigador/equipo-investigador-listado/equipo-investigador-listado.component';
import { EquipoInvestigadorCrearModalComponent } from './peticion-evaluacion-formulario/equipo-investigador/equipo-investigador-crear-modal/equipo-investigador-crear-modal.component';
import { MemoriasListadoComponent } from './peticion-evaluacion-formulario/memorias-listado/memorias-listado.component';

import { PeticionEvaluacionResolver } from './peticion-evaluacion.resolver';
import { PeticionEvaluacionTareasListadoComponent } from './peticion-evaluacion-formulario/peticion-evaluacion-tareas/peticion-evaluacion-tareas-listado/peticion-evaluacion-tareas-listado.component';
import { PeticionEvaluacionEditarComponent } from './peticion-evaluacion-editar/peticion-evaluacion-editar.component';
import { PeticionEvaluacionTareasModalComponent } from './peticion-evaluacion-formulario/peticion-evaluacion-tareas/peticion-evaluacion-tareas-modal/peticion-evaluacion-tareas-modal.component';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';

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
    PeticionEvaluacionEditarComponent
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
    SgpSharedModule
  ],
  providers: [
    PeticionEvaluacionResolver
  ]
})
export class PeticionEvaluacionInvModule { }
