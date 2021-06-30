import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ConvocatoriaConceptoGastoService } from './convocatoria-concepto-gasto.service';
import { LoggerTestingModule } from 'ngx-logger/testing';

describe('ConvocatoriaConceptoGastoService', () => {
  let service: ConvocatoriaConceptoGastoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConvocatoriaConceptoGastoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});