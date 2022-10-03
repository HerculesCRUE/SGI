import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaConceptoGastoPublicService } from './convocatoria-concepto-gasto-public.service';

describe('ConvocatoriaConceptoGastoPublicService', () => {
  let service: ConvocatoriaConceptoGastoPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConvocatoriaConceptoGastoPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});