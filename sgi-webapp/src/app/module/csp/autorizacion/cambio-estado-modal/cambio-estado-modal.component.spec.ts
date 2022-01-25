import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { Estado } from '@core/models/csp/estado-autorizacion';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { AutorizacionCambioEstadoModalComponentData, CambioEstadoModalComponent } from './cambio-estado-modal.component';

describe('CambioEstadoModalComponent', () => {
  let component: CambioEstadoModalComponent;
  let fixture: ComponentFixture<CambioEstadoModalComponent>;

  const newData: AutorizacionCambioEstadoModalComponentData = {
    estadoActual: Estado.BORRADOR,
    autorizacion: undefined
  };

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [CambioEstadoModalComponent],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        LoggerTestingModule,
        ReactiveFormsModule,
        SgiAuthModule,
        SharedModule,
        RouterTestingModule,
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: TestUtils.buildDialogActionMatDialogRef() },
        SgiAuthService,
        { provide: MAT_DIALOG_DATA, useValue: newData },
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CambioEstadoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
