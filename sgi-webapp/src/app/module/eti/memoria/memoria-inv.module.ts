import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormlyFormsModule } from '@formly-forms/formly-forms.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { MemoriaEditarComponent } from '../memoria/memoria-editar/memoria-editar.component';
import { MemoriaDatosGeneralesComponent } from '../memoria/memoria-formulario/memoria-datos-generales/memoria-datos-generales.component';
import { EtiSharedModule } from '../shared/eti-shared.module';
import { MemoriaCrearComponent } from './memoria-crear/memoria-crear.component';
import { MemoriaCrearGuard } from './memoria-crear/memoria-crear.guard';
import { MemoriaDocumentacionComponent } from './memoria-formulario/memoria-documentacion/memoria-documentacion.component';
import { MemoriaEvaluacionesComponent } from './memoria-formulario/memoria-evaluaciones/memoria-evaluaciones.component';
import { MemoriaFormularioComponent } from './memoria-formulario/memoria-formulario/memoria-formulario.component';
import { MemoriaInformesComponent } from './memoria-formulario/memoria-informes/memoria-informes.component';
import { MemoriaRetrospectivaComponent } from './memoria-formulario/memoria-retrospectiva/memoria-retrospectiva.component';
import { MemoriaSeguimientoAnualComponent } from './memoria-formulario/memoria-seguimiento-anual/memoria-seguimiento-anual.component';
import { MemoriaSeguimientoFinalComponent } from './memoria-formulario/memoria-seguimiento-final/memoria-seguimiento-final.component';
import { MemoriaListadoInvComponent } from './memoria-listado-inv/memoria-listado-inv.component';
import { MemoriaRoutingInvModule } from './memoria-routing-inv.module';
import { MemoriaResolver } from './memoria.resolver';
import { MemoriaDocumentacionMemoriaModalComponent } from './modals/memoria-documentacion-memoria-modal/memoria-documentacion-memoria-modal.component';

@NgModule({
  declarations: [
    MemoriaListadoInvComponent,
    MemoriaCrearComponent,
    MemoriaDatosGeneralesComponent,
    MemoriaFormularioComponent,
    MemoriaEditarComponent,
    MemoriaDocumentacionComponent,
    MemoriaDocumentacionMemoriaModalComponent,
    MemoriaEvaluacionesComponent,
    MemoriaInformesComponent,
    MemoriaSeguimientoAnualComponent,
    MemoriaSeguimientoFinalComponent,
    MemoriaRetrospectivaComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    MemoriaRoutingInvModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule,
    FormlyFormsModule,
    EtiSharedModule
  ],
  providers: [
    MemoriaResolver,
    MemoriaCrearGuard
  ]
})
export class MemoriaInvModule { }
