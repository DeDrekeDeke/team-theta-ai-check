import { FormField, TextInput } from '../../../components/FormField';
import { CvPersonalDetails } from '../cvApi';

type PersonalDetailsFormProps = {
  value: CvPersonalDetails;
  onChange: (value: CvPersonalDetails) => void;
};

export function emptyPersonalDetails(): CvPersonalDetails {
  return {
    fullName: '',
    email: '',
    phone: '',
    location: '',
    headline: ''
  };
}

export function PersonalDetailsForm({ value, onChange }: PersonalDetailsFormProps) {
  function update(field: keyof CvPersonalDetails, nextValue: string) {
    onChange({ ...value, [field]: nextValue });
  }

  return (
    <div className="form-stack">
      <div className="form-grid">
        <FormField label="Full name" htmlFor="personal-full-name">
          <TextInput
            id="personal-full-name"
            value={value.fullName ?? ''}
            onChange={(event) => update('fullName', event.target.value)}
          />
        </FormField>
        <FormField label="Email" htmlFor="personal-email">
          <TextInput
            id="personal-email"
            type="email"
            value={value.email ?? ''}
            onChange={(event) => update('email', event.target.value)}
          />
        </FormField>
        <FormField label="Phone" htmlFor="personal-phone">
          <TextInput
            id="personal-phone"
            value={value.phone ?? ''}
            onChange={(event) => update('phone', event.target.value)}
          />
        </FormField>
      </div>
      <div className="form-grid">
        <FormField label="Location" htmlFor="personal-location">
          <TextInput
            id="personal-location"
            value={value.location ?? ''}
            onChange={(event) => update('location', event.target.value)}
          />
        </FormField>
        <FormField label="Headline" htmlFor="personal-headline">
          <TextInput
            id="personal-headline"
            value={value.headline ?? ''}
            onChange={(event) => update('headline', event.target.value)}
          />
        </FormField>
      </div>
    </div>
  );
}
