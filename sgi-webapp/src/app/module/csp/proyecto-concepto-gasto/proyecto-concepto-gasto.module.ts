import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { CspSharedModule } from '../shared/csp-shared.module';
import { ProyectoConceptoGastoCodigoEcModalComponent } from './modals/proyecto-concepto-gasto-codigo-ec-modal/proyecto-concepto-gasto-codigo-ec-modal.component';
import { ProyectoConceptoGastoCrearComponent } from './proyecto-concepto-gasto-crear/proyecto-concepto-gasto-crear.component';
import { ProyectoConceptoGastoDataResolver } from './proyecto-concepto-gasto-data.resolver';
import { ProyectoConceptoGastoEditarComponent } from './proyecto-concepto-gasto-editar/proyecto-concepto-gasto-editar.component';
import { ProyectoConceptoGastoCodigoEcComponent } from './proyecto-concepto-gasto-formulario/proyecto-concepto-gasto-codigo-ec/proyecto-concepto-gasto-codigo-ec.component';
import { ProyectoConceptoGastoDatosGeneralesComponent } from './proyecto-concepto-gasto-formulario/proyecto-concepto-gasto-datos-generales/proyecto-concepto-gasto-datos-generales.component';
import { ProyectoConceptoGastoRouting } from './proyecto-concepto-gasto-routing.module';

@NgModule({
  declarations: [
    ProyectoConceptoGastoCrearComponent,
    ProyectoConceptoGastoEditarComponent,
    ProyectoConceptoGastoDatosGeneralesComponent,
    ProyectoConceptoGastoCodigoEcComponent,
    ProyectoConceptoGastoCodigoEcModalComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    ProyectoConceptoGastoRouting,
    SgiAuthModule,
    FormsModule,
    ReactiveFormsModule,
    CspSharedModule
  ],
  providers: [
    ProyectoConceptoGastoDataResolver
  ]
})
export class ProyectoConceptoGastoModule { }
