import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { I18nCkeditorComponent } from './i18n-ckeditor/i18n-ckeditor.component';
import { I18nDisplayFieldComponent } from './i18n-display-field/i18n-display-field.component';
import { I18nFieldTooltipPipe } from './i18n-field-tooltip.pipe';
import { I18nFieldValuePipe } from './i18n-field-value.pipe';
import { I18nFieldsValuesPipe } from './i18n-fields-values.pipe';
import { I18nInputComponent } from './i18n-input/i18n-input.component';
import { I18nLinkComponent } from './i18n-link/i18n-link.component';
import { I18nTableFieldComponent } from './i18n-table-field/i18n-table-field.component';
import { I18nTableListFieldsComponent } from './i18n-table-list-fields/i18n-table-list-fields.component';
import { I18nTextareaComponent } from './i18n-textarea/i18n-textarea.component';
import { I18nTitleFieldComponent } from './i18n-title-field/i18n-title-field.component';
import { I18nTreeFieldComponent } from './i18n-tree-field/i18n-tree-field.component';

@NgModule({
  declarations: [
    I18nCkeditorComponent,
    I18nDisplayFieldComponent,
    I18nFieldsValuesPipe,
    I18nFieldTooltipPipe,
    I18nFieldValuePipe,
    I18nInputComponent,
    I18nLinkComponent,
    I18nTableFieldComponent,
    I18nTableListFieldsComponent,
    I18nTextareaComponent,
    I18nTitleFieldComponent,
    I18nTreeFieldComponent
  ],
  imports: [
    CommonModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
    TranslateModule,
    CKEditorModule
  ],
  exports: [
    I18nCkeditorComponent,
    I18nDisplayFieldComponent,
    I18nFieldsValuesPipe,
    I18nFieldTooltipPipe,
    I18nFieldValuePipe,
    I18nInputComponent,
    I18nLinkComponent,
    I18nTableFieldComponent,
    I18nTableListFieldsComponent,
    I18nTextareaComponent,
    I18nTitleFieldComponent,
    I18nTreeFieldComponent
  ]
})
export class I18nComponentsModule { }