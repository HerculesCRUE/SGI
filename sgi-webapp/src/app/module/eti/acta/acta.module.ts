import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';

import { ActaCrearComponent } from './acta-crear/acta-crear.component';
import { ActaEditarComponent } from './acta-editar/acta-editar.component';
import {
  ActaAsistentesEditarModalComponent,
} from './acta-formulario/acta-asistentes/acta-asistentes-editar-modal/acta-asistentes-editar-modal.component';
import {
  ActaAsistentesListadoComponent,
} from './acta-formulario/acta-asistentes/acta-asistentes-listado/acta-asistentes-listado.component';
import { ActaDatosGeneralesComponent } from './acta-formulario/acta-datos-generales/acta-datos-generales.component';
import { ActaMemoriasComponent } from './acta-formulario/acta-memorias/acta-memorias.component';
import { ActaListadoComponent } from './acta-listado/acta-listado.component';
import { ActaRoutingModule } from './acta-routing.module';
import { ActaResolver } from './acta.resolver';

@NgModule({
  declarations: [
    ActaCrearComponent,
    ActaDatosGeneralesComponent,
    ActaMemoriasComponent,
    ActaListadoComponent,
    ActaAsistentesListadoComponent,
    ActaEditarComponent,
    ActaAsistentesEditarModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    ActaRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    ActaResolver
  ]
})
export class ActaModule { }
