import { Button } from '../../../components/Button';
import { FormField, TextInput } from '../../../components/FormField';
import { CvWorkExperienceEntry } from '../cvApi';

type WorkExperienceEntriesFormProps = {
  entries: CvWorkExperienceEntry[];
  onChange: (entries: CvWorkExperienceEntry[]) => void;
};

export function emptyWorkExperienceEntry(displayOrder: number): CvWorkExperienceEntry {
  return {
    employer: '',
    jobTitle: '',
    location: '',
    startDate: null,
    endDate: null,
    description: '',
    displayOrder
  };
}

export function WorkExperienceEntriesForm({ entries, onChange }: WorkExperienceEntriesFormProps) {
  function updateEntry(index: number, patch: Partial<CvWorkExperienceEntry>) {
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
            <h3>Work experience</h3>
            <Button type="button" variant="secondary" onClick={() => removeEntry(index)}>
              Remove
            </Button>
          </div>
          <div className="form-stack">
            <div className="form-grid">
              <FormField label="Employer" htmlFor={`work-${index}-employer`}>
                <TextInput
                  id={`work-${index}-employer`}
                  value={entry.employer}
                  onChange={(event) => updateEntry(index, { employer: event.target.value })}
                />
              </FormField>
              <FormField label="Job title" htmlFor={`work-${index}-job-title`}>
                <TextInput
                  id={`work-${index}-job-title`}
                  value={entry.jobTitle}
                  onChange={(event) => updateEntry(index, { jobTitle: event.target.value })}
                />
              </FormField>
              <FormField label="Location" htmlFor={`work-${index}-location`}>
                <TextInput
                  id={`work-${index}-location`}
                  value={entry.location ?? ''}
                  onChange={(event) => updateEntry(index, { location: event.target.value })}
                />
              </FormField>
            </div>
            <div className="form-grid">
              <FormField label="Start date" htmlFor={`work-${index}-start`}>
                <TextInput
                  id={`work-${index}-start`}
                  type="date"
                  value={entry.startDate ?? ''}
                  onChange={(event) => updateEntry(index, { startDate: event.target.value || null })}
                />
              </FormField>
              <FormField label="End date" htmlFor={`work-${index}-end`}>
                <TextInput
                  id={`work-${index}-end`}
                  type="date"
                  value={entry.endDate ?? ''}
                  onChange={(event) => updateEntry(index, { endDate: event.target.value || null })}
                />
              </FormField>
            </div>
            <label className="form-field" htmlFor={`work-${index}-description`}>
              <span>Description</span>
              <textarea
                id={`work-${index}-description`}
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
        <Button type="button" variant="secondary" onClick={() => onChange([...entries, emptyWorkExperienceEntry(entries.length)])}>
          Add work experience
        </Button>
      </div>
    </div>
  );
}
