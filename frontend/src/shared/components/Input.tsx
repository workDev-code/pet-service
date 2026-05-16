import { InputHTMLAttributes } from 'react';
import { clsx } from 'clsx';

type Props = InputHTMLAttributes<HTMLInputElement> & {
  label: string;
  error?: string;
};

export function Input({ label, error, className, ...props }: Props) {
  return (
    <label className="grid gap-1.5 text-sm font-medium text-slate-700">
      {label}
      <input
        className={clsx(
          'h-10 rounded-md border border-slate-300 bg-white px-3 text-sm outline-none focus:border-brand-600 focus:ring-2 focus:ring-brand-100',
          className,
        )}
        {...props}
      />
      {error ? <span className="text-xs font-normal text-rose-600">{error}</span> : null}
    </label>
  );
}
