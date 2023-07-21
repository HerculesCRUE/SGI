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
import { InvencionEquipoInventorListadoExportService } from '../../invencion-equipo-inventor-listado-export.service';
import { InvencionGeneralListadoExportService } from '../../invencion-general-listado-export.service';
import { InvencionListadoExportService } from '../../invencion-listado-export.service';
import { InvencionSolicitudesProteccionListadoExportService } from '../../invencion-solicitudes-proteccion-listado-export.service';
import { InvencionListadoExportModalComponent } from './invencion-listado-export-modal.component';

describe('InvencionListadoExportModalComponent', () => {
  let component: InvencionListadoExportModalComponent;
  let fixture: ComponentFixture<InvencionListadoExportModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        InvencionListadoExportModalComponent
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
        InvencionListadoExportService,
        InvencionGeneralListadoExportService,
        InvencionSolicitudesProteccionListadoExportService,
        InvencionEquipoInventorListadoExportService,
        LuxonDatePipe
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InvencionListadoExportModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
