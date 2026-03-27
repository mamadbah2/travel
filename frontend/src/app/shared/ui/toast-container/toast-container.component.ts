import { Component, inject } from '@angular/core';
import { NotificationService, ToastMessage } from '../../data-access/notification.service';

const TYPE_STYLES: Record<ToastMessage['type'], string> = {
  success: 'bg-emerald-50 border-emerald-400 text-emerald-800',
  error: 'bg-red-50 border-red-400 text-red-800',
  info: 'bg-blue-50 border-blue-400 text-blue-800',
  warning: 'bg-amber-50 border-amber-400 text-amber-800',
};

@Component({
  selector: 'app-toast-container',
  templateUrl: './toast-container.component.html',
  styleUrl: './toast-container.component.css',
  styles: `
    @keyframes slide-in {
      from { transform: translateX(100%); opacity: 0; }
      to { transform: translateX(0); opacity: 1; }
    }
    .animate-slide-in {
      animation: slide-in 0.3s ease-out;
    }
  `,
})
export class ToastContainerComponent {
  readonly notificationService = inject(NotificationService);

  getStyle(type: ToastMessage['type']): string {
    return TYPE_STYLES[type];
  }
}
