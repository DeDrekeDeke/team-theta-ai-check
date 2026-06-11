import { Cv } from '../cvApi';

type CvPreviewProps = {
  cv: Cv;
};

function dateRange(startDate?: string | null, endDate?: string | null) {
  if (startDate && endDate) {
    return `${startDate} - ${endDate}`;
  }
  if (startDate) {
    return `${startDate} - Present`;
  }
  if (endDate) {
    return endDate;
  }
  return '';
}

function safeHttpUrl(url: string) {
  try {
    const parsedUrl = new URL(url);
    return parsedUrl.protocol === 'http:' || parsedUrl.protocol === 'https:' ? parsedUrl.toString() : '';
  } catch {
    return '';
  }
}

export function CvPreview({ cv }: CvPreviewProps) {
  const personalDetails = cv.personalDetails;

  return (
    <div className="cv-preview">
      <h2>{personalDetails?.fullName || cv.title}</h2>
      {personalDetails?.headline ? <p>{personalDetails.headline}</p> : null}
      {personalDetails ? (
        <p className="muted">
          {[personalDetails.email, personalDetails.phone, personalDetails.location].filter(Boolean).join(' | ')}
        </p>
      ) : null}

      {cv.summary ? (
        <section>
          <h3>Summary</h3>
          <p>{cv.summary}</p>
        </section>
      ) : null}

      {cv.workExperienceEntries.length > 0 ? (
        <section>
          <h3>Work experience</h3>
          {cv.workExperienceEntries.map((entry) => (
            <article key={entry.id ?? `${entry.employer}-${entry.displayOrder}`}>
              <h4>{entry.jobTitle}</h4>
              <p>
                {entry.employer}
                {entry.location ? `, ${entry.location}` : ''}
              </p>
              {dateRange(entry.startDate, entry.endDate) ? <p className="muted">{dateRange(entry.startDate, entry.endDate)}</p> : null}
              {entry.description ? <p>{entry.description}</p> : null}
            </article>
          ))}
        </section>
      ) : null}

      {cv.educationEntries.length > 0 ? (
        <section>
          <h3>Education</h3>
          {cv.educationEntries.map((entry) => (
            <article key={entry.id ?? `${entry.institution}-${entry.displayOrder}`}>
              <h4>{entry.institution}</h4>
              {[entry.degree, entry.fieldOfStudy].filter(Boolean).length > 0 ? (
                <p>{[entry.degree, entry.fieldOfStudy].filter(Boolean).join(', ')}</p>
              ) : null}
              {dateRange(entry.startDate, entry.endDate) ? <p className="muted">{dateRange(entry.startDate, entry.endDate)}</p> : null}
              {entry.description ? <p>{entry.description}</p> : null}
            </article>
          ))}
        </section>
      ) : null}

      {cv.skills.length > 0 ? (
        <section>
          <h3>Skills</h3>
          <p>{cv.skills.map((skill) => [skill.name, skill.proficiency].filter(Boolean).join(' - ')).join(', ')}</p>
        </section>
      ) : null}

      {cv.languages.length > 0 ? (
        <section>
          <h3>Languages</h3>
          <p>{cv.languages.map((language) => [language.name, language.proficiency].filter(Boolean).join(' - ')).join(', ')}</p>
        </section>
      ) : null}

      {cv.links.length > 0 ? (
        <section>
          <h3>Links</h3>
          <ul>
            {cv.links.map((link) => {
              const href = safeHttpUrl(link.url);
              return (
                <li key={link.id ?? `${link.url}-${link.displayOrder}`}>
                  {href ? (
                    <a href={href} rel="noreferrer" target="_blank">
                      {link.label}
                    </a>
                  ) : (
                    link.label
                  )}
                </li>
              );
            })}
          </ul>
        </section>
      ) : null}
    </div>
  );
}
