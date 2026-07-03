package com.contentgrid.hateoas.uritemplate;

public interface ParameterReplacer<S extends Enum<S> & SubstitutionVariableDefinition> {
    String replace(S substitutionVariable);

}
