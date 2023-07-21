import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { RolEquipoCrearComponent } from './rol-equipo-crear/rol-equipo-crear.component';
import { RolEquipoEditarComponent } from './rol-equipo-editar/rol-equipo-editar.component';
import { RolEquipoColectivosComponent } from './rol-equipo-formulario/rol-equipo-colectivos/rol-equipo-colectivos.component';
import { RolEquipoDatosGeneralesComponent } from './rol-equipo-formulario/rol-equipo-datos-generales/rol-equipo-datos-generales.component';
import { RolEquipoListadoComponent } from './rol-equipo-listado/rol-equipo-listado.component';
import { RolEquipoRoutingModule } from './rol-equipo-routing.module';
import { RolEquipoResolver } from './rol-equipo.resolver';
import { RolEquipoColectivoModalComponent } from './modals/rol-equipo-colectivo-modal/rol-equipo-colectivo-modal.component';



@NgModule({
  declarations: [
    RolEquipoCrearComponent,
    RolEquipoEditarComponent,
    RolEquipoListadoComponent,
    RolEquipoDatosGeneralesComponent,
    RolEquipoColectivosComponent,
    RolEquipoColectivoModalComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    RolEquipoRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule
  ],
  providers: [
    RolEquipoResolver
  ]
})
export class RolEquipoModule { }
