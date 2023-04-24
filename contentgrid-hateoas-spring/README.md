# ContentGrid HATEOAS Spring

Extensions to [Spring HATEOAS](https://github.com/spring-projects/spring-hateoas)

## Affordances

An extension to make configuring custom affordances easier.

The two main entrypoints for this extension are:

 * [`AffordanceCustomizer#afford()`](src/main/java/com/contentgrid/hateoas/spring/affordances/AffordanceCustomizer.java)
   for creating and customizing a single `Affordance`
 * [`Affordances#of()`](src/main/java/com/contentgrid/hateoas/spring/affordances/Affordances.java) for working with multiple affordances.

### `AffordanceCustomizer`

The builtin Spring HATEOAS library only supports either creating affordances from a controller method (then it is no longer customizable),
or manually (then you have to set all parameters by hand and make sure they match an existing controller).

The `AffordanceCustomizer` provides a middle road by creating a `CustomizableAffordance` from the controller method,
allowing you to further modify it as necessary.

```java
AffordanceCustomizer.afford(methodOn(EmployeeController.class).updateEmployee(123, null))
        .configure(configurableAffordance -> configureableAffordance.withName("update"))
        .build();
```


### `Affordances`

Where `AffordanceCustomizer` allows modifying a single affordance, `Affordances` extends this for handling multiple affordances.

It provides an alternative to the Spring HATEOAS `Link` and `Affordances` API for adding affordances.

There are a couple of ways that `Affordances` can be used:

<ol>
<li>
<details>
<summary>
Using <code>#additionally()</code> to create additional affordances based on a common type.

This would mostly be used to support backwards compatibility, by exposing an affordance with the old name in addition to the affordance with a new name.
</summary>

```java
var affordances = Affordances.of(methodOn(EmployeeController.class).updateEmployee(123, null))
        .configure(configurableAffordance -> configurableAffordance.withInputMediatype(...))
        .additionally(affordanceCustomizer -> {
        // This creates a separate affordance, which also includes all settings made with `#configure()` above.
        return affordanceCustomizer.wothName("update");
        })
        // .onlyAdditional() // This avoids the base affordance being added
        .stream() // This builds the Affordance objects to add to a Link object
        .toList();

var link = linkTo(methodOn(EmployeeController.class).getEmployee(123)).withSelfRel()
        .andAffordances(affordances);
```

</details>

</li>

<li>
<details>
<summary>
Replacing the Spring HATEOAS <code>Link</code> API.

Using `#andAffordance()`/`#andAffordances()` to add additional affordance(s) that are not related to the initial affordance,
and then creating a link with all affordances using `toLink()`.
</summary>

```java
var link = Affordances.of(methodOn(EmployeeController.class).getEmployee(123))
        .andAffordance(
                afford(methodOn(EmployeeController.class).updateEmployee(123, null))
                    .configure(configurableAffordance -> configurableAffordance.withName("update"))
        )
        .toLink()
        .withSelfRel();
```

</details>
</li>

<li>
<details>
<summary>
Automatically setting up a <code>default</code> HAL-FORMS <code>_template</code>.

When using HAL-FORMS, and you only want to use custom-named affordances, you can use `#withDefault()` to automatically set
up a `_templates.default` HAL-FORMS template first, so all your named affordances are exposed as a template of their name.

Note that this only affects the HAL-FORMS mediatype and does not affect other mediatypes that support affordances.
</summary>

```java
var link = Affordances.of(methodOn(EmployeeController.class).getEmployee(123))
        .withDefault()
        .andAffordance(
           afford(methodOn(EmployeeController.class).updateEmployee(123, null))
               .configure(configurableAffordance -> configurableAffordance.withName("update"))
        )
        .toLink()
        .withSelfRel();
```

</details>
</li>

</ol>