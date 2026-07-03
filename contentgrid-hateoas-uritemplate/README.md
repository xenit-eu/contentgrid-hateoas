# ContentGrid HATEOAS URI template

Extensions to [RFC6570](https://www.rfc-editor.org/info/rfc6570/) URI templates that allow for additional substitution variables,
which are replaced server-side before the URI template is sent to the client.

## Usage

A parameterized URI template is typically expressed as a string that represents it.

On top of the RFC6570 URI template variables, _substitution variables_ are defined that are expressed as a name between the delimiters `%{` and `}`.

For example: the parameterized URI template `https://example.com/%{subst-var}/{uri_var}` defines both a substitution variable named `subst-var` and a URI template variable `uri_var`.

Substitution variables have to be statically defined by constructing an enum that implements the [`SubstitutionVariableDefinition`] interface.

These can then be used in a [`ParameterizedUriTemplateParser`], which can parse a string to a [`ParameterizedUriTemplate`].
The [`ParameterizedUriTemplate`] can then be expanded to a standard URI template string by having all substitution variables replaced using a [`ParameterReplacer`].

`SubstitutionVariableDefinition`: src/main/java/com/contentgrid/hateoas/uritemplate/SubstitutionVariableDefinition.java
`ParameterizedUriTemplateParser`: src/main/java/com/contentgrid/hateoas/uritemplate/ParameterizedUriTemplateParser.java
`ParameterizedUriTemplate`: src/main/java/com/contentgrid/hateoas/uritemplate/ParameterizedUriTemplate.java
`ParameterReplacer`: src/main/java/com/contentgrid/hateoas/uritemplate/ParameterReplacer.java


Note that lombok is used to omit tedious operations.

```java
@RequiredArgsConstructor
@Getter
enum TestParams implements SubstitutionVariableDefinition {
    APPLICATION_ID("application.id"),
    APPLICATION_NAME("application.name"),
    DOMAIN_NAME("domain_name");
    
    private final String name;
}

// Create a parser. Note that it is possible to allow only a subset of parameters as well
var parser = new ParameterizedUriTemplateParser<>(EnumSet.allOf(TestParams.class));

// Parses a string into a ParameterizedUriTemplate
var parameterizedTemplate = parser.parse("https://%{domain_name}/applications/%{application.id}{?p}");

// Expand the template by replacing substitution variables
var uriTemplate = parameterizedTemplate.expand(variable -> switch(variable) {
    case APPLICATION_ID -> "app-123";
    case APPLICATION_NAME -> "my-app";
    case DOMAIN_NAME -> "app-lookup.example";
});
// --> https://app-lookup.example/applications/app-123{?p}

```
