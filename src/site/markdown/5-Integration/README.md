# Integration

```mermaid
C4Component
    
    Person(tester, "Tester") 
    Component(jenkins, "Jenkins")
    Rel(tester, jenkins, "invokes a job manually")
```