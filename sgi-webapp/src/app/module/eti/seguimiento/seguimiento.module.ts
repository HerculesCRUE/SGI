import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { DocumentacionMemoriaModule } from '../documentacion-memoria/documentacion-memoria.module';
import { EvaluacionModule } from '../evaluacion/evaluacion.module';
import { SeguimientoFormularioModule } from '../seguimiento-formulario/seguimiento-formulario.module';
import { EtiSharedModule } from '../shared/eti-shared.module';
import { SeguimientoListadoExportModalComponent } from './modals/seguimiento-listado-export-modal/seguimiento-listado-export-modal.component';
import { SeguimientoEvaluacionesAnterioresListadoExportService } from './seguimiento-evaluaciones-anteriores-listado-export.service';
import { SeguimientoEvaluarComponent } from './seguimiento-evaluar/seguimiento-evaluar.component';
import { SeguimientoGeneralListadoExportService } from './seguimiento-general-listado-export.service';
import { SeguimientoListadoExportService } from './seguimiento-listado-export.service';
import { SeguimientoListadoComponent } from './seguimiento-listado/seguimiento-listado.component';
import { SeguimientoRoutingModule } from './seguimiento-routing.module';
import { SeguimientoResolver } from './seguimiento.resolver';


@NgModule({
  declarations: [
    SeguimientoListadoComponent,
    SeguimientoEvaluarComponent,
    SeguimientoListadoExportModalComponent
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
    EvaluacionModule,
    EtiSharedModule
  ],
  providers: [
    SeguimientoResolver,
    SeguimientoListadoExportService,
    SeguimientoGeneralListadoExportService,
    SeguimientoEvaluacionesAnterioresListadoExportService
  ]
})
export class SeguimientoModule { }
