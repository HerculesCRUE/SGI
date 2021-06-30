import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { SgiAuthModule } from '@sgi/framework/auth';
import { PeticionEvaluacionListadoGesComponent } from './peticion-evaluacion-listado-ges/peticion-evaluacion-listado-ges.component';
import { PeticionEvaluacionGesRoutingModule } from './peticion-evaluacion-ges-routing.module';
import { PeticionEvaluacionResolver } from './peticion-evaluacion.resolver';

@NgModule({
  declarations: [
    PeticionEvaluacionListadoGesComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    PeticionEvaluacionGesRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule
  ],
  providers: [
    PeticionEvaluacionResolver
  ]
})
export class PeticionEvaluacionGesModule { }
