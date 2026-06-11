import { Button } from '../../../components/Button';
import { FormField, TextInput } from '../../../components/FormField';
import { CvEducationEntry } from '../cvApi';

type EducationEntriesFormProps = {
  entries: CvEducationEntry[];
  onChange: (entries: CvEducationEntry[]) => void;
};

export function emptyEducationEntry(displayOrder: number): CvEducationEntry {
  return {
    institution: '',
    degree: '',
    fieldOfStudy: '',
    startDate: null,
    endDate: null,
    description: '',
    displayOrder
  };
}

export function EducationEntriesForm({ entries, onChange }: EducationEntriesFormProps) {
  function updateEntry(index: number, patch: Partial<CvEducationEntry>) {
    onChange(entries.map((entry, entryIndex) => (entryIndex === index ? { ...entry, ...patch } : entry)));
  }

  function removeEntry(index: number) {
    onChange(entries.filter((_, entryIndex) => entryIndex !== index));
  }

  return (
    <div className="form-stack">
      {entries.map((entry, index) => (
        <div className="panel" key={entry.id ?? index}>
          <div className="panel-header">
            <h3>Education</h3>
            <Button type="button" variant="secondary" onClick={() => removeEntry(index)}>
              Remove
            </Button>
          </div>
          <div className="form-stack">
            <div className="form-grid">
              <FormField label="Institution" htmlFor={`education-${index}-institution`}>
                <TextInput
                  id={`education-${index}-institution`}
                  value={entry.institution}
                  onChange={(event) => updateEntry(index, { institution: event.target.value })}
                />
              </FormField>
              <FormField label="Degree" htmlFor={`education-${index}-degree`}>
                <TextInput
                  id={`education-${index}-degree`}
                  value={entry.degree ?? ''}
                  onChange={(event) => updateEntry(index, { degree: event.target.value })}
                />
              </FormField>
              <FormField label="Field of study" htmlFor={`education-${index}-field`}>
                <TextInput
                  id={`education-${index}-field`}
                  value={entry.fieldOfStudy ?? ''}
                  onChange={(event) => updateEntry(index, { fieldOfStudy: event.target.value })}
                />
              </FormField>
            </div>
            <div className="form-grid">
              <FormField label="Start date" htmlFor={`education-${index}-start`}>
                <TextInput
                  id={`education-${index}-start`}
                  type="date"
                  value={entry.startDate ?? ''}
                  onChange={(event) => updateEntry(index, { startDate: event.target.value || null })}
                />
              </FormField>
              <FormField label="End date" htmlFor={`education-${index}-end`}>
                <TextInput
                  id={`education-${index}-end`}
                  type="date"
                  value={entry.endDate ?? ''}
                  onChange={(event) => updateEntry(index, { endDate: event.target.value || null })}
                />
              </FormField>
            </div>
            <label className="form-field" htmlFor={`education-${index}-description`}>
              <span>Description</span>
              <textarea
                id={`education-${index}-description`}
                className="text-input"
                rows={4}
                value={entry.description ?? ''}
                onChange={(event) => updateEntry(index, { description: event.target.value })}
              />
            </label>
          </div>
        </div>
      ))}
      <div className="inline-actions end">
        <Button type="button" variant="secondary" onClick={() => onChange([...entries, emptyEducationEntry(entries.length)])}>
          Add education
        </Button>
      </div>
    </div>
  );
}
