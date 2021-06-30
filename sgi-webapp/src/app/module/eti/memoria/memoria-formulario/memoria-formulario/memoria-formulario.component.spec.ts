import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { MemoriaActionService } from '../../memoria.action.service';
import { MemoriaFormularioComponent } from './memoria-formulario.component';
import { IComite } from '@core/models/eti/comite';
import { TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { IRetrospectiva } from '@core/models/eti/retrospectiva';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { IMemoria } from '@core/models/eti/memoria';
import { ActivatedRoute } from '@angular/router';

describe('MemoriaFormularioComponent', () => {
  let component: MemoriaFormularioComponent;
  let fixture: ComponentFixture<MemoriaFormularioComponent>;

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
      } as IPeticionEvaluacion,
    } as IMemoria
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [MemoriaFormularioComponent],
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
        SgiAuthModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { data: snapshotData } } },
        MemoriaActionService,
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MemoriaFormularioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
