import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { IProcedimiento } from '@core/models/pii/procedimiento';
import { StatusWrapper } from '@core/utils/status-wrapper';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { TranslateModule } from '@ngx-translate/core';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { PiiSharedModule } from '../../../shared/pii-shared.module';
import { ISolicitudProteccionProcedimientoModalData, SolicitudProteccionProcedimientoModalComponent } from './solicitud-proteccion-procedimiento-modal.component';

describe('SolicitudProteccionProcedimientoModalComponent', () => {
  let component: SolicitudProteccionProcedimientoModalComponent;
  let fixture: ComponentFixture<SolicitudProteccionProcedimientoModalComponent>;

  const data = {
    procedimiento: { value: {} as IProcedimiento } as StatusWrapper<IProcedimiento>,
    tiposProcedimiento: []
  } as ISolicitudProteccionProcedimientoModalData;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SolicitudProteccionProcedimientoModalComponent],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        SharedModule,
        TranslateModule,
        SgiAuthModule,
        PiiSharedModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: data },
        SgiAuthService
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudProteccionProcedimientoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
