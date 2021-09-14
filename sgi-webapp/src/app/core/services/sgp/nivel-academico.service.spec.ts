import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';

import { NivelAcademicosService } from './nivel-academico.service';

describe('NivelAcademicosService', () => {
  let service: NivelAcademicosService;

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
    service = TestBed.inject(NivelAcademicosService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
