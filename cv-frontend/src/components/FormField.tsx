import { InputHTMLAttributes, PropsWithChildren } from 'react';

type FormFieldProps = PropsWithChildren<{
  label: string;
  htmlFor: string;
}>;

export function FormField({ label, htmlFor, children }: FormFieldProps) {
  return (
    <label className="form-field" htmlFor={htmlFor}>
      <span>{label}</span>
      {children}
    </label>
  );
}

export function TextInput(props: InputHTMLAttributes<HTMLInputElement>) {
  return <input className="text-input" {...props} />;
}
