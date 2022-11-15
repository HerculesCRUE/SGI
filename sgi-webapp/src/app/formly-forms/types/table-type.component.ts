import { Component, OnDestroy, OnInit, Pipe, PipeTransform } from '@angular/core';
import { FieldArrayType } from '@ngx-formly/core';
import { DateTime } from 'luxon';
import { Subject } from 'rxjs';

@Component({
  selector: 'sgi-table-type',
  template: `<div class="table-itens">
      <div class="table-responsive">
          <table id="gritens" class="table table-bordered table-hover table-editable"
          [ngClass]="field?.templateOptions?.theme?.table?.class"
          [ngStyle]="field?.templateOptions?.theme?.table?.style">

              <!-- HEAD -->
              <thead
                [ngClass]="field?.templateOptions?.theme?.thead?.general?.class"
                [ngStyle]="field?.templateOptions?.theme?.thead?.general?.style">

                  <!-- TR -->
                  <tr id="itens_header" class="nowrap"
                    [ngClass]="field?.templateOptions?.theme?.thead?.tr?.class"
                    [ngStyle]="field?.templateOptions?.theme?.thead?.tr?.style">

                    <!-- TH -->
                    <ng-container *ngFor="let fieldItemHead of field?.fieldArray?.fieldGroup;let last = last;">
                      <ng-container *ngIf="!fieldItemHead.hide">
                        <th class="text-center small-width" 
                          [ngClass]="field?.templateOptions?.theme?.thead?.th?.class"
                          [ngStyle]="field?.templateOptions?.theme?.thead?.th?.style">
                            <!-- LABEL (innerHTML) -->
                            <div class="text-truncate" [ngClass]="fieldItemHead?.templateOptions?.tableFieldOptions?.className"
                              [ngStyle]="fieldItemHead?.templateOptions?.tableFieldOptions?.theme">
                              <span [innerHTML]="fieldItemHead?.templateOptions?.label"></span>
                            </div>
                            <!-- // LABEL (innerHTML) -->
                        </th>
                      </ng-container>
                    </ng-container>  
                    <!-- // TH -->

                  </tr>
                  <!-- // TR -->

              </thead>
              <!-- // HEAD -->

              <!-- BODY -->
              <tbody
                [ngClass]="field?.templateOptions?.theme?.tbody?.general?.class"
                [ngStyle]="field?.templateOptions?.theme?.tbody?.general?.style">

                <!-- TR -->
                <ng-container *ngFor="let fieldItemBody of field.fieldGroup;">
                  <!-- *ngIf="!fieldItemBody.hide" -->
                  <ng-container >

                    <tr id="itens" class="editing hide-from-view"
                      [ngClass]="field?.templateOptions?.theme?.tbody?.tr?.class"
                      [ngStyle]="field?.templateOptions?.theme?.tbody?.tr?.style">

                      <!-- TD -->
                      <ng-container *ngFor="let fieldItemBodyItem of fieldItemBody.fieldGroup;let last = last;">
                        <ng-container *ngIf="!(fieldItemBodyItem.hide && last) && !_hideColumnsBody(fieldItemBodyItem.key)">
                          <td class="nowrap nro-itens"
                            [ngClass]="field?.templateOptions?.theme?.tbody?.td?.class"
                            [ngStyle]="field?.templateOptions?.theme?.tbody?.td?.style">

                            <!-- FORMLY -->
                            <div class="text-truncate" [ngClass]="fieldItemBodyItem?.templateOptions?.tableFieldOptions?.className"   [ngStyle]="fieldItemBodyItem?.templateOptions?.tableFieldOptions?.theme">
                              <formly-field [field]="fieldItemBodyItem" style="width: 100%;"></formly-field>
                            </div>
                            <!-- // FORMLY -->

                          </td>
                        </ng-container>
                      </ng-container>
                      <!-- // TD -->

                    </tr>
                    <!-- // TR -->

                  </ng-container>
                </ng-container>

              </tbody>

          </table>

      </div>
    </div>
  <!--   <p>Model:</p>
    {{model | json}}-->
  `,
  styles: [`
    text-rendering: optimizeLegibility;
    .table-responsive {
        width: 100%;
        overflow: auto;
    }
    .table {
      box-shadow: 0px 5px 5px -3px rgba(0, 0, 0, 0.2), 0px 8px 10px 1px rgba(0, 0, 0, 0.14), 0px 3px 14px 2px rgba(0, 0, 0, 0.12);
        color: #333;
        font-size: 15px;
        font-weight: 300;
        border-collapse: collapse;
        border-spacing: 0;
    }
    .table thead{
      background:#f3f6f9;
      font-weight: bold;
    }
    .table thead .text-truncate{
      font-weight: bold;
    }
    /*.table-bordered, .table-bordered>tbody>tr, .table-bordered>tbody>tr>td, .table-bordered>tbody>tr>th, .table-bordered>tfoot>tr>td, .table-bordered>tfoot>tr>th, .table-bordered>thead>tr>td, .table-bordered>thead>tr>th {
        border: 1px solid #ebebeb;
    }*/
    .nowrap {
        white-space: nowrap;
    }
    .table>caption+thead>tr:first-child>td, .table>caption+thead>tr:first-child>th, .table>colgroup+thead>tr:first-child>td, .table>colgroup+thead>tr:first-child>th, .table>thead:first-child>tr:first-child>td, .table>thead:first-child>tr:first-child>th {
        border-top: 0;
    }
    .panel-item .table>thead>tr>th, .panel-item .table th {
        font-size: 14px;
        font-weight: 300;
    }
    .table>thead>tr>th {
        text-align: left;
        border-bottom: 1px solid #ebebeb;
    }
    .table>thead>tr>th, .table th {
        color: #616161;
        font-weight: 400;
        padding: 8px !important;
        // height: 38px;
    }
    .table>tbody>tr>td, .table>tbody>tr>th, .table>tfoot>tr>td, .table>tfoot>tr>th, .table>thead>tr>td, .table>thead>tr>th {
        padding: 18px 8px;
        line-height: 1.42857;
        vertical-align: top;
        border-top: 1px solid #ebebeb;
    }

    .table td, .table th {
        border: none;
        // height: 59px;
    }
    thead > tr > th > span {
      // font-weight: 600;
    }
    .btn-link {
        color: #0050dc;
        font-weight: 500;
        text-decoration: none;
        background-color: transparent;
        border: none;
        margin-top: 15px;
        cursor: pointer;
    }
    .w-10 {
      max-width: 10px !important;
    }
    .text-truncate {
        display: block;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .text-truncate.horizontal-scroll {
      overflow-y: scroll !important;
    }

    .text-truncate.vertical-scroll {
      overflow-x: scroll !important;
    }

  `]
})
export class TableType extends FieldArrayType implements OnInit, OnDestroy {
  trigger = DateTime.now().toMillis();

