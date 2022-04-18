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
import { GrupoCrearComponent } from './grupo-crear/grupo-crear.component';
import { GrupoDataResolver } from './grupo-data.resolver';
import { GrupoEditarComponent } from './grupo-editar/grupo-editar.component';
import { GrupoDatosGeneralesComponent } from './grupo-formulario/grupo-datos-generales/grupo-datos-generales.component';
import { GrupoEquipoInvestigacionComponent } from './grupo-formulario/grupo-equipo-investigacion/grupo-equipo-investigacion.component';
import { GrupoListadoComponent } from './grupo-listado/grupo-listado.component';
import { GrupoRoutingModule } from './grupo-routing.module';
import { GrupoEquipoModalComponent } from './modals/grupo-equipo-modal/grupo-equipo-modal.component';

@NgModule({
  declarations: [
    GrupoCrearComponent,
    GrupoEditarComponent,
    GrupoListadoComponent,
    GrupoDatosGeneralesComponent,
    GrupoEquipoInvestigacionComponent,
    GrupoEquipoModalComponent
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
    GrupoDataResolver
  ]
})
export class GrupoModule { }
