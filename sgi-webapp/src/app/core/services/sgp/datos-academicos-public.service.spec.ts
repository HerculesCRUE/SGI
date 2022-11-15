import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { DatosAcademicosPublicService } from './datos-academicos-public.service';

describe('DatosAcademicosPublicService', () => {
  let service: DatosAcademicosPublicService;

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
    service = TestBed.inject(DatosAcademicosPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
