import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ProyectoConceptoGastoCodigoEcService } from './proyecto-concepto-gasto-codigo-ec.service';
import { LoggerTestingModule } from 'ngx-logger/testing';

describe('ProyectoConceptoGastoCodigoEcService', () => {
  let service: ProyectoConceptoGastoCodigoEcService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoConceptoGastoCodigoEcService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
