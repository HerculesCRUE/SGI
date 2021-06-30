import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SnackBarService } from '@core/services/snack-bar.service';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { MemoriaEvaluacionesComponent } from './memoria-evaluaciones.component';
import { LoggerTestingModule } from 'ngx-logger/testing';
import TestUtils from '@core/utils/test-utils';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MemoriaActionService } from '../../memoria.action.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IMemoria } from '@core/models/eti/memoria';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { IComite } from '@core/models/eti/comite';
import { TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { IRetrospectiva } from '@core/models/eti/retrospectiva';


describe('MemoriaEvaluacionesComponent', () => {
  let component: MemoriaEvaluacionesComponent;
  let fixture: ComponentFixture<MemoriaEvaluacionesComponent>;

  const snapshotData = {
    memoria: {
      comite: {
        id: 1
      } as IComite,
      estadoActual: {
        id: 1
      } as TipoEstadoMemoria,
      retrospectiva: {
        id: 1,
        estadoRetrospectiva: {
          id: 1
        }
      } as IRetrospectiva,
      peticionEvaluacion: {
        id: 1
      } as IPeticionEvaluacion
    } as IMemoria
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        MemoriaEvaluacionesComponent
      ],
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
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} },
        { provide: ActivatedRoute, useValue: { snapshot: { data: snapshotData } } },
        MemoriaActionService,
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MemoriaEvaluacionesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
