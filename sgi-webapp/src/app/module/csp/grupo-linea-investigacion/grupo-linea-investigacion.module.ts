import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgeSharedModule } from 'src/app/esb/sge/shared/sge-shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { GrupoLineaInvestigacionCrearComponent } from './grupo-linea-investigacion-crear/grupo-linea-investigacion-crear.component';
import { GrupoLineaInvestigacionDataResolver } from './grupo-linea-investigacion-data.resolver';
import { GrupoLineaInvestigacionEditarComponent } from './grupo-linea-investigacion-editar/grupo-linea-investigacion-editar.component';
import { GrupoLineaClasificacionesComponent } from './grupo-linea-investigacion-formulario/grupo-linea-clasificaciones/grupo-linea-clasificaciones.component';
import { GrupoLineaEquipoInstrumentalComponent } from './grupo-linea-investigacion-formulario/grupo-linea-equipo-instrumental/grupo-linea-equipo-instrumental.component';
import { GrupoLineaInvestigacionDatosGeneralesComponent } from './grupo-linea-investigacion-formulario/grupo-linea-investigacion-datos-generales/grupo-linea-investigacion-datos-generales.component';
import { GrupoLineaInvestigadorComponent } from './grupo-linea-investigacion-formulario/grupo-linea-investigacion-linea-investigador/grupo-linea-investigador.component';
import { GrupoLineaInvestigacionRouting } from './grupo-linea-investigacion-routing.module';
import { GrupoLineaEquipoInstrumentalModalComponent } from './modals/grupo-linea-equipo-instrumental-modal/grupo-linea-equipo-instrumental-modal.component';
import { GrupoLineaInvestigadorModalComponent } from './modals/grupo-linea-investigador-modal/grupo-linea-investigador-modal.component';

@NgModule({
  declarations: [
    GrupoLineaInvestigacionEditarComponent,
    GrupoLineaInvestigacionCrearComponent,
    GrupoLineaInvestigacionDatosGeneralesComponent,
    GrupoLineaInvestigadorComponent,
    GrupoLineaInvestigadorModalComponent,
    GrupoLineaClasificacionesComponent,
    GrupoLineaEquipoInstrumentalComponent,
    GrupoLineaEquipoInstrumentalModalComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    GrupoLineaInvestigacionRouting,
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
    GrupoLineaInvestigacionDataResolver,
  ]
})
export class GrupoLineaInvestigacionModule { }
