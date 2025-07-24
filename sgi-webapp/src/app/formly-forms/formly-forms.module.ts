import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AbstractControl, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import { I18nComponentsModule } from '@components/i18n/i18n-components.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { FORMLY_CONFIG, FormlyModule } from '@ngx-formly/core';
import { FormlySelectModule } from '@ngx-formly/core/select';
import { FormlyMaterialModule } from '@ngx-formly/material';
import { FormlyMatDatepickerModule } from '@ngx-formly/material/datepicker';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { SgiFormlyFieldConfig } from './formly-field-config';
import { CKEditorTemplate } from './types/ckeditor-template';
import { I18nCkeditorTypeComponent } from './types/i18n-ckeditor';
import { I18nInputTypeComponent } from './types/i18n-input';
import { I18nTextareaTypeComponent } from './types/i18n-textarea.type';
import { SelectEntityTypeComponent } from './types/select-entity.type';
import { SelectProcedimientosTypeComponent } from './types/select-procedimientos.type';
import { TipoValorSocialComponent } from './types/tipo-valor-social.component';
import { dateIsAfter, dateIsBetween } from './validators/date.validator';
import { emailPrincipalUniqueValidator } from './validators/email-principal-unique.validator';
import { emailValidator } from './validators/email.validator';
import { fieldArrayMax } from './validators/field-array-max.validator';
import { IValidationError } from './validators/models/validation-error';
import { IValidatorOptions } from './validators/models/validator-options';
import { multicheckboxRestricted } from './validators/multicheckbox.validator';
import { requiredChecked } from './validators/utils.validator';
import { InfoDivWrapperComponent } from './wrappers/info-div/info-div.wrapper';
import { PanelWrapperComponent } from './wrappers/panel/panel.wrapper';
import { SubtitleDivWrapperComponent } from './wrappers/subtitle-div/subtitle-div.wrapper';
import { TitleDivWrapperComponent } from './wrappers/title-div/title-div.wrapper';
import { WarnDivWrapperComponent } from './wrappers/warn-div/warn-div.wrapper';

export function validatorFactory(
  validator: (control: AbstractControl, field: SgiFormlyFieldConfig, options: IValidatorOptions, translate: TranslateService) => IValidationError | null,
  translate: TranslateService
) {
  return (control: AbstractControl, field: SgiFormlyFieldConfig, options: IValidatorOptions): ValidationErrors | null => {
    const validationError = validator(control, field, options, translate);
    if (validationError) {
      return {
        [validationError.name]: {
          message: validationError.customMessage ? validationError.customMessage : validationError.defatultMessage
        }
      };
    }
    return null;
  };
}

@NgModule({
  declarations: [
    CKEditorTemplate,
    I18nCkeditorTypeComponent,
    I18nInputTypeComponent,
    I18nTextareaTypeComponent,
    InfoDivWrapperComponent,
    PanelWrapperComponent,
    SelectEntityTypeComponent,
    SelectProcedimientosTypeComponent,
    SubtitleDivWrapperComponent,
    TipoValorSocialComponent,
    TitleDivWrapperComponent,
    WarnDivWrapperComponent,
  ],
  imports: [
    CKEditorModule,
    CommonModule,
    FormlyMatDatepickerModule,
    FormlyMaterialModule,
    FormlyModule.forChild({
      types: [
        {
          name: 'ckeditor',
          component: CKEditorTemplate,
          wrappers: ['form-field'],
        },
        { name: 'documento', extends: 'radio' },
        {
          name: 'select-entity',
          component: SelectEntityTypeComponent,
          wrappers: ['form-field'],
        },
        {
          name: 'selector-procedimientos',
          component: SelectProcedimientosTypeComponent,
          wrappers: ['form-field'],
        },
        {
          name: 'tipo-valor-social',
          component: TipoValorSocialComponent,
          wrappers: ['form-field'],
        },
        {
          name: 'i18n-textarea',
          component: I18nTextareaTypeComponent,
          wrappers: ['form-field'],
        },
        {
          name: 'i18n-input',
          component: I18nInputTypeComponent,
          wrappers: ['form-field'],
        },
        {
          name: 'i18n-ckeditor',
          component: I18nCkeditorTypeComponent,
          wrappers: ['form-field'],
        },
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
        },
        {
          name: 'warn-div',
          component: WarnDivWrapperComponent
        },
      ]
    }),
    FormlySelectModule,
    FormsModule,
    I18nComponentsModule,
    MaterialDesignModule,
    ReactiveFormsModule,
    TranslateModule
  ],
  exports: [
    CKEditorModule,
    FormlyMatDatepickerModule,
    FormlyMaterialModule,
    FormlyModule
  ],
  providers: [
    {
      provide: FORMLY_CONFIG,
      useFactory: (translate: TranslateService) => ({
        validators: [
          { name: 'requiredChecked', validation: requiredChecked },
          /** TODO: Remove when any declared template didn't use it */
          { name: 'nif', validation: Validators.required },
          {
            name: 'date-is-after',
            validation: validatorFactory(dateIsAfter, translate)
          },
          {
            name: 'date-is-between',
            validation: validatorFactory(dateIsBetween, translate)
          },
          {
            name: 'email',
            validation: validatorFactory(emailValidator, translate)
          },
          {
            name: 'email-principal-unique',
            validation: validatorFactory(emailPrincipalUniqueValidator, translate)
          },
          {
            name: 'field-array-max',
            validation: validatorFactory(fieldArrayMax, translate)
          },
          {
            name: 'multicheckbox-restricted',
            validation: validatorFactory(multicheckboxRestricted, translate)
          }
        ]
      }),
      deps: [TranslateService],
      multi: true
    }
  ]
})
export class FormlyFormsModule { }
