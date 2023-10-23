import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldControl } from '@angular/material/form-field';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { EtiSharedModule } from '../shared/eti-shared.module';
import { ActaCrearComponent } from './acta-crear/acta-crear.component';
import { ActaEditarComponent } from './acta-editar/acta-editar.component';
import {
  ActaAsistentesEditarModalComponent
} from './acta-formulario/acta-asistentes/acta-asistentes-editar-modal/acta-asistentes-editar-modal.component';
import {
  ActaAsistentesListadoComponent
} from './acta-formulario/acta-asistentes/acta-asistentes-listado/acta-asistentes-listado.component';
import { ActaComentariosComponent } from './acta-formulario/acta-comentarios/acta-comentarios.component';
import { ActaDatosGeneralesComponent } from './acta-formulario/acta-datos-generales/acta-datos-generales.component';
import { ActaMemoriasComponent } from './acta-formulario/acta-memorias/acta-memorias.component';
import { ActaGeneralListadoExportService } from './acta-general-listado-export.service';
import { ActaListadoExportService } from './acta-listado-export.service';
import { ActaListadoComponent } from './acta-listado/acta-listado.component';
import { ActaMemoriaListadoExportService } from './acta-memoria-listado-export.service';
import { ActaRoutingModule } from './acta-routing.module';
import { ActaResolver } from './acta.resolver';
import { ActaListadoExportModalComponent } from './modals/acta-listado-export-modal/acta-listado-export-modal.component';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import { ActaListadoComentariosEquipoEvaluadorComponent } from './acta-formulario/acta-listado-comentarios-equipo-evaluador/acta-listado-comentarios-equipo-evaluador.component';

@NgModule({
  declarations: [
    ActaCrearComponent,
    ActaDatosGeneralesComponent,
    ActaMemoriasComponent,
    ActaListadoComponent,
    ActaAsistentesListadoComponent,
    ActaEditarComponent,
    ActaAsistentesEditarModalComponent,
    ActaComentariosComponent,
    ActaListadoExportModalComponent,
    ActaListadoComentariosEquipoEvaluadorComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    ActaRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    SgpSharedModule,
    EtiSharedModule,
    CKEditorModule
  ],
  providers: [
    ActaResolver,
    LuxonDatePipe,
    ActaListadoExportService,
    ActaGeneralListadoExportService,
    ActaMemoriaListadoExportService,
    ActaListadoComentariosEquipoEvaluadorComponent
  ]
})
export class ActaModule { }
