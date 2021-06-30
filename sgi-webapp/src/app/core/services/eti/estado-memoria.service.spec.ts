import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';

import { EstadoMemoriaService } from './estado-memoria.service';

describe('EstadoMemoriaService', () => {
  let service: EstadoMemoriaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        SgiAuthModule
      ],
      providers: [
        SgiAuthService
      ],
    });
    service = TestBed.inject(EstadoMemoriaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
