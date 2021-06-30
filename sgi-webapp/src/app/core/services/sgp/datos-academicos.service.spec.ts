import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';

import { DatosAcademicosService } from './datos-academicos.service';

describe('DatosAcademicosService', () => {
  let service: DatosAcademicosService;

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
    service = TestBed.inject(DatosAcademicosService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
