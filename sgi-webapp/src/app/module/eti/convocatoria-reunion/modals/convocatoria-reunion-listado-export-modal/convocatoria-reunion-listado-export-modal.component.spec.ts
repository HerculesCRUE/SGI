import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaReunionGeneralListadoExportService } from '../../convocatoria-reunion-general-listado-export.service';
import { ConvocatoriaReunionListadoExportService } from '../../convocatoria-reunion-listado-export.service';
import { ConvocatoriaReunionMemoriasListadoExportService } from '../../convocatoria-reunion-memorias-listado-export.service';
import { ConvocatoriaReunionListadoExportModalComponent } from './convocatoria-reunion-listado-export-modal.component';
describe('ConvocatoriaReunionListadoExportModalComponent', () => {
  let component: ConvocatoriaReunionListadoExportModalComponent;
  let fixture: ComponentFixture<ConvocatoriaReunionListadoExportModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaReunionListadoExportModalComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TranslateModule,
        TestUtils.getIdiomas(),
        FlexLayoutModule,
        ReactiveFormsModule,
        LoggerTestingModule,
        SharedModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: TestUtils.buildDialogActionMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: {} as IBaseExportModalData },
        SgiAuthService,
        ConvocatoriaReunionListadoExportService,
        ConvocatoriaReunionGeneralListadoExportService,
        ConvocatoriaReunionMemoriasListadoExportService,
        LuxonDatePipe
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaReunionListadoExportModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
