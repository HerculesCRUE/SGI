import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EmpresaExplotacionResultadosService } from './empresa-explotacion-resultados.service';

describe('EmpresaExplotacionResultadosService', () => {
  let service: EmpresaExplotacionResultadosService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(EmpresaExplotacionResultadosService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
