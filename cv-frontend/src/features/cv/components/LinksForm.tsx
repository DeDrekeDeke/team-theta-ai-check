import { Button } from '../../../components/Button';
import { FormField, TextInput } from '../../../components/FormField';
import { CvLink } from '../cvApi';

type LinksFormProps = {
  links: CvLink[];
  onChange: (links: CvLink[]) => void;
};

export function emptyLink(displayOrder: number): CvLink {
  return {
    label: '',
    url: '',
    displayOrder
  };
}

export function LinksForm({ links, onChange }: LinksFormProps) {
  function updateLink(index: number, patch: Partial<CvLink>) {
    onChange(links.map((link, linkIndex) => (linkIndex === index ? { ...link, ...patch } : link)));
  }

  return (
    <div className="form-stack">
      {links.map((link, index) => (
        <div className="form-grid" key={link.id ?? index}>
          <FormField label="Label" htmlFor={`link-${index}-label`}>
            <TextInput
              id={`link-${index}-label`}
              value={link.label}
              onChange={(event) => updateLink(index, { label: event.target.value })}
            />
          </FormField>
          <FormField label="URL" htmlFor={`link-${index}-url`}>
            <TextInput
              id={`link-${index}-url`}
              type="url"
              value={link.url}
              onChange={(event) => updateLink(index, { url: event.target.value })}
            />
          </FormField>
          <div className="inline-actions end">
            <Button type="button" variant="secondary" onClick={() => onChange(links.filter((_, linkIndex) => linkIndex !== index))}>
              Remove
            </Button>
          </div>
        </div>
      ))}
      <div className="inline-actions end">
        <Button type="button" variant="secondary" onClick={() => onChange([...links, emptyLink(links.length)])}>
          Add link
        </Button>
      </div>
    </div>
  );
}
