import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ClasificacionPublicService } from './clasificacion-public.service';

describe('ClasificacionPublicService', () => {
  let service: ClasificacionPublicService;

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
    service = TestBed.inject(ClasificacionPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
