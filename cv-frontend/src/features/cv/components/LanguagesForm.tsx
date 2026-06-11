import { Button } from '../../../components/Button';
import { FormField, TextInput } from '../../../components/FormField';
import { CvLanguage } from '../cvApi';

type LanguagesFormProps = {
  languages: CvLanguage[];
  onChange: (languages: CvLanguage[]) => void;
};

export function emptyLanguage(displayOrder: number): CvLanguage {
  return {
    name: '',
    proficiency: '',
    displayOrder
  };
}

export function LanguagesForm({ languages, onChange }: LanguagesFormProps) {
  function updateLanguage(index: number, patch: Partial<CvLanguage>) {
    onChange(languages.map((language, languageIndex) => (languageIndex === index ? { ...language, ...patch } : language)));
  }

  return (
    <div className="form-stack">
      {languages.map((language, index) => (
        <div className="form-grid" key={language.id ?? index}>
          <FormField label="Language" htmlFor={`language-${index}-name`}>
            <TextInput
              id={`language-${index}-name`}
              value={language.name}
              onChange={(event) => updateLanguage(index, { name: event.target.value })}
            />
          </FormField>
          <FormField label="Proficiency" htmlFor={`language-${index}-proficiency`}>
            <TextInput
              id={`language-${index}-proficiency`}
              value={language.proficiency ?? ''}
              onChange={(event) => updateLanguage(index, { proficiency: event.target.value })}
            />
          </FormField>
          <div className="inline-actions end">
            <Button type="button" variant="secondary" onClick={() => onChange(languages.filter((_, languageIndex) => languageIndex !== index))}>
              Remove
            </Button>
          </div>
        </div>
      ))}
      <div className="inline-actions end">
        <Button type="button" variant="secondary" onClick={() => onChange([...languages, emptyLanguage(languages.length)])}>
          Add language
        </Button>
      </div>
    </div>
  );
}
