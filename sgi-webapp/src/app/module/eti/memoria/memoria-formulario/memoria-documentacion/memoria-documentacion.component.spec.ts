import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { IComite } from '@core/models/eti/comite';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { IFormulario } from '@core/models/eti/formulario';
import { IMemoria } from '@core/models/eti/memoria';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { IRetrospectiva } from '@core/models/eti/retrospectiva';
import { ITipoDocumento } from '@core/models/eti/tipo-documento';
import { TipoEstadoMemoria } from '@core/models/eti/tipo-estado-memoria';
import { ITipoMemoria } from '@core/models/eti/tipo-memoria';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { MemoriaActionService } from '../../memoria.action.service';
import { MemoriaDocumentacionComponent } from './memoria-documentacion.component';

describe('MemoriaDocumentacionComponent', () => {
  let component: MemoriaDocumentacionComponent;
  let fixture: ComponentFixture<MemoriaDocumentacionComponent>;

  const snapshotData = {
    id: 1,
    memoria: {
      tipoMemoria: {
        id: 1
      } as ITipoMemoria,
      id: 1,
      comite: {
        formulario: {
          id: 1,
        } as IFormulario,
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
    } as IMemoria,
    documentacionMemoria: {
      tipoDocumento: {
        id: 1
      } as ITipoDocumento,
      memoria: {
        tipoMemoria: {
          id: 1
        } as ITipoMemoria,
        id: 1,
        comite: {
          formulario: {
            id: 1,
          } as IFormulario,
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
      } as IMemoria,
      id: 1
    } as IDocumentacionMemoria
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [MemoriaDocumentacionComponent],
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
    fixture = TestBed.createComponent(MemoriaDocumentacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
