import { apiClient } from '../../shared/api/apiClient';
import { BookingResponse, BookingStatus } from '../../shared/types/domain';

export interface CreateBookingPayload {
  petId: number;
  serviceId: number;
  scheduledAt: string;
  address: string;
  notes?: string;
}

export async function listBookings() {
  const { data } = await apiClient.get<BookingResponse[]>('/bookings');
  return data;
}

export async function getBooking(id: number) {
  const { data } = await apiClient.get<BookingResponse>(`/bookings/${id}`);
  return data;
}

export async function createBooking(payload: CreateBookingPayload) {
  const { data } = await apiClient.post<BookingResponse>('/bookings', payload);
  return data;
}

export async function assignBooking(id: number, staffId: number) {
  const { data } = await apiClient.patch<BookingResponse>(`/bookings/${id}/assign`, { staffId });
  return data;
}

export async function updateBookingStatus(id: number, status: BookingStatus) {
  const { data } = await apiClient.patch<BookingResponse>(`/bookings/${id}/status`, { status });
  return data;
}
