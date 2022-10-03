import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SeguimientoJustificacionRequerimientoCrearComponent } from './seguimiento-justificacion-requerimiento-crear/seguimiento-justificacion-requerimiento-crear.component';
import { SeguimientoJustificacionRequerimientoDataResolver } from './seguimiento-justificacion-requerimiento-data.resolver';
import { SeguimientoJustificacionRequerimientoEditarComponent } from './seguimiento-justificacion-requerimiento-editar/seguimiento-justificacion-requerimiento-editar.component';
import { SeguimientoJustificacionRequerimientoRouting } from './seguimiento-justificacion-requerimiento-routing.module';
import { SeguimientoJustificacionRequerimientoDatosGeneralesComponent } from './seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-datos-generales/seguimiento-justificacion-requerimiento-datos-generales.component';
import { CspSharedModule } from '../shared/csp-shared.module';
import { IncidenciaDocumentoRequerimientoModalComponent } from './modals/incidencia-documento-requerimiento-modal/incidencia-documento-requerimiento-modal.component';
import { SeguimientoJustificacionRequerimientoGastosComponent } from './seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-gastos/seguimiento-justificacion-requerimiento-gastos.component';
import { GastosJustificadosModalComponent } from './modals/gastos-justificados-modal/gastos-justificados-modal.component';
import { GastoJustificadoDetalleModalComponent } from './modals/gasto-justificado-detalle-modal/gasto-justificado-detalle-modal.component';
import { DetalleGastoJustificadoComponent } from './common/detalle-gasto-justificado/detalle-gasto-justificado.component';
import { GastoRequerimientoJustificacionModalComponent } from './modals/gasto-requerimiento-justificacion-modal/gasto-requerimiento-justificacion-modal.component';
import { BooleanToTextPipe } from './common/pipes/boolean-to-text.pipe';
import { SeguimientoJustificacionRequerimientoRespuestaAlegacionComponent } from './seguimiento-justificacion-requerimiento-formulario/seguimiento-justificacion-requerimiento-respuesta-alegacion/seguimiento-justificacion-requerimiento-respuesta-alegacion.component';
import { IncidenciaDocumentacionRequerimientoAlegacionModalComponent } from './modals/incidencia-documentacion-requerimiento-alegacion-modal/incidencia-documentacion-requerimiento-alegacion-modal.component';

@NgModule({
  declarations: [
    SeguimientoJustificacionRequerimientoEditarComponent,
    SeguimientoJustificacionRequerimientoCrearComponent,
    SeguimientoJustificacionRequerimientoDatosGeneralesComponent,
    IncidenciaDocumentoRequerimientoModalComponent,
    SeguimientoJustificacionRequerimientoGastosComponent,
    GastosJustificadosModalComponent,
    GastoJustificadoDetalleModalComponent,
    DetalleGastoJustificadoComponent,
    GastoRequerimientoJustificacionModalComponent,
    SeguimientoJustificacionRequerimientoRespuestaAlegacionComponent,
    BooleanToTextPipe,
    IncidenciaDocumentacionRequerimientoAlegacionModalComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    SeguimientoJustificacionRequerimientoRouting,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule
  ],
  providers: [
    SeguimientoJustificacionRequerimientoDataResolver,
  ]
})
export class SeguimientoJustificacionRequerimientoModule { }
