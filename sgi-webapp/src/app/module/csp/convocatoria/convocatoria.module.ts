import { CommonModule, DecimalPipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { SharedModule } from '@shared/shared.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { ConvocatoriaAreaTematicaListadoExportService } from './convocatoria-area-tematica-listado-export.service';
import { ConvocatoriaCalendarioJustificacionListadoExportService } from './convocatoria-calendario-justificacion-listado-export.service';
import { ConvocatoriaConceptoGastoListadoExportService } from './convocatoria-concepto-gasto-listado-export.service';
import { ConvocatoriaConfiguracionSolicitudListadoExportService } from './convocatoria-configuracion-solicitud-listado-export.service';
import { ConvocatoriaCrearComponent } from './convocatoria-crear/convocatoria-crear.component';
import { ConvocatoriaDataResolver } from './convocatoria-data.resolver';
import { ConvocatoriaEditarComponent } from './convocatoria-editar/convocatoria-editar.component';
import { ConvocatoriaEnlaceListadoExportService } from './convocatoria-enlace-listado-export.service';
import { ConvocatoriaEntidadConvocanteListadoExportService } from './convocatoria-entidad-convocante-listado-export.service';
import { ConvocatoriaEntidadFinanciadoraListadoExportService } from './convocatoria-entidad-financiadora-listado-export.service';
import { ConvocatoriaFaseListadoExportService } from './convocatoria-fase-listado-export.service';
import { ConvocatoriaConceptoGastoComponent } from './convocatoria-formulario/convocatoria-concepto-gasto/convocatoria-concepto-gasto.component';
import { ConvocatoriaConfiguracionSolicitudesComponent } from './convocatoria-formulario/convocatoria-configuracion-solicitudes/convocatoria-configuracion-solicitudes.component';
import { ConvocatoriaDatosGeneralesComponent } from './convocatoria-formulario/convocatoria-datos-generales/convocatoria-datos-generales.component';
import { ConvocatoriaDocumentosComponent } from './convocatoria-formulario/convocatoria-documentos/convocatoria-documentos.component';
import { ConvocatoriaEnlaceComponent } from './convocatoria-formulario/convocatoria-enlace/convocatoria-enlace.component';
import { ConvocatoriaEntidadesConvocantesComponent } from './convocatoria-formulario/convocatoria-entidades-convocantes/convocatoria-entidades-convocantes.component';
import { ConvocatoriaEntidadesFinanciadorasComponent } from './convocatoria-formulario/convocatoria-entidades-financiadoras/convocatoria-entidades-financiadoras.component';
import { ConvocatoriaHitosComponent } from './convocatoria-formulario/convocatoria-hitos/convocatoria-hitos.component';
import { ConvocatoriaPartidaPresupuestariaComponent } from './convocatoria-formulario/convocatoria-partidas-presupuestarias/convocatoria-partidas-presupuestarias.component';
import { ConvocatoriaPeriodosJustificacionComponent } from './convocatoria-formulario/convocatoria-periodos-justificacion/convocatoria-periodos-justificacion.component';
import { ConvocatoriaPlazosFasesComponent } from './convocatoria-formulario/convocatoria-plazos-fases/convocatoria-plazos-fases.component';
import { ConvocatoriaRequisitosEquipoComponent } from './convocatoria-formulario/convocatoria-requisitos-equipo/convocatoria-requisitos-equipo.component';
import { ConvocatoriaRequisitosIPComponent } from './convocatoria-formulario/convocatoria-requisitos-ip/convocatoria-requisitos-ip.component';
import { ConvocatoriaSeguimientoCientificoComponent } from './convocatoria-formulario/convocatoria-seguimiento-cientifico/convocatoria-seguimiento-cientifico.component';
import { ConvocatoriaGeneralListadoExportService } from './convocatoria-general-listado-export.service';
import { ConvocatoriaHitoListadoExportService } from './convocatoria-hito-listado-export.service';
import { ConvocatoriaListadoExportService } from './convocatoria-listado-export.service';
import { ConvocatoriaListadoComponent } from './convocatoria-listado/convocatoria-listado.component';
import { ConvocatoriaPartidaPresupuestariaListadoExportService } from './convocatoria-partida-presupuestaria-listado-export.service';
import { ConvocatoriaPeriodoSeguimientoListadoExportService } from './convocatoria-periodo-seguimiento-listado-export.service';
import { ConvocatoriaRequisitoEquipoListadoExportService } from './convocatoria-requisito-equipo-listado-export.service';
import { ConvocatoriaRequisitoIPListadoExportService } from './convocatoria-requisito-ip-listado-export.service';
import { ConvocatoriaRoutingModule } from './convocatoria-routing.module';
import { CategoriaProfesionalModalComponent } from './modals/categoria-profesional-modal/categoria-profesional-modal.component';
import { ConvocatoriaAreaTematicaModalComponent } from './modals/convocatoria-area-tematica-modal/convocatoria-area-tematica-modal.component';
import { ConvocatoriaConfiguracionSolicitudesModalComponent } from './modals/convocatoria-configuracion-solicitudes-modal/convocatoria-configuracion-solicitudes-modal.component';
import { ConvocatoriaEnlaceModalComponent } from './modals/convocatoria-enlace-modal/convocatoria-enlace-modal.component';
import { ConvocatoriaEntidadConvocanteModalComponent } from './modals/convocatoria-entidad-convocante-modal/convocatoria-entidad-convocante-modal.component';
import { ConvocatoriaHitosModalComponent } from './modals/convocatoria-hitos-modal/convocatoria-hitos-modal.component';
import { ConvocatoriaListadoExportModalComponent } from './modals/convocatoria-listado-export-modal/convocatoria-listado-export-modal.component';
import { ConvocatoriaPartidaPresupuestariaModalComponent } from './modals/convocatoria-partidas-presupuestarias-modal/convocatoria-partidas-presupuestarias-modal.component';
import { ConvocatoriaPeriodosJustificacionModalComponent } from './modals/convocatoria-periodos-justificacion-modal/convocatoria-periodos-justificacion-modal.component';
import { ConvocatoriaPlazosFaseModalComponent } from './modals/convocatoria-plazos-fase-modal/convocatoria-plazos-fase-modal.component';
import { ConvocatoriaSeguimientoCientificoModalComponent } from './modals/convocatoria-seguimiento-cientifico-modal/convocatoria-seguimiento-cientifico-modal.component';
import { NivelAcademicoModalComponent } from './modals/nivel-academico-modal/nivel-academico-modal.component';

@NgModule({
  declarations: [
    ConvocatoriaListadoComponent,
    ConvocatoriaListadoExportModalComponent,
    ConvocatoriaCrearComponent,
    ConvocatoriaDatosGeneralesComponent,
    ConvocatoriaPeriodosJustificacionComponent,
    ConvocatoriaEditarComponent,
    ConvocatoriaPlazosFasesComponent,
    ConvocatoriaHitosComponent,
    ConvocatoriaPartidaPresupuestariaComponent,
    ConvocatoriaEntidadesConvocantesComponent,
    ConvocatoriaSeguimientoCientificoComponent,
    ConvocatoriaEntidadesFinanciadorasComponent,
    ConvocatoriaEnlaceComponent,
    ConvocatoriaHitosModalComponent,
    ConvocatoriaPartidaPresupuestariaModalComponent,
    ConvocatoriaPeriodosJustificacionModalComponent,
    ConvocatoriaEnlaceModalComponent,
    ConvocatoriaEntidadConvocanteModalComponent,
    ConvocatoriaPlazosFaseModalComponent,
    ConvocatoriaSeguimientoCientificoModalComponent,
    ConvocatoriaAreaTematicaModalComponent,
    ConvocatoriaRequisitosIPComponent,
    ConvocatoriaConceptoGastoComponent,
    ConvocatoriaRequisitosEquipoComponent,
    ConvocatoriaDocumentosComponent,
    ConvocatoriaConfiguracionSolicitudesComponent,
    ConvocatoriaConfiguracionSolicitudesModalComponent,
    NivelAcademicoModalComponent,
    CategoriaProfesionalModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ConvocatoriaRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule,
    SgempSharedModule
  ],
  providers: [
    ConvocatoriaDataResolver,
    DecimalPipe,
    LuxonDatePipe,
    ConvocatoriaListadoExportService,
    ConvocatoriaGeneralListadoExportService,
    ConvocatoriaAreaTematicaListadoExportService,
    ConvocatoriaEntidadConvocanteListadoExportService,
    ConvocatoriaEntidadFinanciadoraListadoExportService,
    ConvocatoriaEnlaceListadoExportService,
    ConvocatoriaFaseListadoExportService,
    ConvocatoriaCalendarioJustificacionListadoExportService,
    ConvocatoriaPeriodoSeguimientoListadoExportService,
    ConvocatoriaHitoListadoExportService,
    ConvocatoriaRequisitoIPListadoExportService,
    ConvocatoriaRequisitoEquipoListadoExportService,
    ConvocatoriaConceptoGastoListadoExportService,
    ConvocatoriaPartidaPresupuestariaListadoExportService,
    ConvocatoriaConfiguracionSolicitudListadoExportService
  ]
})
export class ConvocatoriaModule { }
