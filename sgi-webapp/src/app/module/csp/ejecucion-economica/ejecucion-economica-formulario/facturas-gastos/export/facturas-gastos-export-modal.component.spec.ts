import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { IDesgloseEconomicoExportData } from '../../desglose-economico.fragment';
import { FacturasGastosExportService } from './facturas-gastos-export.service';
import { FacturasGastosExportModalComponent } from './facturas-gastos-export-modal.component';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';

describe('FacturasGastosExportModalComponent', () => {
  let component: FacturasGastosExportModalComponent;
  let fixture: ComponentFixture<FacturasGastosExportModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        FacturasGastosExportModalComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TranslateModule,
        TestUtils.getIdiomas(),
        FlexLayoutModule,
        FormsModule,
        ReactiveFormsModule,
        LoggerTestingModule,
        SharedModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: TestUtils.buildDialogActionMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: {} as IDesgloseEconomicoExportData },
        FacturasGastosExportService,
        LuxonDatePipe
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FacturasGastosExportModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
