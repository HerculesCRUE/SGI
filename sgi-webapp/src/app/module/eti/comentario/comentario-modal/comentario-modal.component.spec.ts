import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IComite } from '@core/models/eti/comite';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IFormulario } from '@core/models/eti/formulario';
import { IMemoria } from '@core/models/eti/memoria';
import { TipoEvaluacion } from '@core/models/eti/tipo-evaluacion';
import { FormularioService } from '@core/services/eti/formulario.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EtiSharedModule } from '../../shared/eti-shared.module';
import { ComentarioModalComponent } from './comentario-modal.component';

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
        formulario: {
          id: 1
        } as IFormulario
      } as IMemoria,
      evaluaciones: [{
        tipoEvaluacion: {
          id: 2
        } as TipoEvaluacion,
        memoria: {
          formulario: {
            id: 1
          } as IFormulario
        } as IMemoria
      }] as IEvaluacion[]
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
        SgiAuthModule,
        SharedModule,
        EtiSharedModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: TestUtils.buildDialogCommonMatDialogRef() },
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
