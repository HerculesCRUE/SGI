import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { ConceptoGastoListadoComponent } from './concepto-gasto-listado/concepto-gasto-listado.component';
import { ConceptoGastoModalComponent } from './concepto-gasto-modal/concepto-gasto-modal.component';
import { ConceptoGastoRoutingModule } from './concepto-gasto-routing.module';


@NgModule({
  declarations: [ConceptoGastoListadoComponent, ConceptoGastoModalComponent],
  imports: [
    CommonModule,
    ConceptoGastoRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    MaterialDesignModule,
    TranslateModule,
    SharedModule,
    SgiAuthModule
  ]
})
export class ConceptoGastoModule { }
