CREATE EXTENSION IF NOT EXISTS unaccent;

-- Função converter texto para minúsculo e sem acento.
CREATE OR REPLACE FUNCTION public.lower_unaccent(input character varying)
 RETURNS character varying
 LANGUAGE plpgsql
AS $function$
begin
	return lower(unaccent(input));
end;
$function$;