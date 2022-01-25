import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { ConvocatoriaConceptoGastoCrearComponent } from './convocatoria-concepto-gasto-crear/convocatoria-concepto-gasto-crear.component';
import { ConvocatoriaConceptoGastoDataResolver } from './convocatoria-concepto-gasto-data.resolver';
import {
  ConvocatoriaConceptoGastoEditarComponent
} from './convocatoria-concepto-gasto-editar/convocatoria-concepto-gasto-editar.component';
import { ConvocatoriaConceptoGastoCodigoEcComponent } from './convocatoria-concepto-gasto-formulario/convocatoria-concepto-gasto-codigo-ec/convocatoria-concepto-gasto-codigo-ec.component';
import { ConvocatoriaConceptoGastoDatosGeneralesComponent } from './convocatoria-concepto-gasto-formulario/convocatoria-concepto-gasto-datos-generales/convocatoria-concepto-gasto-datos-generales.component';
import { ConvocatoriaConceptoGastoRouting } from './convocatoria-concepto-gasto-routing.module';
import { ConvocatoriaConceptoGastoCodigoEcModalComponent } from './modals/convocatoria-concepto-gasto-codigo-ec-modal/convocatoria-concepto-gasto-codigo-ec-modal.component';

@NgModule({
  declarations: [
    ConvocatoriaConceptoGastoCrearComponent,
    ConvocatoriaConceptoGastoEditarComponent,
    ConvocatoriaConceptoGastoDatosGeneralesComponent,
    ConvocatoriaConceptoGastoCodigoEcComponent,
    ConvocatoriaConceptoGastoCodigoEcModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ConvocatoriaConceptoGastoRouting,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    CspSharedModule
  ],
  providers: [
    ConvocatoriaConceptoGastoDataResolver
  ]
})
export class ConvocatoriaConceptoGastoModule { }
