import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ComunidadAutonomaPublicService } from './comunidad-autonoma-public.service';

describe('ComunidadAutonomaPublicService', () => {
  let service: ComunidadAutonomaPublicService;

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
    service = TestBed.inject(ComunidadAutonomaPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
