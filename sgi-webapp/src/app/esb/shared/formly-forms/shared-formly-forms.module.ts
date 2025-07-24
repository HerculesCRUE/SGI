import { NgxMatDatetimePickerModule } from '@angular-material-components/datetime-picker';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { I18nComponentsModule } from '@components/i18n/i18n-components.module';
import { requiredRowTable } from '@formly-forms/validators/utils.validator';
import { MaterialDesignModule } from '@material/material-design.module';
import { FORMLY_CONFIG, FormlyModule } from '@ngx-formly/core';
import { FormlySelectModule } from '@ngx-formly/core/select';
import { FormlyMaterialModule } from '@ngx-formly/material';
import { FormlyMatDatepickerModule } from '@ngx-formly/material/datepicker';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { DateTimePickerTypeComponent } from './types/datetimepicker.type';
import { SelectAttributesTypeComponent } from './types/select-attributes.type';
import { SelectObjectTypeComponent } from './types/select-object.type';
import { SelectTypeComponent } from './types/select.type';
import { TableCRUDModalComponent } from './types/table-crud/table-crud-modal/table-crud-modal.component';
import { TableCRUDTypeComponent } from './types/table-crud/table-crud.type';
import { GlobalWrapperComponent } from './wrappers/global/global.wrapper';
import { MatCardGroupWrapperComponent } from './wrappers/mat-card-group/mat-card-group.wrapper';

const MSG_FORMLY_VALIDATIONS_ONE_ROW_REQUIRED = marker('msg.formly.validations.oneRowRequired');
const MSG_FORMLY_VALIDATIONS_REQUIRED = marker('msg.formly.validations.required');

@NgModule({
  declarations: [
    SelectTypeComponent,
    SelectObjectTypeComponent,
    TableCRUDTypeComponent,
    TableCRUDModalComponent,
    SelectAttributesTypeComponent,
    DateTimePickerTypeComponent,
    MatCardGroupWrapperComponent,
    GlobalWrapperComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    NgxMatDatetimePickerModule,
    FormlyMatDatepickerModule,
    FormlySelectModule,
    FormlyMaterialModule,
    I18nComponentsModule,
    FormlyModule.forChild({
      types: [
        {
          name: 'select',
          component: SelectTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'select-object',
          component: SelectObjectTypeComponent,
          wrappers: ['form-field']
        },
        {
          name: 'table-crud',
          component: TableCRUDTypeComponent
        },
        {
          name: 'table-crud-one-element',
          component: TableCRUDTypeComponent
        },
        {
          name: 'select-attributes',
          component: SelectAttributesTypeComponent,
          wrappers: ['form-field'],
        },
        {
          name: 'dateTimePicker',
          component: DateTimePickerTypeComponent,
          wrappers: ['form-field'],
        }
      ],
      wrappers: [
        {
          name: 'mat-card-group',
          component: MatCardGroupWrapperComponent
        },
        {
          name: 'global',
          component: GlobalWrapperComponent
        }
      ],
      validators: [
        { name: 'oneRowRequired', validation: requiredRowTable },
      ]
    })
  ],
  exports: [
    SelectTypeComponent,
    SelectObjectTypeComponent,
    TableCRUDTypeComponent,
    TableCRUDModalComponent,
    SelectAttributesTypeComponent,
    DateTimePickerTypeComponent,
    MatCardGroupWrapperComponent,
    GlobalWrapperComponent
  ],
  providers: [
    {
      provide: FORMLY_CONFIG,
      useFactory: (translate: TranslateService) => {
        return {
          validationMessages: [
            {
              name: 'oneRowRequired',
              message: () => translate.get(MSG_FORMLY_VALIDATIONS_ONE_ROW_REQUIRED)
            },
            {
              name: 'required',
              message: () => translate.get(MSG_FORMLY_VALIDATIONS_REQUIRED)
            }
          ]
        };
      },
      deps: [TranslateService],
      multi: true
    }
  ]
})
export class SharedFormlyFormsModule { }
