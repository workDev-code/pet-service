import { forwardRef, SelectHTMLAttributes } from 'react';
import type { FieldError } from 'react-hook-form';
import { clsx } from 'clsx';

type SelectError = string | FieldError | undefined;

export type SelectProps = SelectHTMLAttributes<HTMLSelectElement> & {
  label: string;
  error?: SelectError;
};

function getErrorMessage(error: SelectError) {
  if (!error) {
    return undefined;
  }

  if (typeof error === 'string') {
    return error;
  }

  return error.message;
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(function Select(
  { label, error, className, children, ...props },
  ref,
) {
  const errorMessage = getErrorMessage(error);

  return (
    <label className="grid gap-1.5 text-sm font-medium text-slate-700">
      {label}
      <select
        className={clsx(
          'h-10 rounded-md border border-slate-300 bg-white px-3 text-sm outline-none focus:border-brand-600 focus:ring-2 focus:ring-brand-100',
          className,
        )}
        ref={ref}
        {...props}
      >
        {children}
      </select>
      {errorMessage ? <span className="text-xs font-normal text-rose-600">{errorMessage}</span> : null}
    </label>
  );
});

Select.displayName = 'Select';
