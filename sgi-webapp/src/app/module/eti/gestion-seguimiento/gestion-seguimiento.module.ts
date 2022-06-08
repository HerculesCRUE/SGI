import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { SeguimientoFormularioModule } from '../seguimiento-formulario/seguimiento-formulario.module';
import { SeguimientoListadoAnteriorMemoriaComponent } from '../seguimiento-formulario/seguimiento-listado-anterior-memoria/seguimiento-listado-anterior-memoria.component';
import { SeguimientoEvaluacionesAnterioresListadoExportService } from '../seguimiento/seguimiento-evaluaciones-anteriores-listado-export.service';
import { SeguimientoGeneralListadoExportService } from '../seguimiento/seguimiento-general-listado-export.service';
import { SeguimientoListadoExportService } from '../seguimiento/seguimiento-listado-export.service';
import { EtiSharedModule } from '../shared/eti-shared.module';
import { GestionSeguimientoEvaluarComponent } from './gestion-seguimiento-evaluar/gestion-seguimiento-evaluar.component';
import { GestionSeguimientoListadoComponent } from './gestion-seguimiento-listado/gestion-seguimiento-listado.component';
import { GestionSeguimientoRoutingModule } from './gestion-seguimiento-routing.module';
import { GestionSeguimientoResolver } from './gestion-seguimiento.resolver';



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
    SgpSharedModule,
    EtiSharedModule
  ],
  exports: [
    SeguimientoListadoAnteriorMemoriaComponent,
  ],
  providers: [
    GestionSeguimientoResolver,
    SeguimientoListadoExportService,
    SeguimientoGeneralListadoExportService,
    SeguimientoEvaluacionesAnterioresListadoExportService
  ]
})
export class GestionSeguimientoModule { }
