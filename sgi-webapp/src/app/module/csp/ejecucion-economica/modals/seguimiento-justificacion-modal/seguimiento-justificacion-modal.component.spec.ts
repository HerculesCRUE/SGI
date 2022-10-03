import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { StatusWrapper } from '@core/utils/status-wrapper';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ISeguimientoJustificacionModalData, SeguimientoJustificacionModalComponent } from './seguimiento-justificacion-modal.component';

describe('SeguimientoJustificacionModalComponent', () => {
  let component: SeguimientoJustificacionModalComponent;
  let fixture: ComponentFixture<SeguimientoJustificacionModalComponent>;

  const newData: ISeguimientoJustificacionModalData = {
    proyecto: {},
    seguimientoJustificacion: new StatusWrapper({})
  } as ISeguimientoJustificacionModalData;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SeguimientoJustificacionModalComponent
      ],
      imports: [
        MatDialogModule,
        FormsModule,
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule,
        SgiAuthModule,
        SharedModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
        { provide: MAT_DIALOG_DATA, useValue: newData },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SeguimientoJustificacionModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
