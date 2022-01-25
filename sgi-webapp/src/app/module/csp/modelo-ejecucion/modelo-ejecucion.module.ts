import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModeloEjecucionListadoComponent } from './modelo-ejecucion-listado/modelo-ejecucion-listado.component';
import { ModeloEjecuccionRoutingModule } from './modelo-ejecucion-routing.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { ModeloEjecucionCrearComponent } from './modelo-ejecucion-crear/modelo-ejecucion-crear.component';
import { ModeloEjecucionDatosGeneralesComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-datos-generales/modelo-ejecucion-datos-generales.component';
import { ModeloEjecucionResolver } from './modelo-ejecucion.resolver';
import { ModeloEjecucionEditarComponent } from './modelo-ejecucion-editar/modelo-ejecucion-editar.component';
import { ModeloEjecucionTipoEnlaceComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-enlace/modelo-ejecucion-tipo-enlace.component';
import { ModeloEjecucionTipoFinalidadComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-finalidad/modelo-ejecucion-tipo-finalidad.component';
import { ModeloEjecucionTipoEnlaceModalComponent } from './modals/modelo-ejecucion-tipo-enlace-modal/modelo-ejecucion-tipo-enlace-modal.component';
import { ModeloEjecucionTipoFinalidadModalComponent } from './modals/modelo-ejecucion-tipo-finalidad-modal/modelo-ejecucion-tipo-finalidad-modal.component';
import { ModeloEjecucionTipoFaseComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-fase/modelo-ejecucion-tipo-fase.component';
import { ModeloEjecucionTipoDocumentoComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-documento/modelo-ejecucion-tipo-documento.component';
import { ModeloEjecucionTipoDocumentoModalComponent } from './modals/modelo-ejecucion-tipo-documento-modal/modelo-ejecucion-tipo-documento-modal.component';
import { ModeloEjecucionTipoHitoComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-hito/modelo-ejecucion-tipo-hito.component';
import { ModeloEjecucionTipoFaseModalComponent } from './modals/modelo-ejecucion-tipo-fase-modal/modelo-ejecucion-tipo-fase-modal.component';
import { ModeloEjecucionTipoUnidadGestionComponent } from './modelo-ejecucion-formulario/modelo-ejecucion-tipo-unidad-gestion/modelo-ejecucion-tipo-unidad-gestion.component';
import { ModeloEjecucionTipoUnidadGestionModalComponent } from './modals/modelo-ejecucion-tipo-unidad-gestion-modal/modelo-ejecucion-tipo-unidad-gestion-modal.component';
import { ModeloEjecucionTipoHitoModalComponent } from './modals/modelo-ejecucion-tipo-hito-modal/modelo-ejecucion-tipo-hito-modal.component';
import { SgiAuthModule } from '@sgi/framework/auth';
import { CspSharedModule } from '../shared/csp-shared.module';

@NgModule({
  declarations: [
    ModeloEjecucionListadoComponent,
    ModeloEjecucionCrearComponent,
    ModeloEjecucionDatosGeneralesComponent,
    ModeloEjecucionEditarComponent,
    ModeloEjecucionTipoEnlaceComponent,
    ModeloEjecucionTipoEnlaceModalComponent,
    ModeloEjecucionTipoFinalidadComponent,
    ModeloEjecucionTipoFinalidadModalComponent,
    ModeloEjecucionTipoFaseModalComponent,
    ModeloEjecucionTipoFaseComponent,
    ModeloEjecucionTipoDocumentoComponent,
    ModeloEjecucionTipoDocumentoModalComponent,
    ModeloEjecucionTipoHitoComponent,
    ModeloEjecucionTipoHitoModalComponent,
    ModeloEjecucionTipoUnidadGestionComponent,
    ModeloEjecucionTipoUnidadGestionModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    ModeloEjecuccionRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule
  ],
  providers: [
    ModeloEjecucionResolver
  ]
})
export class ModeloEjecucionModule { }
