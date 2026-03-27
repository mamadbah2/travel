import { Component, computed, input } from '@angular/core';

const STATUS_COLORS: Record<string, string> = {
  // Travel statuses
  DRAFT: 'bg-[#dbd3d8] text-[#223843]',
  PUBLISHED: 'bg-emerald-100 text-emerald-800',
  CANCELLED: 'bg-red-100 text-red-800',
  COMPLETED: 'bg-[#223843] text-white',
  // Subscription statuses
  PENDING_PAYMENT: 'bg-amber-100 text-amber-800',
  CONFIRMED: 'bg-emerald-100 text-emerald-800',
  // Payment statuses
  PENDING: 'bg-amber-100 text-amber-800',
  SUCCESS: 'bg-emerald-100 text-emerald-800',
  FAILED: 'bg-red-100 text-red-800',
  // Notification statuses
  SENT: 'bg-emerald-100 text-emerald-800',
  // User statuses
  ACTIVE: 'bg-emerald-100 text-emerald-800',
  BANNED: 'bg-red-100 text-red-800',
  PENDING_VERIFICATION: 'bg-amber-100 text-amber-800',
  // Report statuses
  OPEN: 'bg-amber-100 text-amber-800',
  REVIEWED: 'bg-emerald-100 text-emerald-800',
  DISMISSED: 'bg-gray-100 text-gray-600',
};

@Component({
  selector: 'app-status-badge',
  templateUrl: './status-badge.component.html',
  styleUrl: './status-badge.component.css',
})
export class StatusBadgeComponent {
  readonly status = input.required<string>();

  readonly label = computed(() => this.status().replace(/_/g, ' '));
  readonly classes = computed(() => STATUS_COLORS[this.status()] ?? 'bg-gray-100 text-gray-800');
}
