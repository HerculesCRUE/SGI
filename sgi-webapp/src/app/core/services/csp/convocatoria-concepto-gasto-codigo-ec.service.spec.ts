import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ConvocatoriaConceptoGastoCodigoEcService } from './convocatoria-concepto-gasto-codigo-ec.service';
import { LoggerTestingModule } from 'ngx-logger/testing';

describe('ConvocatoriaConceptoGastoCodigoEcService', () => {
  let service: ConvocatoriaConceptoGastoCodigoEcService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConvocatoriaConceptoGastoCodigoEcService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});