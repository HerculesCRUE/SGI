import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { TranslateModule } from '@ngx-translate/core';
import { MaterialDesignModule } from '@material/material-design.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SelectInvencionComponent } from './select-invencion/select-invencion.component';
import { SearchInvencionModalComponent } from './select-invencion/dialog/search-invencion.component';



@NgModule({
  declarations: [SelectInvencionComponent, SearchInvencionModalComponent],
  imports: [
    SharedModule,
    CommonModule,
    TranslateModule,
    MaterialDesignModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  exports: [SelectInvencionComponent]
})
export class PiiSharedModule { }
