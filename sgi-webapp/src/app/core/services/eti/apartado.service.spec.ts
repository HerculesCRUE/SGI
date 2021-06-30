import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ApartadoService } from './apartado.service';

describe('ApartadoService', () => {
  let service: ApartadoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        LoggerTestingModule,
        SgiAuthModule
      ],
      providers: [
        SgiAuthService
      ],
    });
    service = TestBed.inject(ApartadoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
