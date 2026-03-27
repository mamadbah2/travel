import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { ApiConfig } from '../../../core/config/api.config';
import { PageResponse, PaymentResponse } from '../../../shared/models/api.models';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private readonly http = inject(HttpClient);

  getPaymentBySubscription(subscriptionId: string): Promise<PaymentResponse> {
    return firstValueFrom(
      this.http.get<PaymentResponse>(ApiConfig.payments.bySubscription(subscriptionId)),
    );
  }

  getPaymentsByTraveler(
    travelerId: string,
    page = 0,
    size = 20,
  ): Promise<PageResponse<PaymentResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return firstValueFrom(
      this.http.get<PageResponse<PaymentResponse>>(ApiConfig.payments.byTraveler(travelerId), {
        params,
      }),
    );
  }
}
