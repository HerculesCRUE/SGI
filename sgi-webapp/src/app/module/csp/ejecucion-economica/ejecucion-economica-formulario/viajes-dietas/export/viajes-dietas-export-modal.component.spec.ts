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
import { ViajesDietasExportService } from './viajes-dietas-export.service';
import { ViajesDietasExportModalComponent } from './viajes-dietas-export-modal.component';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';

describe('ViajesDietasExportModalComponent', () => {
  let component: ViajesDietasExportModalComponent;
  let fixture: ComponentFixture<ViajesDietasExportModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ViajesDietasExportModalComponent
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
        ViajesDietasExportService,
        LuxonDatePipe
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViajesDietasExportModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
