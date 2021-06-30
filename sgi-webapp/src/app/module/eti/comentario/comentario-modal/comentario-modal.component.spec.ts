import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ComentarioModalComponent } from './comentario-modal.component';
import { IComite } from '@core/models/eti/comite';
import { TipoEvaluacion } from '@core/models/eti/tipo-evaluacion';
import { ActivatedRoute } from '@angular/router';
import { FormularioService } from '@core/services/eti/formulario.service';

describe('ComentarioModalComponent', () => {
  let component: ComentarioModalComponent;
  let fixture: ComponentFixture<ComentarioModalComponent>;

  beforeEach(waitForAsync(() => {

    const snapshotData = {
      tipoEvaluacion: {
        id: 1
      } as TipoEvaluacion,
      memoria: {
        id: 1,
        comite: {
          id: 1
        } as IComite,
      }
    };

    TestBed.configureTestingModule({
      declarations: [ComentarioModalComponent],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        SgiAuthModule
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: snapshotData },
        { provide: MAT_DIALOG_DATA, useValue: snapshotData },
        { provide: ActivatedRoute, useValue: { snapshot: { data: snapshotData } } },
        FormularioService,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ComentarioModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
