import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CvnFieldPipe } from './cvn-field.pipe';
import { CvnBooleanValuePipe } from './cvn-boolean-value.pipe';
import { CvnEnumValuePipe } from './cvn-enum-value.pipe';
import { CvnDateValuePipe } from './cvn-date-value.pipe';
import { CvnNumberValuePipe } from './cvn-number-value.pipe';
import { CvnTextValuePipe } from './cvn-text-value.pipe';
import { SharedModule } from '@shared/shared.module';



@NgModule({
  declarations: [
    CvnFieldPipe,
    CvnBooleanValuePipe,
    CvnEnumValuePipe,
    CvnDateValuePipe,
    CvnNumberValuePipe,
    CvnTextValuePipe,
  ],
  imports: [
    CommonModule,
    SharedModule,
  ],
  exports: [
    CvnFieldPipe,
    CvnBooleanValuePipe,
    CvnEnumValuePipe,
    CvnDateValuePipe,
    CvnNumberValuePipe,
    CvnTextValuePipe,
  ]
})
export class CvnPipesModule { }
