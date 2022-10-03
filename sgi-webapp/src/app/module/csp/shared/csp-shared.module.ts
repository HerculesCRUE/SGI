import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgeFormlyFormsModule } from 'src/app/esb/sge/formly-forms/sge-formly-forms.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { EntidadFinanciadoraModalComponent } from './entidad-financiadora-modal/entidad-financiadora-modal.component';
import { MiembroEquipoProyectoModalComponent } from './miembro-equipo-proyecto-modal/miembro-equipo-proyecto-modal.component';
import { MiembroEquipoSolicitudModalComponent } from './miembro-equipo-solicitud-modal/miembro-equipo-solicitud-modal.component';
import { PartidaGastoModalComponent } from './partida-gasto-modal/partida-gasto-modal.component';
import { PartidaPresupuestariaModalComponent } from './partida-presupuestaria-modal/partida-presupuestaria-modal.component';
import { SelectAreaTematicaComponent } from './select-area-tematica/select-area-tematica.component';
import { SelectConceptoGastoComponent } from './select-concepto-gasto/select-concepto-gasto.component';
import { SearchConvocatoriaModalComponent } from './select-convocatoria/dialog/search-convocatoria.component';
import { SelectConvocatoriaComponent } from './select-convocatoria/select-convocatoria.component';
import { SelectFuenteFinanciacionComponent } from './select-fuente-financiacion/select-fuente-financiacion.component';
import { SearchGrupoModalComponent } from './select-dialog-grupo/dialog/search-grupo.component';
import { SelectDialogGrupoComponent } from './select-dialog-grupo/select-dialog-grupo.component';
import { SelectGrupoComponent } from './select-grupo/select-grupo-component';
import { SelectModeloEjecucionComponent } from './select-modelo-ejecucion/select-modelo-ejecucion.component';
import { SelectProgramaComponent } from './select-programa/select-programa.component';
import { SearchProyectoModalComponent } from './select-proyecto/dialog/search-proyecto.component';
import { SelectProyectoComponent } from './select-proyecto/select-proyecto.component';
import { SelectTipoAmbitoGeograficoComponent } from './select-tipo-ambito-geografico/select-tipo-ambito-geografico.component';
import { SelectTipoDocumentoComponent } from './select-tipo-documento/select-tipo-documento.component';
import { SelectTipoEnlaceComponent } from './select-tipo-enlace/select-tipo-enlace.component';
import { SelectTipoFaseComponent } from './select-tipo-fase/select-tipo-fase.component';
import { SelectTipoFinalidadComponent } from './select-tipo-finalidad/select-tipo-finalidad.component';
import { SelectTipoFinanciacionComponent } from './select-tipo-financiacion/select-tipo-financiacion.component';
import { SelectTipoHitoComponent } from './select-tipo-hito/select-tipo-hito.component';
import { SelectUnidadGestionComponent } from './select-unidad-gestion/select-unidad-gestion.component';
import { SolicitiudPresupuestoModalComponent } from './solicitud-presupuesto-modal/solicitud-presupuesto-modal.component';
import { SelectLineaInvestigacionComponent } from './select-linea-investigacion/select-linea-investigacion.component';
import { EntidadFinanciadoraEmpresaNombrePipe } from './pipes/entidad-financiadora-empresa-nombre.pipe';
import { SelectProyectoProyectoSgeComponent } from './select-proyecto-proyecto-sge/select-proyecto-proyecto-sge.component';
import { SelectProyectoPeriodoJustificacionComponent } from './select-proyecto-periodo-justificacion/select-proyecto-periodo-justificacion.component';
import { SelectTipoRequerimientoComponent } from './select-tipo-requerimiento/select-tipo-requerimiento.component';
import { SelectRequerimientoJustificacionComponent } from './select-requerimiento-justificacion/select-requerimiento-justificacion.component';

@NgModule({
  declarations: [
    EntidadFinanciadoraModalComponent,
    MiembroEquipoProyectoModalComponent,
    MiembroEquipoSolicitudModalComponent,
    PartidaGastoModalComponent,
    SearchConvocatoriaModalComponent,
    SelectAreaTematicaComponent,
    SelectConvocatoriaComponent,
    SelectFuenteFinanciacionComponent,
    SelectModeloEjecucionComponent,
    SelectProgramaComponent,
    SelectTipoAmbitoGeograficoComponent,
    SelectTipoFinalidadComponent,
    SelectUnidadGestionComponent,
    SolicitiudPresupuestoModalComponent,
    PartidaPresupuestariaModalComponent,
    SearchProyectoModalComponent,
    SelectProyectoComponent,
    SelectTipoFaseComponent,
    SelectTipoEnlaceComponent,
    SelectTipoDocumentoComponent,
    SelectTipoHitoComponent,
    SelectConceptoGastoComponent,
    SelectTipoFinanciacionComponent,
    SelectDialogGrupoComponent,
    SelectGrupoComponent,
    SearchGrupoModalComponent,
    SelectLineaInvestigacionComponent,
    SelectProyectoProyectoSgeComponent,
    SelectProyectoPeriodoJustificacionComponent,
    SelectTipoRequerimientoComponent,
    SelectRequerimientoJustificacionComponent,
    EntidadFinanciadoraEmpresaNombrePipe,
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgeFormlyFormsModule,
    SgempSharedModule,
    SgpSharedModule,
    SgiAuthModule
  ],
  exports: [
    EntidadFinanciadoraModalComponent,
    MiembroEquipoProyectoModalComponent,
    MiembroEquipoSolicitudModalComponent,
    PartidaGastoModalComponent,
    SelectAreaTematicaComponent,
    SelectConvocatoriaComponent,
    SelectFuenteFinanciacionComponent,
    SelectModeloEjecucionComponent,
    SelectProgramaComponent,
    SelectTipoAmbitoGeograficoComponent,
    SelectTipoFinalidadComponent,
    SelectUnidadGestionComponent,
    SolicitiudPresupuestoModalComponent,
    SelectProyectoComponent,
    SelectTipoFaseComponent,
    SelectTipoEnlaceComponent,
    SelectTipoDocumentoComponent,
    SelectTipoHitoComponent,
    SelectConceptoGastoComponent,
    SelectTipoFinanciacionComponent,
    SelectDialogGrupoComponent,
    SelectGrupoComponent,
    SelectLineaInvestigacionComponent,
    SelectProyectoProyectoSgeComponent,
    SelectProyectoPeriodoJustificacionComponent,
    SelectTipoRequerimientoComponent,
    SelectRequerimientoJustificacionComponent,
    EntidadFinanciadoraEmpresaNombrePipe,
  ]
})
export class CspSharedModule { }
