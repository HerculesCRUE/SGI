import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';

import { DatosPersonalesService } from './datos-personales.service';

describe('DatosPersonalesService', () => {
  let service: DatosPersonalesService;

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
    service = TestBed.inject(DatosPersonalesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
