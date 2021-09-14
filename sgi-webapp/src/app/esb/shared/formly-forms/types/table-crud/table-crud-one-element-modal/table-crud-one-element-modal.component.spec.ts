import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { TableCRUDOneElementModalComponent, TableCRUDOneElementModalData } from './table-crud-one-element-modal.component';

describe('TableCRUDOneElementModalComponent', () => {
  let component: TableCRUDOneElementModalComponent;
  let fixture: ComponentFixture<TableCRUDOneElementModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        TableCRUDOneElementModalComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,

      ],
      providers: [
        { provide: MatDialogRef, useValue: {} as TableCRUDOneElementModalData },
        { provide: MAT_DIALOG_DATA, useValue: {} as TableCRUDOneElementModalData },

      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TableCRUDOneElementModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
