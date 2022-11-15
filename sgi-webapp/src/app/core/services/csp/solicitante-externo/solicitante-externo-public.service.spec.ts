import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitanteExternoPublicService } from './solicitante-externo-public.service';

describe('SolicitanteExternoPublicService', () => {
  let service: SolicitanteExternoPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitanteExternoPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
