select CONCEPT_ID, CONCEPT_NAME, ISNULL(STANDARD_CONCEPT,'N') STANDARD_CONCEPT, ISNULL(INVALID_REASON,'V') INVALID_REASON, CONCEPT_CODE, CONCEPT_CLASS_ID, DOMAIN_ID, VOCABULARY_ID
from @CDM_schema.CONCEPT 
where (LOWER(CONCEPT_NAME) LIKE '%@query%' or LOWER(CONCEPT_CODE) LIKE '%@query%' or CAST(CONCEPT_ID as VARCHAR(255)) = '@query')
@filters
order by CONCEPT_NAME ASC
