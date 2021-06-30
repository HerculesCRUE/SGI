import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { ActionFooterButtonComponent } from './action-footer-button/action-footer-button.component';
import { ActionFooterComponent } from './action-footer/action-footer.component';
import { ActionFragmentLinkItemComponent } from './action-fragment-link-item/action-fragment-link-item.component';
import { ActionFragmentMenuGroupComponent } from './action-fragment-menu-group/action-fragment-menu-group.component';
import { ActionFragmentMenuItemComponent } from './action-fragment-menu-item/action-fragment-menu-item.component';
import { ActionComponent } from './action/action.component';
import { BreadcrumbComponent } from './breadcrumb/breadcrumb.component';
import { FieldInfoComponent } from './field-info/field-info.component';
import { SgiFileUploadComponent } from './file-upload/file-upload.component';
import { FooterCrearComponent } from './footers/footer-crear/footer-crear.component';
import { LuxonDatePipe } from './luxon-date-pipe';
import { RootComponent } from './root/root.component';
import { SelectBooleanComponent } from './select-boolean/select-boolean.component';
import { SearchEmpresaModalComponent } from './select-empresa/dialog/search-empresa.component';
import { SelectEmpresaComponent } from './select-empresa/select-empresa.component';
import { SelectEntityComponent } from './select-entity/select-entity.component';
import { SelectEnumComponent } from './select-enum/select-enum.component';
import { SearchPersonaModalComponent } from './select-persona/dialog/search-persona.component';
import { SelectPersonaComponent } from './select-persona/select-persona.component';
import { SelectComponent } from './select/select.component';

@NgModule({
  declarations: [
    BreadcrumbComponent,
    FooterCrearComponent,
    ActionFooterComponent,
    ActionFooterButtonComponent,
    RootComponent,
    SearchPersonaModalComponent,
    ActionFragmentMenuItemComponent,
    ActionFragmentLinkItemComponent,
    SelectPersonaComponent,
    SelectEmpresaComponent,
    SearchEmpresaModalComponent,
    SgiFileUploadComponent,
    ActionFragmentMenuGroupComponent,
    LuxonDatePipe,
    SelectBooleanComponent,
    SelectComponent,
    SelectEntityComponent,
    SelectEnumComponent,
    FieldInfoComponent,
    ActionComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    MaterialDesignModule,
    TranslateModule,
    FormsModule,
    ReactiveFormsModule
  ],
  exports: [
    BreadcrumbComponent,
    FooterCrearComponent,
    ActionFooterComponent,
    ActionFooterButtonComponent,
    RootComponent,
    ActionFragmentMenuItemComponent,
    ActionFragmentLinkItemComponent,
    SelectPersonaComponent,
    SelectEmpresaComponent,
    SgiFileUploadComponent,
    ActionFragmentMenuGroupComponent,
    LuxonDatePipe,
    SelectBooleanComponent,
    SelectComponent,
    SelectEntityComponent,
    SelectEnumComponent,
    FieldInfoComponent,
    ActionComponent
  ]
})
export class SharedModule { }
