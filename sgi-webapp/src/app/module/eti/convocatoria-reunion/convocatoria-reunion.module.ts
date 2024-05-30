import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { EtiSharedModule } from '../shared/eti-shared.module';
import { ConvocatoriaReunionCrearComponent } from './convocatoria-reunion-crear/convocatoria-reunion-crear.component';
import { ConvocatoriaReunionDataResolver } from './convocatoria-reunion-data.resolver';
import { ConvocatoriaReunionEditarComponent } from './convocatoria-reunion-editar/convocatoria-reunion-editar.component';
import { ConvocatoriaReunionAsignacionMemoriasListadoComponent } from './convocatoria-reunion-formulario/convocatoria-reunion-asignacion-memorias/convocatoria-reunion-asignacion-memorias-listado/convocatoria-reunion-asignacion-memorias-listado.component';
import { ConvocatoriaReunionAsignacionMemoriasModalComponent } from './convocatoria-reunion-formulario/convocatoria-reunion-asignacion-memorias/convocatoria-reunion-asignacion-memorias-modal/convocatoria-reunion-asignacion-memorias-modal.component';
import { ConvocatoriaReunionDatosGeneralesComponent } from './convocatoria-reunion-formulario/convocatoria-reunion-datos-generales/convocatoria-reunion-datos-generales.component';
import { ConvocatoriaReunionDocumentacionComponent } from './convocatoria-reunion-formulario/convocatoria-reunion-documentacion/convocatoria-reunion-documentacion.component';
import { ConvocatoriaReunionGeneralListadoExportService } from './convocatoria-reunion-general-listado-export.service';
import { ConvocatoriaReunionListadoExportService } from './convocatoria-reunion-listado-export.service';
import { ConvocatoriaReunionListadoComponent } from './convocatoria-reunion-listado/convocatoria-reunion-listado.component';
import { ConvocatoriaReunionMemoriasListadoExportService } from './convocatoria-reunion-memorias-listado-export.service';
import { ConvocatoriaReunionRoutingModule } from './convocatoria-reunion-routing.module';
import { ConvocatoriaReunionDocumentacionConvocatoriaReunionModalComponent } from './modals/convocatoria-reunion-documentacion-convocatoria-reunion-modal/convocatoria-reunion-documentacion-convocatoria-reunion-modal.component';
import { ConvocatoriaReunionListadoExportModalComponent } from './modals/convocatoria-reunion-listado-export-modal/convocatoria-reunion-listado-export-modal.component';


@NgModule({
  declarations: [
    ConvocatoriaReunionCrearComponent,
    ConvocatoriaReunionListadoComponent,
    ConvocatoriaReunionDatosGeneralesComponent,
    ConvocatoriaReunionAsignacionMemoriasListadoComponent,
    ConvocatoriaReunionAsignacionMemoriasModalComponent,
    ConvocatoriaReunionEditarComponent,
    ConvocatoriaReunionListadoExportModalComponent,
    ConvocatoriaReunionDocumentacionComponent,
    ConvocatoriaReunionDocumentacionConvocatoriaReunionModalComponent,
  ],
  imports: [
    SharedModule,
    CommonModule,
    ConvocatoriaReunionRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    EtiSharedModule,
    CKEditorModule
  ],
  providers: [
    ConvocatoriaReunionDataResolver,
    ConvocatoriaReunionListadoExportService,
    ConvocatoriaReunionGeneralListadoExportService,
    ConvocatoriaReunionMemoriasListadoExportService
  ]
})
export class ConvocatoriaReunionModule { }
