import { Button } from '../../../components/Button';
import { FormField, TextInput } from '../../../components/FormField';
import { CvSkill } from '../cvApi';

type SkillsFormProps = {
  skills: CvSkill[];
  onChange: (skills: CvSkill[]) => void;
};

export function emptySkill(displayOrder: number): CvSkill {
  return {
    name: '',
    category: '',
    proficiency: '',
    displayOrder
  };
}

export function SkillsForm({ skills, onChange }: SkillsFormProps) {
  function updateSkill(index: number, patch: Partial<CvSkill>) {
    onChange(skills.map((skill, skillIndex) => (skillIndex === index ? { ...skill, ...patch } : skill)));
  }

  return (
    <div className="form-stack">
      {skills.map((skill, index) => (
        <div className="form-grid" key={skill.id ?? index}>
          <FormField label="Skill" htmlFor={`skill-${index}-name`}>
            <TextInput
              id={`skill-${index}-name`}
              value={skill.name}
              onChange={(event) => updateSkill(index, { name: event.target.value })}
            />
          </FormField>
          <FormField label="Category" htmlFor={`skill-${index}-category`}>
            <TextInput
              id={`skill-${index}-category`}
              value={skill.category ?? ''}
              onChange={(event) => updateSkill(index, { category: event.target.value })}
            />
          </FormField>
          <FormField label="Proficiency" htmlFor={`skill-${index}-proficiency`}>
            <TextInput
              id={`skill-${index}-proficiency`}
              value={skill.proficiency ?? ''}
              onChange={(event) => updateSkill(index, { proficiency: event.target.value })}
            />
          </FormField>
          <div className="inline-actions end">
            <Button type="button" variant="secondary" onClick={() => onChange(skills.filter((_, skillIndex) => skillIndex !== index))}>
              Remove
            </Button>
          </div>
        </div>
      ))}
      <div className="inline-actions end">
        <Button type="button" variant="secondary" onClick={() => onChange([...skills, emptySkill(skills.length)])}>
          Add skill
        </Button>
      </div>
    </div>
  );
}
