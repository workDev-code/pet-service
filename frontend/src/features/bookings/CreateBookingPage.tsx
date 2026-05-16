import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { z } from 'zod';
import { Button } from '../../shared/components/Button';
import { Input } from '../../shared/components/Input';
import { Select } from '../../shared/components/Select';
import { formatCurrency } from '../../shared/utils/format';
import { listPets } from '../pets/petsApi';
import { createBooking } from './bookingsApi';
import { listServices } from './servicesApi';

const schema = z.object({
  petId: z.coerce.number().min(1),
  serviceId: z.coerce.number().min(1),
  scheduledAt: z.string().min(1),
  address: z.string().min(5),
  notes: z.string().optional(),
});

type FormValues = z.infer<typeof schema>;

export function CreateBookingPage() {
  const navigate = useNavigate();
  const { data: pets = [] } = useQuery({ queryKey: ['pets'], queryFn: listPets });
  const { data: services = [] } = useQuery({ queryKey: ['services'], queryFn: listServices });
  const form = useForm<FormValues>({ resolver: zodResolver(schema) });
  const mutation = useMutation({
    mutationFn: (values: FormValues) => createBooking({
      ...values,
      scheduledAt: new Date(values.scheduledAt).toISOString(),
    }),
    onSuccess: (booking) => navigate(`/bookings/${booking.id}`),
  });

  return (
    <section className="max-w-2xl">
      <h2 className="mb-4 text-xl font-semibold text-slate-900">Create Booking</h2>
      <form className="grid gap-4 rounded-md border border-slate-200 bg-white p-5" onSubmit={form.handleSubmit((values) => mutation.mutate(values))}>
        <Select label="Pet" error={form.formState.errors.petId?.message} {...form.register('petId')}>
          <option value="">Select pet</option>
          {pets.map((pet) => <option key={pet.id} value={pet.id}>{pet.name} · {pet.species}</option>)}
        </Select>
        <Select label="Service" error={form.formState.errors.serviceId?.message} {...form.register('serviceId')}>
          <option value="">Select service</option>
          {services.map((service) => (
            <option key={service.id} value={service.id}>
              {service.name} · {formatCurrency(service.price)} · {service.durationMinutes} min
            </option>
          ))}
        </Select>
        <Input label="Scheduled at" type="datetime-local" error={form.formState.errors.scheduledAt?.message} {...form.register('scheduledAt')} />
        <Input label="Address" error={form.formState.errors.address?.message} {...form.register('address')} />
        <Input label="Notes" error={form.formState.errors.notes?.message} {...form.register('notes')} />
        {mutation.error ? <p className="text-sm text-rose-600">Could not create booking.</p> : null}
        <Button className="w-fit" type="submit" disabled={mutation.isPending}>Create booking</Button>
      </form>
    </section>
  );
}
