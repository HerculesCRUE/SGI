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
import { SeguimientoEvaluacionesAnterioresListadoExportService } from './seguimiento-evaluaciones-anteriores-listado-export.service';
import { SeguimientoGeneralListadoExportService } from './seguimiento-general-listado-export.service';
import { SeguimientoListadoExportService } from './seguimiento-listado-export.service';
import { SeguimientoPerfilEvaluadorRoutingModule } from './seguimiento-perfil-evaluador-routing.module';
import { SeguimientoResolver } from './seguimiento.resolver';

@NgModule({
  declarations: [
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    SeguimientoPerfilEvaluadorRoutingModule,
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
export class SeguimientoPerfilEvaluadorModule { }
