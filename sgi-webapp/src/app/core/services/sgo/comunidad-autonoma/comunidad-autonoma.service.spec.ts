import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ComunidadAutonomaService } from './comunidad-autonoma.service';

describe('ComunidadAutonomaService', () => {
  let service: ComunidadAutonomaService;

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
    service = TestBed.inject(ComunidadAutonomaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
