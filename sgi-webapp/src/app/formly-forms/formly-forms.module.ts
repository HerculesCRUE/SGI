import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormlyModule } from '@ngx-formly/core';
import { FormlyMaterialModule } from '@ngx-formly/material';
import { FormlyMatDatepickerModule } from '@ngx-formly/material/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { TipoValorSocialComponent } from './types/tipo-valor-social.component';
import { requiredChecked } from './validators/utils.validator';
import { InfoDivWrapperComponent } from './wrappers/info-div/info-div.wrapper';
import { PanelWrapperComponent } from './wrappers/panel/panel.wrapper';
import { SubtitleDivWrapperComponent } from './wrappers/subtitle-div/subtitle-div.wrapper';
import { TitleDivWrapperComponent } from './wrappers/title-div/title-div.wrapper';

@NgModule({
  declarations: [
    PanelWrapperComponent,
    TitleDivWrapperComponent,
    SubtitleDivWrapperComponent,
    InfoDivWrapperComponent,
    TipoValorSocialComponent
  ],
  imports: [
    CommonModule,
    MaterialDesignModule,
    TranslateModule,
    FormsModule,
    ReactiveFormsModule,
    FormlyMatDatepickerModule,
    FormlyModule.forChild({
      types: [
        {
          name: 'tipo-valor-social',
          component: TipoValorSocialComponent,
          wrappers: ['form-field'],
        },
        { name: 'documento', extends: 'radio' }
      ],
      wrappers: [
        {
          name: 'expansion-panel',
          component: PanelWrapperComponent
        },
        {
          name: 'title-div',
          component: TitleDivWrapperComponent
        },
        {
          name: 'subtitle-div',
          component: SubtitleDivWrapperComponent
        },
        {
          name: 'info-div',
          component: InfoDivWrapperComponent
        }
      ],
      validators: [
        { name: 'requiredChecked', validation: requiredChecked },
        /** TODO: Remove when any declared template didn't use it */
        { name: 'nif', validation: Validators.required }
      ]
    }),
    FormlyMaterialModule
  ],
  exports: [
    FormlyMatDatepickerModule,
    FormlyModule,
    FormlyMaterialModule
  ]
})
export class FormlyFormsModule { }
