import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { PlanInvestigacionRoutingModule } from './plan-investigacion-routing.module';
import { PlanInvestigacionResolver } from './plan-investigacion.resolver';
import { PlanInvestigacionCrearComponent } from './plan-investigacion-crear/plan-investigacion-crear.component';
import { PlanInvestigacionEditarComponent } from './plan-investigacion-editar/plan-investigacion-editar.component';
import { PlanInvestigacionListadoComponent } from './plan-investigacion-listado/plan-investigacion-listado.component';
import { PlanInvestigacionDatosGeneralesComponent } from './plan-investigacion-formulario/plan-investigacion-datos-generales/plan-investigacion-datos-generales.component';
import { PlanInvestigacionProgramasComponent } from './plan-investigacion-formulario/plan-investigacion-programas/plan-investigacion-programas.component';
import { SgiAuthModule } from '@sgi/framework/auth';

@NgModule({
  declarations: [
    PlanInvestigacionCrearComponent,
    PlanInvestigacionEditarComponent,
    PlanInvestigacionListadoComponent,
    PlanInvestigacionDatosGeneralesComponent,
    PlanInvestigacionProgramasComponent],
  imports: [
    CommonModule,
    SharedModule,
    PlanInvestigacionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule
  ],
  providers: [
    PlanInvestigacionResolver
  ]
})
export class PlanInvestigacionModule { }
