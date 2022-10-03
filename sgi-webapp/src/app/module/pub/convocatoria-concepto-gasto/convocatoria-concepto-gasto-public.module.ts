import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { ConvocatoriaConceptoGastoPublicEditarComponent } from './convocatoria-concepto-gasto-editar/convocatoria-concepto-gasto-public-editar.component';
import { ConvocatoriaConceptoGastoCodigoEcPublicComponent } from './convocatoria-concepto-gasto-formulario/convocatoria-concepto-gasto-codigo-ec/convocatoria-concepto-gasto-codigo-ec-public.component';
import { ConvocatoriaConceptoGastoDatosGeneralesPublicComponent } from './convocatoria-concepto-gasto-formulario/convocatoria-concepto-gasto-datos-generales/convocatoria-concepto-gasto-datos-generales-public.component';
import { ConvocatoriaConceptoGastoPublicDataResolver } from './convocatoria-concepto-gasto-public-data.resolver';
import { ConvocatoriaConceptoGastoPublicRouting } from './convocatoria-concepto-gasto-public-routing.module';

@NgModule({
  declarations: [
    ConvocatoriaConceptoGastoPublicEditarComponent,
    ConvocatoriaConceptoGastoDatosGeneralesPublicComponent,
    ConvocatoriaConceptoGastoCodigoEcPublicComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ConvocatoriaConceptoGastoPublicRouting,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    ConvocatoriaConceptoGastoPublicDataResolver
  ]
})
export class ConvocatoriaConceptoGastoPublicModule { }
