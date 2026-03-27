import { Component, inject } from '@angular/core';
import { SubscriptionStore } from '../../data-access/subscription.store';
import { SubscriptionCardComponent } from '../../ui/subscription-card/subscription-card.component';
import { PaginationComponent } from '../../../../shared/ui/pagination/pagination.component';

@Component({
  selector: 'app-subscription-list-page',
  imports: [SubscriptionCardComponent, PaginationComponent],
  templateUrl: './subscription-list-page.component.html',
  styleUrl: './subscription-list-page.component.css',
})
export class SubscriptionListPageComponent {
  readonly store = inject(SubscriptionStore);

  constructor() {
    this.store.loadSubscriptions(0);
  }
}
