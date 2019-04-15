# Argument Setter

[![cm-available](https://cdap-users.herokuapp.com/assets/cm-available.svg)](https://docs.cdap.io/cdap/current/en/integrations/cask-market.html)
[![CDAP Action](cdap-users.herokuapp.com/assets/cdap-action.svg)](https://docs.cdap.io/cdap)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Join CDAP community](https://cdap-users.herokuapp.com/badge.svg?t=wrangler)](https://cdap-users.herokuapp.com?t=1)

Argument Setter is a type of Action plugin that allows one to create reusable pipelines by dynamically substituting the configurations that can be served by an HTTP Server. It uses the Macro capabilities provided by CDAP.

## Usage

Following is a simple example of a configuration that can be served by HTTP server to this plugin.

```
{
  "arguments" : [
    { "name" : "input.path", "type" : "string", "value" : "/Users/nitin/Work/Demo/data/titanic.xlsx" },
    { "name" : "rulebook", "type" : "import", "value" : "http://localhost:11015/v3/namespaces/default/apps/yare/services/service/rulebook/MyRuleBook" },
    { "name" : "parser", "type" : "array", "value" :
      [
        "parse-as-excel body , true",
        "drop body",
        "set columns PassengerId,Survived,Pclass,Name,Sex,Age,SibSp,Parch,Ticket,Fare,Cabin,Embarked",
        "filter-row-if-matched Age Age"
        "cut-character Sex Sex 1",
        "uppercase Sex",
        "rename Sex Gender",
        "keep PassengerId, Name",
        "fill-null-or-empty Age 0"
      ]
    },
    {
      "name" : "output.schema", "type" : "schema", "value" :
      [
        { "name" : "PassengerId", "type" : "string", "nullable" : true},
        { "name" : "Name", "type" : "string", "nullable" : true}
      ]
    },
    { "name" : "dq.checks", "type" : "array", "value" :
      [
        "filter-rows-on regex-match Name .*James.*"
      ]
    },
    { "name" : "stage.path", "type" : "string", "value" : "titanic_excel"}
  ]
}
```

The configuration is made up of collection of `Arguments` and `Arguments` defines all the configurations for one or more pipelines qualified by macro names.

```
{
    "arguments" : [
        { argument }, { argument }, ..., {argument}
    ]
}
```

Each `argument` is made up of following fields

* **Name** -- Defines the name of the argument or macro name.
* **Type** -- Defines the type of argument. It can be either int, float, double, short, string, schema, char, array or map. &
* **Value** -- Defines the value based on the type.

### Argument Types

Following are the argument types supported by the system:

| Argument Type | Type Description |
| ------------- | ---------------- |
| import | Defines the type that imports the value using a HTTP invocation |
| schema | Defines the value to be of schema type that is defined as combination of `name`, `type` and `nullable` |
| int | Defines the value as integer type |
| float | Defines the value as float type |
| double | Defines the value as double type |
| short | Defines the value as short type |
| string | Defines the value as string type |
| char | Defines the value as char type |
| array | Defines the value as array of string type |
| map | Defines the value of map |
