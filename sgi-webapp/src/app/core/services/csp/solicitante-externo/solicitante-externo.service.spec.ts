import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitanteExternoService } from './solicitante-externo.service';

describe('SolicitanteExternoService', () => {
  let service: SolicitanteExternoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitanteExternoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
