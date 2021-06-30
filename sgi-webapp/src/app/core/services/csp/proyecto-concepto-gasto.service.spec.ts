import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ProyectoConceptoGastoService } from './proyecto-concepto-gasto.service';

describe('ProyectoConceptoGastoService', () => {
  let service: ProyectoConceptoGastoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoConceptoGastoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
