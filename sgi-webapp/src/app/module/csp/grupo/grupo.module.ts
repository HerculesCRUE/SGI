import { CommonModule, PercentPipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { SharedModule } from '@shared/shared.module';
import { SgeSharedModule } from 'src/app/esb/sge/shared/sge-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { GrupoCrearComponent } from './grupo-crear/grupo-crear.component';
import { GrupoDataResolver } from './grupo-data.resolver';
import { GrupoEditarComponent } from './grupo-editar/grupo-editar.component';
import { GrupoEnlaceListadoExportService } from './grupo-enlace-listado-export.service';
import { GrupoEquipoInstrumentalListadoExportService } from './grupo-equipo-instrumental-listado-export.service';
import { GrupoEquipoListadoExportService } from './grupo-equipo-listado-export.service';
import { GrupoDatosGeneralesComponent } from './grupo-formulario/grupo-datos-generales/grupo-datos-generales.component';
import { GrupoEnlaceComponent } from './grupo-formulario/grupo-enlace/grupo-enlace.component';
import { GrupoEquipoInstrumentalComponent } from './grupo-formulario/grupo-equipo-instrumental/grupo-equipo-instrumental.component';
import { GrupoEquipoInvestigacionComponent } from './grupo-formulario/grupo-equipo-investigacion/grupo-equipo-investigacion.component';
import { GrupoLineaInvestigacionComponent } from './grupo-formulario/grupo-linea-investigacion-listado/grupo-linea-investigacion.component';
import { GrupoPersonaAutorizadaComponent } from './grupo-formulario/grupo-persona-autorizada/grupo-persona-autorizada.component';
import { GrupoResponsableEconomicoComponent } from './grupo-formulario/grupo-responsable-economico/grupo-responsable-economico.component';
import { GrupoGeneralListadoExportService } from './grupo-general-listado-export.service';
import { GrupoLineaInvestigacionListadoExportService } from './grupo-linea-investigacion-listado-export.service';
import { GrupoListadoExportService } from './grupo-listado-export.service';
import { GrupoListadoComponent } from './grupo-listado/grupo-listado.component';
import { GrupoPersonaAutorizadaListadoExportService } from './grupo-persona-autorizada-listado-export.service';
import { GrupoResponsableEconomicoListadoExportService } from './grupo-responsable-economico-listado-export.service';
import { GrupoRoutingModule } from './grupo-routing.module';
import { GrupoEnlaceModalComponent } from './modals/grupo-enlace-modal/grupo-enlace-modal.component';
import { GrupoEquipoInstrumentalModalComponent } from './modals/grupo-equipo-instrumental-modal/grupo-equipo-instrumental-modal.component';
import { GrupoEquipoModalComponent } from './modals/grupo-equipo-modal/grupo-equipo-modal.component';
import { GrupoListadoExportModalComponent } from './modals/grupo-listado-export-modal/grupo-listado-export-modal.component';
import { GrupoPersonaAutorizadaModalComponent } from './modals/grupo-persona-autorizada-modal/grupo-persona-autorizada-modal.component';
import { GrupoResponsableEconomicoModalComponent } from './modals/grupo-responsable-economico-modal/grupo-responsable-economico-modal.component';

@NgModule({
  declarations: [
    GrupoCrearComponent,
    GrupoEditarComponent,
    GrupoListadoComponent,
    GrupoDatosGeneralesComponent,
    GrupoEquipoInvestigacionComponent,
    GrupoEquipoModalComponent,
    GrupoResponsableEconomicoComponent,
    GrupoResponsableEconomicoModalComponent,
    GrupoEquipoInstrumentalComponent,
    GrupoEquipoInstrumentalModalComponent,
    GrupoEnlaceComponent,
    GrupoEnlaceModalComponent,
    GrupoPersonaAutorizadaComponent,
    GrupoPersonaAutorizadaModalComponent,
    GrupoLineaInvestigacionComponent,
    GrupoListadoExportModalComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    GrupoRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule,
    SgpSharedModule,
    SgeSharedModule
  ],
  providers: [
    GrupoDataResolver,
    GrupoListadoExportService,
    LuxonDatePipe,
    PercentPipe,
    GrupoGeneralListadoExportService,
    GrupoEquipoListadoExportService,
    GrupoResponsableEconomicoListadoExportService,
    GrupoEnlaceListadoExportService,
    GrupoPersonaAutorizadaListadoExportService,
    GrupoEquipoInstrumentalListadoExportService,
    GrupoLineaInvestigacionListadoExportService,
  ]
})
export class GrupoModule { }