  private _unsubscribeAll$: Subject<any>;

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  /**
   * On init
   */
  ngOnInit(): void {
    this._assignDefaultValues();
    this._createsFirstPosition();
    this._hideColumnsHeader();

    this.formControl
      .valueChanges
      // .pipe(
      //   takeUntil(this._unsubscribeAll$)
      // )
      .subscribe(el => {
        this._hideColumnsHeader();
      });
  }

  /**
   * On destroy
   */
  ngOnDestroy(): void {
    // Unsubscribe from all subscriptions
    this._unsubscribeAll$.next();
    this._unsubscribeAll$.complete();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------
  addNewLine(): void {
    this.add(null, { _index: (this.field.fieldGroup.length + 1), _focused: {} });
    this._removeLabelFields();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------
  private _createsFirstPosition(): void {
    if (
      !this.field.fieldGroup ||
      (Array.isArray(this.field.fieldGroup) && !this.field.fieldGroup.length)
    ) {
      this.add(null, { _index: (this.field.fieldGroup.length + 1), _focused: {} });
      this._removeLabelFields();
    }
  }

  private _assignDefaultValues(): void {
    // removeLabelFields
    if (this.field.templateOptions.removeLabelFields && this.field.templateOptions.removeLabelFields !== false) {
      this.field.templateOptions.removeLabelFields = true;
      this._removeLabelFields();
    } else {
      this.field.templateOptions.removeLabelFields = false;
      this._removeLabelFields();
    }
  }

  private _removeLabelFields(): void {
    if (
      this.field.fieldGroup &&
      Array.isArray(this.field.fieldGroup) &&
      this.field.fieldGroup.length
    ) {
      // Remove
      if (this.field.templateOptions.removeLabelFields) {
        this.field.fieldGroup.forEach(el => {
          el.fieldGroup.forEach(elFieldGroup => {
            elFieldGroup.templateOptions.label = null;
          });
          return el;
        });
      }
    }
  }

  private _hideColumnsHeader(): void {
    if (
      this.field.fieldArray &&
      this.field.fieldArray.fieldGroup &&
      Array.isArray(this.field.fieldArray.fieldGroup)
    ) {
      // adds +1, when the hideExpression field result is true.
      const countControlTrue = {};
      // total lines (formgroups)
      const controlCountsNumberLines = this.field.fieldGroup ? this.field.fieldGroup.length : 1;
      // map in the available fields per line
      this.field.fieldArray.fieldGroup.forEach((fgroup, index) => {
        // creates the key to use as a counting control
        countControlTrue[fgroup.key.toString()] = 0;
        // map on lines
        this.field.fieldGroup.forEach((fGroupItem, indexGroupItem) => {
          // get temporary model based on the line to be analyzed
          const _tempModel = fGroupItem.formControl.value;
          // map in the fields
          fGroupItem.fieldGroup.forEach((fGroupItemItem, indexGroupItemItem) => {
            // check if the column key field is the same as the field key
            if (fgroup.key === fGroupItemItem.key) {
              // checks if the hideExpression field exists and is a function
              if (fGroupItemItem.hideExpression && (typeof fGroupItemItem.hideExpression === 'function')) {
                // if the result of the expression is true, it counts +1
                if (fGroupItemItem.hideExpression(_tempModel, this.formState, this.field)) {
                  countControlTrue[fgroup.key.toString()]++;
                }
              }

              // last record
              // if(( index + 1) >= controlCountsNumberLines) {

              // enter if all checks were true, if all were true
              // the variable countControlTrue is equal to 
              // controlCountsNumberLines
              fgroup['hide'] = countControlTrue[fgroup.key.toString()] >= controlCountsNumberLines;
              // }
            }
          });
        });
      });
    }
  }

  _hideColumnsBody(key: string): boolean {
    let _result = false;

    if (!key) {
      return _result;
    }

    if (this.field.fieldGroup) {
      const _countRows = this.field.fieldGroup.length;
      let _countFieldsTrue = {};
      this.field.fieldGroup.forEach((field, index) => {
        if (field.fieldGroup) {
          field.fieldGroup.forEach(item => {
            if (item.key === key) {
              if (!item['hide']) {
                _result = false;
                return;
              }

              if (!_countFieldsTrue[key]) {
                _countFieldsTrue[key] = 0;
              }

              if (item['hide']) {
                _countFieldsTrue[key]++;
              }

              if (_countFieldsTrue[key] === _countRows && index + 1 === _countRows) {
                _result = true;
              }
            }
          });
        }
      });
    }
    return _result;
  }
}




@Pipe({ name: 'hideColumnsBody' })
export class HideColumnsBodyPipe implements PipeTransform {
  transform(key: string, data: any): boolean {
    let _result = false;

    if (!key) {
      return _result;
    }

    if (data.fieldGroup) {
      const _countRows = data.fieldGroup.length;
      let _countFieldsTrue = {};
      data.fieldGroup.forEach((field, index) => {
        if (field.fieldGroup) {
          field.fieldGroup.forEach(item => {
            if (item.key === key) {
              if (!_countFieldsTrue[key]) {
                _countFieldsTrue[key] = 0;
              }

              if (item['hide']) {
                _countFieldsTrue[key]++;
              }
              if (_countFieldsTrue[key] === _countRows && index + 1 === _countRows) {
                _result = true;
              }
            }
          });
        }
      });
    }

    return _result;
  }
}
