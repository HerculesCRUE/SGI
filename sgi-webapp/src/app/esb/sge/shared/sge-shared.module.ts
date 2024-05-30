import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { SearchProyectoEconomicoModalComponent } from './search-proyecto-economico-modal/search-proyecto-economico-modal.component';
import { SelectPartidaPresupuestariaSgeComponent } from './select-partida-presupuestaria-sge/select-partida-presupuestaria-sge.component';
import { SelectProyectoEconomicoComponent } from './select-proyecto-economico/select-proyecto-economico.component';

@NgModule({
  declarations: [
    SelectPartidaPresupuestariaSgeComponent,
    SearchProyectoEconomicoModalComponent,
    SelectProyectoEconomicoComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule
  ],
  exports: [
    SelectPartidaPresupuestariaSgeComponent,
    SearchProyectoEconomicoModalComponent,
    SelectProyectoEconomicoComponent
  ]
})
export class SgeSharedModule { }
