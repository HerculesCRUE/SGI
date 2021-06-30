import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MemoriaInformesComponent } from './memoria-informes.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialDesignModule } from '@material/material-design.module';
import TestUtils from '@core/utils/test-utils';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MemoriaActionService } from '../../memoria.action.service';
import { IComite } from '@core/models/eti/comite';
import { TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { IRetrospectiva } from '@core/models/eti/retrospectiva';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { IMemoria } from '@core/models/eti/memoria';
import { ActivatedRoute } from '@angular/router';
import { SnackBarService } from '@core/services/snack-bar.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { RouterTestingModule } from '@angular/router/testing';

describe('MemoriaInformesComponent', () => {
  let component: MemoriaInformesComponent;
  let fixture: ComponentFixture<MemoriaInformesComponent>;

  const snapshotData = {
    memoria: {
      id: 1,
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
      declarations: [MemoriaInformesComponent],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        RouterTestingModule,
        TestUtils.getIdiomas(),
        FormsModule,
        ReactiveFormsModule,
      ],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} },
        { provide: ActivatedRoute, useValue: { snapshot: { data: snapshotData } } },
        MemoriaActionService,
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MemoriaInformesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
