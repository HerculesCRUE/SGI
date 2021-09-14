import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormlyFormsModule } from '@formly-forms/formly-forms.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormlyMaterialModule } from '@ngx-formly/material';
import { FormlyMatDatepickerModule } from '@ngx-formly/material/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { SharedFormlyFormsModule } from '../../shared/formly-forms/shared-formly-forms.module';
import { ProyectoEconomicoFormlyModalComponent } from './proyecto-economico-formly-modal/proyecto-economico-formly-modal.component';

@NgModule({
  declarations: [
    ProyectoEconomicoFormlyModalComponent,
  ],
  imports: [
    SharedModule,
    SharedFormlyFormsModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    FormlyMatDatepickerModule,
    FormlyMaterialModule,
    SharedFormlyFormsModule,
    FormlyFormsModule
  ],
  exports: [
    ProyectoEconomicoFormlyModalComponent
  ]
})
export class SgeFormlyFormsModule { }
