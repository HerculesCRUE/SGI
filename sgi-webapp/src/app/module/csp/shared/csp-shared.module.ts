import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SgeFormlyFormsModule } from 'src/app/esb/sge/formly-forms/sge-formly-forms.module';
import { SgempSharedModule } from 'src/app/esb/sgemp/shared/sgemp-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { EntidadFinanciadoraModalComponent } from './entidad-financiadora-modal/entidad-financiadora-modal.component';
import { MiembroEquipoProyectoModalComponent } from './miembro-equipo-proyecto-modal/miembro-equipo-proyecto-modal.component';
import { MiembroEquipoSolicitudModalComponent } from './miembro-equipo-solicitud-modal/miembro-equipo-solicitud-modal.component';
import { PartidaGastoModalComponent } from './partida-gasto-modal/partida-gasto-modal.component';
import { PartidaPresupuestariaModalComponent } from './partida-presupuestaria-modal/partida-presupuestaria-modal.component';
import { SearchProyectosEconomicosModalComponent } from './search-proyectos-economicos-modal/search-proyectos-economicos-modal.component';
import { SelectAreaTematicaComponent } from './select-area-tematica/select-area-tematica.component';
import { SearchConvocatoriaModalComponent } from './select-convocatoria/dialog/search-convocatoria.component';
import { SelectConvocatoriaComponent } from './select-convocatoria/select-convocatoria.component';
import { SelectFuenteFinanciacionComponent } from './select-fuente-financiacion/select-fuente-financiacion.component';
import { SelectModeloEjecucionComponent } from './select-modelo-ejecucion/select-modelo-ejecucion.component';
import { SelectProgramaComponent } from './select-programa/select-programa.component';
import { SearchProyectoModalComponent } from './select-proyecto/dialog/search-proyecto.component';
import { SelectProyectoComponent } from './select-proyecto/select-proyecto.component';
import { SelectTipoAmbitoGeograficoComponent } from './select-tipo-ambito-geografico/select-tipo-ambito-geografico.component';
import { SelectTipoFinalidadComponent } from './select-tipo-finalidad/select-tipo-finalidad.component';
import { SelectUnidadGestionComponent } from './select-unidad-gestion/select-unidad-gestion.component';
import { SolicitiudPresupuestoModalComponent } from './solicitud-presupuesto-modal/solicitud-presupuesto-modal.component';

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
    SearchProyectosEconomicosModalComponent,
    SolicitiudPresupuestoModalComponent,
    PartidaPresupuestariaModalComponent,
    SearchProyectoModalComponent,
    SelectProyectoComponent
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
    SgpSharedModule
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
    SearchProyectosEconomicosModalComponent,
    SolicitiudPresupuestoModalComponent,
    SelectProyectoComponent
  ]
})
export class CspSharedModule { }
