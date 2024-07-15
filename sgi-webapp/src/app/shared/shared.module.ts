import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FormlyFormsModule } from '@formly-forms/formly-forms.module';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { ActionDialogComponent } from './action-dialog/action-dialog.component';
import { ActionEmptyFooterComponent } from './action-empty-footer/action-empty-footer.component';
import { ActionFooterButtonComponent } from './action-footer-button/action-footer-button.component';
import { ActionFooterMessageComponent } from './action-footer-message/action-footer-message.component';
import { ActionFooterComponent } from './action-footer/action-footer.component';
import { ActionFragmentLinkItemComponent } from './action-fragment-link-item/action-fragment-link-item.component';
import { ActionFragmentMenuGroupComponent } from './action-fragment-menu-group/action-fragment-menu-group.component';
import { ActionFragmentMenuItemComponent } from './action-fragment-menu-item/action-fragment-menu-item.component';
import { ActionComponent } from './action/action.component';
import { BreadcrumbComponent } from './breadcrumb/breadcrumb.component';
import { DialogContentComponent } from './dialog-content/dialog-content.component';
import { DialogFooterComponent } from './dialog-footer/dialog-footer.component';
import { DialogHeaderComponent } from './dialog-header/dialog-header.component';
import { FormAutocompleteOffDirective } from './directives/form-autocomplete-off.directive';
import { NotFoundErrorDirective } from './directives/not-found-error.directive';
import { SgiInputWithThousandSeparator } from './directives/sgi-input-number-separator.directive';
import { SgiTooltipDirective } from './directives/sgi-tooltip.directive';
import { ErrorMessageComponent } from './error-message/error-message.component';
import { ExportDialogComponent } from './export-dialog/export-dialog.component';
import { FieldInfoComponent } from './field-info/field-info.component';
import { SgiFileUploadComponent } from './file-upload/file-upload.component';
import { FooterCrearComponent } from './footers/footer-crear/footer-crear.component';
import { FragmentContentComponent } from './fragment-content/fragment-content.component';
import { FragmentTitleComponent } from './fragment-title/fragment-title.component';
import { FragmentComponent } from './fragment/fragment.component';
import { IconChangesComponent } from './icon-changes/icon-changes.component';
import { IconErrorsComponent } from './icon-errors/icon-errors.component';
import { IconProblemsComponent } from './icon-problems/icon-problems.component';
import { InfoMessageComponent } from './info-message/info-message.component';
import { InputEmailsComponent } from './input-emails/input-emails.component';
import { LuxonDatePipe } from './luxon-date-pipe';
import { MenuContentComponent, MenuContentFooter } from './menu-content/menu-content.component';
import { MenuGroupComponent } from './menu-group/menu-group.component';
import { MenuItemExternalComponent } from './menu-item-external/menu-item-external.component';
import { MenuItemComponent } from './menu-item/menu-item.component';
import { MenuSubItemExternalComponent } from './menu-subitem-external/menu-subitem-external.component';
import { MenuSubItemComponent } from './menu-subitem/menu-subitem.component';
import { NotFoundErrorComponent } from './not-found-error/not-found-error.component';
import { PalabraClaveComponent } from './palabra-clave/palabra-clave.component';
import { ProblemPanelComponent } from './problem-panel/problem-panel.component';
import { RootComponent } from './root/root.component';
import { SelectBooleanComponent } from './select-boolean/select-boolean.component';
import { SelectEmailRecipientsComponent } from './select-email-recipients/select-email-recipients.component';
import { SelectEntityComponent } from './select-entity/select-entity.component';
import { SelectEnumComponent } from './select-enum/select-enum.component';
import { SelectMultipleComponent } from './select-multiple/select-multiple.component';
import { SelectComponent } from './select/select.component';

@NgModule({
  declarations: [
    ActionComponent,
    ActionDialogComponent,
    ActionEmptyFooterComponent,
    ActionFooterButtonComponent,
    ActionFooterComponent,
    ActionFooterMessageComponent,
    ActionFragmentLinkItemComponent,
    ActionFragmentMenuGroupComponent,
    ActionFragmentMenuItemComponent,
    BreadcrumbComponent,
    DialogContentComponent,
    DialogFooterComponent,
    DialogHeaderComponent,
    ErrorMessageComponent,
    ExportDialogComponent,
    FieldInfoComponent,
    FooterCrearComponent,
    FormAutocompleteOffDirective,
    FragmentComponent,
    FragmentContentComponent,
    FragmentTitleComponent,
    IconChangesComponent,
    IconErrorsComponent,
    IconProblemsComponent,
    InfoMessageComponent,
    InputEmailsComponent,
    LuxonDatePipe,
    MenuContentComponent,
    MenuContentFooter,
    MenuGroupComponent,
    MenuItemComponent,
    MenuItemExternalComponent,
    MenuSubItemComponent,
    MenuSubItemExternalComponent,
    NotFoundErrorComponent,
    NotFoundErrorDirective,
    PalabraClaveComponent,
    ProblemPanelComponent,
    RootComponent,
    SelectBooleanComponent,
    SelectComponent,
    SelectEmailRecipientsComponent,
    SelectEntityComponent,
    SelectEnumComponent,
    SelectMultipleComponent,
    SgiFileUploadComponent,
    SgiInputWithThousandSeparator,
    SgiTooltipDirective
  ],
  imports: [
    CommonModule,
    RouterModule,
    MaterialDesignModule,
    TranslateModule,
    FormsModule,
    ReactiveFormsModule,
    FormlyFormsModule,
  ],
  exports: [
    ActionComponent,
    ActionDialogComponent,
    ActionEmptyFooterComponent,
    ActionFooterButtonComponent,
    ActionFooterComponent,
    ActionFragmentLinkItemComponent,
    ActionFragmentMenuGroupComponent,
    ActionFragmentMenuItemComponent,
    BreadcrumbComponent,
    DialogContentComponent,
    DialogHeaderComponent,
    ErrorMessageComponent,
    ExportDialogComponent,
    FieldInfoComponent,
    FooterCrearComponent,
    FormAutocompleteOffDirective,
    FragmentComponent,
    IconChangesComponent,
    IconErrorsComponent,
    IconProblemsComponent,
    InfoMessageComponent,
    InputEmailsComponent,
    LuxonDatePipe,
    MenuContentComponent,
    MenuContentFooter,
    MenuGroupComponent,
    MenuItemComponent,
    MenuItemExternalComponent,
    MenuSubItemComponent,
    MenuSubItemExternalComponent,
    NotFoundErrorComponent,
    NotFoundErrorDirective,
    PalabraClaveComponent,
    ProblemPanelComponent,
    RootComponent,
    SelectBooleanComponent,
    SelectComponent,
    SelectEmailRecipientsComponent,
    SelectEntityComponent,
    SelectEnumComponent,
    SelectMultipleComponent,
    SgiFileUploadComponent,
    SgiInputWithThousandSeparator,
    SgiTooltipDirective
  ]
})
export class SharedModule { }
