import { CommonModule, DecimalPipe } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { InvencionRepartoCrearComponent } from './invencion-reparto-crear/invencion-reparto-crear.component';
import { InvencionRepartoDataResolver } from './invencion-reparto-data.resolver';
import { InvencionRepartoEditarComponent } from './invencion-reparto-editar/invencion-reparto-editar.component';
import { InvencionRepartoDatosGeneralesComponent } from './invencion-reparto-formulario/invencion-reparto-datos-generales/invencion-reparto-datos-generales.component';
import { InvencionRepartoRoutingModule } from './invencion-reparto-routing.module';
import { RepartoGastoModalComponent } from './modals/reparto-gasto-modal/reparto-gasto-modal.component';
import { RepartoIngresoModalComponent } from './modals/reparto-ingreso-modal/reparto-ingreso-modal.component';
import { InvencionRepartoEquipoInventorComponent } from './invencion-reparto-formulario/invencion-reparto-equipo-inventor/invencion-reparto-equipo-inventor.component';
import { RepartoEquipoModalComponent } from './modals/reparto-equipo-modal/reparto-equipo-modal.component';
import { CspSharedModule } from '../../csp/shared/csp-shared.module';
import { InvencionRepartoDataResolverService } from './services/invencion-reparto-data-resolver.service';


@NgModule({
  declarations: [
    InvencionRepartoCrearComponent,
    InvencionRepartoEditarComponent,
    InvencionRepartoDatosGeneralesComponent,
    RepartoGastoModalComponent,
    RepartoIngresoModalComponent,
    InvencionRepartoEquipoInventorComponent,
    RepartoEquipoModalComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    InvencionRepartoRoutingModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule,
    FormsModule,
    SgiAuthModule,
    CspSharedModule
  ],
  providers: [
    InvencionRepartoDataResolverService,
    InvencionRepartoDataResolver,
    DecimalPipe
  ]
})
export class InvencionRepartoModule { }
