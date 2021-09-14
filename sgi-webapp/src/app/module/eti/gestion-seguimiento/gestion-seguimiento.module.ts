import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SgiAuthModule } from '@sgi/framework/auth';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { GestionSeguimientoRoutingModule } from './gestion-seguimiento-routing.module';
import { GestionSeguimientoListadoComponent } from './gestion-seguimiento-listado/gestion-seguimiento-listado.component';
import { GestionSeguimientoEvaluarComponent } from './gestion-seguimiento-evaluar/gestion-seguimiento-evaluar.component';
import { GestionSeguimientoResolver } from './gestion-seguimiento.resolver';
import { SeguimientoListadoAnteriorMemoriaComponent } from '../seguimiento-formulario/seguimiento-listado-anterior-memoria/seguimiento-listado-anterior-memoria.component';
import { TranslateModule } from '@ngx-translate/core';
import { SeguimientoEvaluacionComponent } from '../seguimiento-formulario/seguimiento-evaluacion/seguimiento-evaluacion.component';
import { SeguimientoEvaluarActionService } from '../seguimiento/seguimiento-evaluar.action.service';
import { SeguimientoFormularioModule } from '../seguimiento-formulario/seguimiento-formulario.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';


@NgModule({
  declarations: [
    GestionSeguimientoListadoComponent,
    GestionSeguimientoEvaluarComponent
  ],
  imports: [
    CommonModule,
    SgiAuthModule,
    GestionSeguimientoRoutingModule,
    SeguimientoFormularioModule,
    SharedModule,
    MaterialDesignModule,
    TranslateModule,
    FormsModule,
    ReactiveFormsModule,
    SgpSharedModule
  ],
  exports: [
    SeguimientoListadoAnteriorMemoriaComponent,
  ],
  providers: [
    GestionSeguimientoResolver
  ]
})
export class GestionSeguimientoModule { }
