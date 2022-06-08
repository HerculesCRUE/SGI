import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormlyFormsModule } from '@formly-forms/formly-forms.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SgpSharedModule } from 'src/app/esb/sgp/shared/sgp-shared.module';
import { SharedFormlyFormsModule } from 'src/app/esb/shared/formly-forms/shared-formly-forms.module';
import { EtiSharedModule } from '../shared/eti-shared.module';
import { MemoriaCrearGuard } from './memoria-crear/memoria-crear.guard';
import { MemoriaEvaluacionesListadoExportService } from './memoria-evaluaciones-listado-export.service';
import { MemoriaGeneralListadoExportService } from './memoria-general-listado-export.service';
import { MemoriaListadoExportService } from './memoria-listado-export.service';
import { MemoriaListadoGesComponent } from './memoria-listado-ges/memoria-listado-ges.component';
import { MemoriaRoutingGesModule } from './memoria-routing-ges.module';
import { MemoriaResolver } from './memoria.resolver';
import { MemoriaListadoExportModalComponent } from './modals/memoria-listado-export-modal/memoria-listado-export-modal.component';

@NgModule({
  declarations: [
    MemoriaListadoGesComponent,
    MemoriaListadoExportModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    MemoriaRoutingGesModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule,
    FormlyFormsModule,
    SharedFormlyFormsModule,
    EtiSharedModule,
    SgpSharedModule
  ],
  providers: [
    MemoriaResolver,
    MemoriaCrearGuard,
    MemoriaListadoExportService,
    MemoriaGeneralListadoExportService,
    MemoriaEvaluacionesListadoExportService
  ]
})
export class MemoriaGesModule { }
