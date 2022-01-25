import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { DialogComponent } from '@block/dialog/dialog.component';
import { HeaderComponent } from '@block/header/header.component';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CspSharedModule } from '../../../shared/csp-shared.module';
import { ISolicitudCrearProyectoModalData, SolicitudCrearProyectoModalComponent } from './solicitud-crear-proyecto-modal.component';

describe('SolicitudCrearProyectoModalComponent', () => {
  let component: SolicitudCrearProyectoModalComponent;
  let fixture: ComponentFixture<SolicitudCrearProyectoModalComponent>;

  const data: ISolicitudCrearProyectoModalData = {
    solicitud: {
      id: 3,
      convocatoriaId: 1
    }
  } as ISolicitudCrearProyectoModalData;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SolicitudCrearProyectoModalComponent,
        DialogComponent,
        HeaderComponent
      ],
      imports: [
        SharedModule,
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        ReactiveFormsModule,
        CspSharedModule,
        SgiAuthModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: data },
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudCrearProyectoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
