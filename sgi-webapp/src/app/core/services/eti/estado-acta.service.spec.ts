import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';

import { EstadoActaService } from './estado-acta.service';

describe('EstadoActaService', () => {
  let service: EstadoActaService;

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
    service = TestBed.inject(EstadoActaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
