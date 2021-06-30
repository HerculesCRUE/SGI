import { Component, OnInit } from '@angular/core';
import { SgiFormlyTemplateOptions, SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { FieldWrapper } from '@ngx-formly/core';

@Component({
  selector: 'sgi-formly-title-div-wrapper',
  templateUrl: './title-div.wrapper.html',
  styleUrls: ['./title-div.wrapper.scss']
})
export class TitleDivWrapperComponent extends FieldWrapper implements OnInit {

  comments = false;

  readonly to: SgiFormlyTemplateOptions;

  ngOnInit() {
    this.comments = Boolean(this.to.comentario);
    if (!this.comments && this.field.fieldGroup) {
      this.comments = this.hasComment(this.field.fieldGroup);
    }

  }

  private hasComment(fieldsConfig: SgiFormlyFieldConfig[]): boolean {
    let comment = false;
    fieldsConfig.forEach((field) => {
      if (!comment) {
        comment = Boolean(field.templateOptions.comentario);
        if (!comment && field.fieldGroup) {
          comment = this.hasComment(field.fieldGroup);
        }
      }
    });
    return comment;
  }
}
