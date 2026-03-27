import { Component, input } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { PaymentResponse } from '../../../../shared/models/api.models';
import { StatusBadgeComponent } from '../../../../shared/ui/status-badge/status-badge.component';

@Component({
  selector: 'app-payment-status',
  imports: [DatePipe, DecimalPipe, StatusBadgeComponent],
  templateUrl: './payment-status.component.html',
  styleUrl: './payment-status.component.css',
})
export class PaymentStatusComponent {
  readonly payment = input<PaymentResponse | null>(null);
  readonly loading = input(false);
}
