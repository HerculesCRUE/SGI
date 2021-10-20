import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FormlyFormsModule } from '@formly-forms/formly-forms.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { ChecklistFormularioComponent } from './checklist-formulario/checklist-formulario.component';
import { ChecklistRoutingModule } from './checklist-routing.module';

@NgModule({
  declarations: [
    ChecklistFormularioComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    ChecklistRoutingModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    SgiAuthModule,
    FormlyFormsModule,
  ]
})
export class ChecklistModule { }
