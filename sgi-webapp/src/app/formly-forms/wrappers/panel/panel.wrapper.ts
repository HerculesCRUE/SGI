import { Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { Group } from '@core/services/action-service';
import { SgiFormlyTemplateOptions, SgiFormlyFieldConfig } from '@formly-forms/formly-field-config';
import { FieldWrapper } from '@ngx-formly/core';

@Component({
  selector: 'sgi-formly-wrapper-panel',
  templateUrl: './panel.wrapper.html',
  styleUrls: ['./panel.wrapper.scss']
})
export class PanelWrapperComponent extends FieldWrapper implements OnInit {
  comments = false;
  locked = false;
  changes = false;
  errors = false;
  modified = false;
  private group: Group;

  readonly to: SgiFormlyTemplateOptions;

  @ViewChild(MatExpansionPanel, { static: true }) private expansionPanel: MatExpansionPanel;

  constructor() {
    super();
  }

  ngOnInit() {
    this.group = (this.field as SgiFormlyFieldConfig).group;
    if (this.group && !this.group.initialized) {
      this.group.load(this.formControl as FormGroup);
      this.group.patch(this.formControl.value);
      this.group.initialize();
    }

    if (this.group) {
      this.group.status$.subscribe((status) => {
        this.changes = status.changes;
        this.errors = status.errors;
        if (this.errors && !this.expansionPanel.expanded) {
          this.expansionPanel.open();
        }
      });
    }
    this.locked = Boolean(this.to.locked);
    this.comments = Boolean(this.to.comentario);
    this.modified = Boolean(this.to.modified);
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
